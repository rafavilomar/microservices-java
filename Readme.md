# Microservices

### Table of content
- [Technologies](#technologies)
- [About](#about)
- [Running the Project](#running-the-project)
- [Author](#author)

## Technologies
![TypeScript](https://img.shields.io/badge/-Java-FF160B?style=flat&logo=Java&logoColor=ffffff)
![Spring Boot](https://img.shields.io/badge/-Socket_io-6DB33F?style=flat&logo=springboot&logoColor=fff)
![PostgreSQL](https://img.shields.io/badge/-Postgres-4169E1?style=flat&logo=postgresql&logoColor=fff)
![Docker](https://img.shields.io/badge/-Docker-2496ED?style=flat&logo=Docker&logoColor=fff)


## About
This is just an example of how to implement a microservice architecture based on Java technologies. The purpose is to 
show how I would handle different kind of communications for microservice looking for better performance and the perfect
tools depending on the context.

This is not a product or application itself, but just a showcase of how Spring technologies could be implemented.

### Modules and Microservices
- **Api Gateway:** It's the unique entry point for all users to the system. Use Spring Gateway to redirect all requests to the necessary microservice.
- **Common:** Consist of different classes and services necessary for many microservices. It's used to avoid code duplication and can be imported as a dependency from any microservice.
- **Customer:** Handle all customer and payment method information.
- **Docs:** Contains all documentation and images used for any Design Doc.
- **Email Notification:** Dedicated to all user's notification trough email. It isn't connected to API Gateway and use Kafka to communicate with other microservices.
- **Eureka Server:** Use Spring Cloud Netflix Eureka to act like a discovery service and register all running microservice.
- **Loyalty:** Handle all benefits for frequent customers.
- **Product:** Handle product inventory classified by categories.
- **Security:** Handle all service to create and validate JWT tokens, users, roles and permissions.
- **Shopping:** Perform basic services for shopping modules like purchase.

## General Diagram

- **Red arrows:** Indicates which microservices are exposed to users by API Gateway.
- **Blue arrows:** Indicates which microservices are using Kafka to send and read events.
- **Green arrows:** Indicates which microservices are using gRPC for internal communication.

![General Diagram.png](docs%2Fimages%2FGeneral%20Diagram.png)


## Before Running the Project
It's necessary to create a new `.env` file in the root project to define all necessary environment variables, there is 
a `.env.example` file with variable list. 

## Running the Project
It is advisable to use docker to run docker-compose and avoid installing all the technologies and running the services 
one by one. Install `Docker` depending on the operative system, go to project root and then execute the following 
commands in console:

```shell
docker-compose up -d
```

### Author
- Author: Rafael Vilomar
- LinkedIn: https://www.linkedin.com/in/rafavilomar/
- Email: rafavilomar@gmail.com