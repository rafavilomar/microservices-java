# Basic missing configuration
Author: rafavilomar  
Status: `Developing` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-05-03

## Contents
- Objective
- Solution
    - Use environment variables with docker-compose
    - Add principal Readme.md
- Considerations

## Objective

There are some missing elements or configuration that must be added.  

## Solution

### Use environment variables with docker-compose

Now microservices are using environment variables based on this expample:

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

