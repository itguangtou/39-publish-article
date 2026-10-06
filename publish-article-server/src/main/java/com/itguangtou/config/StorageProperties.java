package com.itguangtou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    private String baseDir = "data";
    private String publicPrefix = "/data/";
    private int maxWidth = 2048;
    private float webpQuality = 0.8f;
}
