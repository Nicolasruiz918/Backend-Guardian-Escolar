package com.guardianescolar.api.modules.push.service;

import java.net.URI;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "guardian.push")
public class PushProperties {

    private boolean enabled;
    private String provider = "expo";
    private URI expoEndpoint = URI.create("https://exp.host/--/api/v2/push/send");
    private String expoAccessToken;
    private Duration connectTimeout = Duration.ofSeconds(3);
    private Duration requestTimeout = Duration.ofSeconds(5);

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public URI getExpoEndpoint() {
        return expoEndpoint;
    }

    public void setExpoEndpoint(URI expoEndpoint) {
        this.expoEndpoint = expoEndpoint;
    }

    public String getExpoAccessToken() {
        return expoAccessToken;
    }

    public void setExpoAccessToken(String expoAccessToken) {
        this.expoAccessToken = expoAccessToken;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Duration requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
}
