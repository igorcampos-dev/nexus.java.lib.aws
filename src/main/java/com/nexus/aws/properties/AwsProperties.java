package com.nexus.aws.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.aws.credentials")
public class AwsProperties {
    private String serviceEndpoint;
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;
}
