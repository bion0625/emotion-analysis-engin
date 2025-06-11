FROM gradle:8.0.2-jdk17 AS builder

COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# gradlew를 사용해서 빌드
RUN ./gradlew clean build -x test

FROM openjdk:17
COPY --from=builder /home/gradle/project/build/libs/nlp-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
