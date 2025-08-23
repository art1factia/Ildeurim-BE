package com.example.Ildeurim.config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "ncp.storage")
public class NcpStorageProps {
    /** 예: ildeurim-bucket */
    private String bucket;
    /** 예: https://kr.object.ncloudstorage.com */
    private String endpoint;
    /** 예: kr-standard (필요시) */
    private String region;
}
