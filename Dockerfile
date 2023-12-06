# Dockerfile
# jdk17 Image Start
FROM eclipse-temurin:17-jdk-jammy

# 인자 설정 - JAR_File
ARG JAR_FILE=build/libs/*.jar

# jar 파일 복제
COPY ${JAR_FILE} app.jar

# 실행 명령어
ENTRYPOINT ["java", "-Dspring.profiles.active=prod,security,swagger", "-jar", "app.jar"]