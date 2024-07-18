package com.fyh.esi_test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fyh
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "esi")
public class ESIConfig {
    private String clientId;
    private String secretKey;
    private String scope;
    private String rasPublicKey;
    private String oauth2Http;
}

