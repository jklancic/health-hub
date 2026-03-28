package com.healthhub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimitProperties {

    private LimitConfig auth = new LimitConfig(5, 1);
    private LimitConfig api = new LimitConfig(100, 1);

    public LimitConfig getAuth() {
        return auth;
    }

    public void setAuth(LimitConfig auth) {
        this.auth = auth;
    }

    public LimitConfig getApi() {
        return api;
    }

    public void setApi(LimitConfig api) {
        this.api = api;
    }

    public static class LimitConfig {
        private int capacity;
        private long refillMinutes;

        public LimitConfig() {
        }

        public LimitConfig(int capacity, long refillMinutes) {
            this.capacity = capacity;
            this.refillMinutes = refillMinutes;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public long getRefillMinutes() {
            return refillMinutes;
        }

        public void setRefillMinutes(long refillMinutes) {
            this.refillMinutes = refillMinutes;
        }
    }
}
