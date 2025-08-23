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

import java.net.URI;
import java.time.Instant;
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
        System.out.println("Put OK: " + keyy);


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
}
