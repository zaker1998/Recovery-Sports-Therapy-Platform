# syntax=docker/dockerfile:1.7
# ─── Stage 1: build ───────────────────────────────────────────────────────────
# Use the official Maven image because this repository does not currently
# include a Maven wrapper (`mvnw` + `.mvn/`).
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./

# Pre-download dependencies so they cache in a separate layer.
RUN mvn -B -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# ─── Stage 2: runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75 -Djava.security.egd=file:/dev/./urandom"

RUN addgroup --system app && adduser --system --ingroup app app
USER app

COPY --from=build /workspace/target/recovery-platform.jar ./app.jar

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q UP || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
