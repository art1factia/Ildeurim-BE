package com.example.Ildeurim.controller;


import com.example.Ildeurim.config.NcpStorageProps;
import com.example.Ildeurim.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Map;

@CommonsLog
@RestController
@RequiredArgsConstructor
@RequestMapping("/debug/storage")
public class StorageDebugController {

    private final ObjectStorageService storage;
    private final S3Client s3;
    private final NcpStorageProps props;

    @PostMapping("/ping-upload")
    public ResponseEntity<?> pingUpload() {
        log.info("Bucket="+ props.getBucket()+ "Endpoint=" + props.getEndpoint());

        try {
            String key = storage.putPingObject();
            return ResponseEntity.ok(Map.of(
                    "ok", true,
                    "key", key
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "ok", false,
                    "error", e.getClass().getSimpleName() + ": " + e.getMessage()
            ));
        }
    }
    @PostMapping("/probe")
    public ResponseEntity<?> probe() {
        try {
            // 1) 버킷 접근 가능한지
            s3.headBucket(b -> b.bucket(props.getBucket()));

            // 2) 가장 보수적인 PutObject (ACL/암호화/메타데이터 없음)
            String key = "ping/" + System.currentTimeMillis() + ".txt";
            s3.putObject(b -> b.bucket(props.getBucket()).key(key),
                    software.amazon.awssdk.core.sync.RequestBody.fromString("ping"));

            return ResponseEntity.ok(Map.of("ok", true, "key", key));
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "ok", false,
                    "code", e.awsErrorDetails().errorCode(),
                    "msg", e.awsErrorDetails().errorMessage(),
                    "status", e.statusCode(),
                    "requestId", e.requestId()
            ));
        }
    }


}
