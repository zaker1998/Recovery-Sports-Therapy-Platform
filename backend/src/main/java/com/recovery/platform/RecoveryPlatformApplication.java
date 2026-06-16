package com.recovery.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Recovery &amp; Sports Therapy Management Platform — Spring Boot entry point.
 *
 * <p>JPA auditing is enabled here so that {@code @CreatedBy/@CreatedDate} on
 * {@code BaseEntity} are populated automatically. Caching backs the body-map
 * heatmap read-through cache (see {@code CacheConfig}).</p>
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableCaching
public class RecoveryPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecoveryPlatformApplication.class, args);
    }
}
