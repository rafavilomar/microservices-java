FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY pom.xml .
COPY target/Shopping-1.0-SNAPSHOT.jar app.jar
EXPOSE 8150
ENTRYPOINT ["java","-jar","app.jar"]