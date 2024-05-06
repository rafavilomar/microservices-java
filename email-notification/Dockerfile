FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY pom.xml .
COPY target/email-notification-1.0-SNAPSHOT.jar app.jar
EXPOSE 8095
ENTRYPOINT ["java","-jar","app.jar"]