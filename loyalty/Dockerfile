FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY pom.xml .
COPY target/loyalty-1.0-SNAPSHOT.jar app.jar
EXPOSE 8120
ENTRYPOINT ["java","-jar","app.jar"]