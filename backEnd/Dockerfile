# Build環境
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Docker Layer Cachingのため　gradel設定ファイルを最初にコピー
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Source Codeをコピーし、build
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test

# Healthcheckのためのcurl設置
RUN apk add --no-cache curl

# build段階で生成された実行可能なjarファイルだけコピー
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# Container 実行
ENTRYPOINT ["java", "-jar", "app.jar"]