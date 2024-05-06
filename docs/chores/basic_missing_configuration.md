# Basic missing configuration
Author: rafavilomar  
Status: `Finished` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-05-06

## Contents
- Objective
- Solution
  - Add Dockerfile for each microservice
  - Use environment variables with docker-compose
  - Add principal Readme.md
- Considerations

## Objective

There are some missing elements or configuration that must be added.  

## Solution

### Add Dockerfile and Docker Compose for each microservice

Now each microservice have their own Dockerfile and Docker Compose, just like API Gateway module:

#### Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/api-gateway-1.0-SNAPSHOT.jar app.jar
EXPOSE 8765
ENTRYPOINT ["java","-jar","app.jar"]
```

#### Docker Compose

```yaml
services:
  api-gateway:
    container_name: api-gateway
    build:
      context: .
      dockerfile: Dockerfile
    platform: linux/amd64
    env_file: ../.env
    ports:
      - "8765:8765"
    restart: unless-stopped

networks:
  apps:
    driver: bridge
    external: true
```

### Use environment variables with docker-compose

Now microservices are using environment variables based on this example:

```text
# Email service credentials
SMTP_EMAIL_USERNAME
SMTP_EMAIL_PASSWORD

# Service URLs
EUREKA_DEFAULT_ZONE
ZIPKIN_URL
KAFKA_URL

# Spring Security key
JWT_SECRET_KEY

# Database credentials
DB_USERNAME
DB_PASSWORD
```

### Add principal Readme.md

Now there is a new `Readme.md` explaining the projects modules and all prerequisites to run all microservices.

