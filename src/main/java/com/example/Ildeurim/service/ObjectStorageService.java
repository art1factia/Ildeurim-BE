package com.example.Ildeurim.service;

import com.example.Ildeurim.config.NcpStorageProps;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/webp"
    );
    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5MB

    private final S3Client s3;
    private final S3Presigner presigner;
    private final NcpStorageProps props;

    /** 워커 프로필 업로드: 기존 이미지가 있으면 삭제(선택) */
    public String uploadWorkerProfile(long workerId, MultipartFile file, String oldUrl) {
        validate(file);

        String contentType = file.getContentType();
        String ext = resolveExt(file, contentType); // jpg/png/webp 등
        String key = "workers/%d/profile-%d-%s.%s"
                .formatted(workerId, Instant.now().toEpochMilli(), shortId(), ext);
        // 업로드 (공개 읽기)
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .contentType(contentType)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        try {
            s3.putObject(req, RequestBody.fromBytes(file.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException("Object upload failed: " + e.getMessage(), e);
        }

        // 이전 파일 정리(선택)
        deleteIfMyBucketObject(oldUrl);

        // 공개 URL 생성 (bucket.subdomain 스타일)
        return publicUrl(key);
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        if (file.getSize() > MAX_BYTES)
            throw new IllegalArgumentException("파일이 5MB를 초과합니다.");
        String ct = file.getContentType();
        if (ct == null || !ALLOWED_TYPES.contains(ct))
            throw new IllegalArgumentException("허용되지 않는 이미지 형식입니다. (jpeg/png/webp)");
    }

    // ObjectStorageService.java
    public String putPingObject() {
        String keyy = "debug/ping-" + System.currentTimeMillis() + ".txt";
        PutObjectRequest reqq = PutObjectRequest.builder()
                .bucket(props.getBucket())
                .key(keyy)
                .contentType("text/plain")
                .build(); // ★ ACL 절대 넣지 않기

        s3.putObject(reqq, RequestBody.fromString("ping"));
//        System.out.println("Put OK: " + keyy);


        String key = "debug/ping-" + System.currentTimeMillis() + ".txt";
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .contentType("text/plain")
                 .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
        s3.putObject(req, RequestBody.fromString("ping"));
        return key;
    }

    private String resolveExt(MultipartFile file, String contentType) {
        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? StringUtils.getFilenameExtension(original).toLowerCase()
                : null;
        if (ext == null || ext.isBlank()) {
            // content-type 기반 fallback
            if (MediaType.IMAGE_JPEG_VALUE.equals(contentType)) return "jpg";
            if (MediaType.IMAGE_PNG_VALUE.equals(contentType)) return "png";
            if ("image/webp".equals(contentType)) return "webp";
            return "bin";
        }
        return switch (ext) {
            case "jpeg" -> "jpg";
            default -> ext;
        };
    }

    private String shortId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String publicUrl(String key) {
        // https://{bucket}.kr.object.ncloudstorage.com/{key}
        try {
            URI ep = new URI(props.getEndpoint());
            String host = ep.getHost(); // kr.object.ncloudstorage.com
            return "https://%s.%s/%s".formatted(props.getBucket(), host, key);
        } catch (Exception e) {
            // 엔드포인트 파싱 실패 시 path-style로 대체
            return props.getEndpoint().replaceAll("/+$","") + "/" + props.getBucket() + "/" + key;
        }
    }

    /** 우리 버킷의 객체라면 삭제 (선택 로직) */
    private void deleteIfMyBucketObject(String url) {
        if (url == null || url.isBlank()) return;
        try {
            // URL 패턴: https://{bucket}.host/{key}
            URI u = URI.create(url);
            String host = u.getHost(); // {bucket}.kr.object...
            String bucketPrefix = props.getBucket() + ".";
            if (host != null && host.startsWith(bucketPrefix)) {
                String key = u.getPath().replaceFirst("^/+", "");
                if (!key.isBlank()) {
                    s3.deleteObject(DeleteObjectRequest.builder()
                            .bucket(props.getBucket())
                            .key(key)
                            .build());
                }
            }
        } catch (Exception ignore) { /* 안전 무시 */ }
    }

    // --- 추가: 계약서 전용 제한 ---
    private static final Set<String> ALLOWED_CONTRACT_TYPES = Set.of(
            MediaType.APPLICATION_PDF_VALUE,
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/webp"
    );
    private static final long MAX_CONTRACT_BYTES = 10L * 1024 * 1024; // 10MB

    /** 근로계약서 업로드: 기존 파일이 우리 버킷이면 삭제 */
    public String uploadContract(long jobId, MultipartFile file, String oldUrl) {
        validateContract(file);

        final String contentType = Optional.ofNullable(file.getContentType())
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        final String ext = resolveContractExt(file, contentType);

        final String key = "jobs/%d/contracts/contract-%d-%s.%s"
                .formatted(jobId, Instant.now().toEpochMilli(), shortId(), ext);

        // 민감한 문서이므로 ACL(공개권한) 설정하지 않음 = private 객체
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .contentType(contentType)
                .contentDisposition("attachment; filename=\"contract-%d.%s\"".formatted(jobId, ext))
                .build();

        try {
            s3.putObject(req, RequestBody.fromBytes(file.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException("Contract upload failed: " + e.getMessage(), e);
        }

        // 이전 파일 정리(우리 버킷 소유일 때만)
        deleteIfMyBucketObject(oldUrl);

        // 비공개 객체라도 위치 식별을 위해 URL(경로)을 저장해 둠.
        // 실제 다운로드 제공은 presigned URL 생성 등으로 처리하면 안전.
        return publicUrl(key);
    }

    // --- 추가: 계약서 검증 ---
    private void validateContract(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        if (file.getSize() > MAX_CONTRACT_BYTES)
            throw new IllegalArgumentException("파일이 10MB를 초과합니다.");

        String ct = file.getContentType();
        String name = Optional.ofNullable(file.getOriginalFilename()).orElse("").toLowerCase();

        boolean allowedByCT = (ct != null && ALLOWED_CONTRACT_TYPES.contains(ct));
        boolean allowedByExt = name.endsWith(".pdf") || name.endsWith(".jpg")
                || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".webp");

        if (!(allowedByCT || allowedByExt)) {
            throw new IllegalArgumentException("허용되지 않는 계약서 형식입니다. (pdf/jpeg/png/webp)");
        }
    }

    // --- 추가: 계약서 확장자 판별 (pdf 포함) ---
    private String resolveContractExt(MultipartFile file, String contentType) {
        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? StringUtils.getFilenameExtension(original).toLowerCase()
                : null;

        if (ext == null || ext.isBlank()) {
            if (MediaType.APPLICATION_PDF_VALUE.equals(contentType)) return "pdf";
            if (MediaType.IMAGE_JPEG_VALUE.equals(contentType)) return "jpg";
            if (MediaType.IMAGE_PNG_VALUE.equals(contentType)) return "png";
            if ("image/webp".equals(contentType)) return "webp";
            return "bin";
        }
        return switch (ext) {
            case "jpeg" -> "jpg";
            default -> ext;
        };
    }

    /** 저장된 contractUrl(우리 버킷 URL)로부터, 짧게 유효한 다운로드 URL 발급 */
    public String presignContractDownload(String contractUrl, Duration ttl, String downloadName) {
        if (contractUrl == null || contractUrl.isBlank())
            throw new IllegalArgumentException("계약서가 없습니다.");

        // DB에 URL을 저장해뒀으므로 URL→key 로 변환
        String key = extractKeyFromOurUrl(contractUrl);
        if (key == null || key.isBlank())
            throw new IllegalArgumentException("잘못된 계약서 URL 형식입니다.");

        // 파일명 UTF-8 인코딩 (RFC 5987)
        String encoded = URLEncoder.encode(downloadName, StandardCharsets.UTF_8).replace("+", "%20");
        String contentDisposition = "attachment; filename*=UTF-8''" + encoded;

        GetObjectRequest get = GetObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .responseContentType("application/octet-stream")
                .responseContentDisposition(contentDisposition)
                .build();

        PresignedGetObjectRequest pre = presigner.presignGetObject(b -> b
                .getObjectRequest(get)
                .signatureDuration(ttl));

        return pre.url().toString();
    }

    /** https://{bucket}.{host}/{key} → {key} */
    private String extractKeyFromOurUrl(String url) {
        try {
            URI u = URI.create(url);
            String host = u.getHost(); // e.g. <bucket>.kr.object.ncloudstorage.com
            if (host != null && host.startsWith(props.getBucket() + ".")) {
                return u.getPath().replaceFirst("^/+", ""); // leading slash 제거
            }
            // path-style 등 다른 형식도 허용
            String prefix = props.getEndpoint().replaceAll("/+$","") + "/" + props.getBucket() + "/";
            if (url.startsWith(prefix)) {
                return url.substring(prefix.length());
            }
        } catch (Exception ignore) {}
        return null;
    }

}
