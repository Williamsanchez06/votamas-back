FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace
COPY . .

RUN chmod +x ./gradlew \
    && ./gradlew :app-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S votamas \
    && adduser -S votamas -G votamas

WORKDIR /app

COPY --from=builder \
    /workspace/applications/app-service/build/libs/votamas.jar \
    /app/votamas.jar

USER votamas

EXPOSE 10000

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/votamas.jar"]
