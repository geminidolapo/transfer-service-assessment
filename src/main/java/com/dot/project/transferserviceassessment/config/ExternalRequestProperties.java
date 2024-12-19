package com.dot.project.transferserviceassessment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Data
@ConfigurationProperties(prefix = "app")
@ConfigurationPropertiesScan("com.dot.project")
public class ExternalRequestProperties {
    private String feePercentage;  // Default to 0.5%
    private String commissionPercentage; // Default to 0.2%
    private String feeCap; // Default cap at 100
}
