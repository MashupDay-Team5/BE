# Java 21 실행 환경
FROM eclipse-temurin:21-jre

WORKDIR /app

# GitHub Actions에서 빌드한 jar를 이미지 안으로 복사
COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
