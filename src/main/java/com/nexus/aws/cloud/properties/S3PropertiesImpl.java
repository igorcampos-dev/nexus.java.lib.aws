package com.nexus.aws.cloud.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.aws.credentials")
public class S3PropertiesImpl implements S3Properties {
    private String serviceEndpoint;
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;
}
