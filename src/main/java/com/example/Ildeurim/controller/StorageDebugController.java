package com.example.Ildeurim.controller;


import com.example.Ildeurim.config.NcpStorageProps;
import com.example.Ildeurim.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CommonsLog
@RestController
@RequiredArgsConstructor
@RequestMapping("/debug/storage")
public class StorageDebugController {

    private final ObjectStorageService storage;
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
}
