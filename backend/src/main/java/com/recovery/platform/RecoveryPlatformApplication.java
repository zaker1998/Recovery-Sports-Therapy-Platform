package com.recovery.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Recovery &amp; Sports Therapy Management Platform — Spring Boot entry point.
 *
 * <p>JPA auditing is enabled here so that {@code @CreatedBy/@CreatedDate} on
 * {@code BaseEntity} are populated automatically. {@code @EnableScheduling} is
 * used by appointment reminders and recovery streak jobs.</p>
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableCaching
@EnableAsync
@EnableScheduling
public class RecoveryPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecoveryPlatformApplication.class, args);
    }
}
