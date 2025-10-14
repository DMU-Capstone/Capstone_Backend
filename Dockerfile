# =================
# 1. Build Stage: 소스코드를 빌드하여 .jar 파일을 생성하는 단계
# =================
FROM gradle:8.5.0-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 관련 파일들을 먼저 복사하여 의존성 캐시를 활용
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# 의존성 다운로드 (소스코드 변경 없이 의존성만 변경되었을 때 이 부분만 재실행됨)
RUN ./gradlew dependencies

# 전체 소스코드 복사
COPY src ./src

# Gradle 빌드 실행 (테스트는 제외)
RUN ./gradlew clean build -x test


# =================
# 2. Final Image Stage: 실제 실행될 최종 이미지를 만드는 단계
# =================
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# Build Stage에서 생성된 .jar 파일을 최종 이미지로 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java","-jar","/app.jar"]