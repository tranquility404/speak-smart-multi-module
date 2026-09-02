package com.tranquility.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration")
public record OAuthProperties(
        Google google,
        Github github,
        LinkedIn linkedin
) {
    public record Google(String clientId, String clientSecret, String redirectUri) {}
    public record Github(String clientId, String clientSecret, String redirectUri) {}
    public record LinkedIn(String clientId, String clientSecret, String redirectUri) {}
}