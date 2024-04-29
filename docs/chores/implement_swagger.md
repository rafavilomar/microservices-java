# Update loyalty microservice
Author: rafavilomar  
Status: `In review` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-04-29

## Contents
- Objective
- Solution
    - Add swagger for each microservice
    - Unify Swagger-UI from all microservices into API Gateway
- Considerations

## Objective

It's necessary to add swagger for all required microservices to have each endpoint documented. But have multiple 
swaggers is not intuitive, so it's also necessary to unify them. This could be done by the API Gateway itself.

## Solution

### Add swagger for each microservice

Every needed microservice has a `SwaggerConfiguration` with a basic configuration like this:

```java
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Shopping")
                .description("Shopping module")
                .version("1.0");
    }

}
```

Also, all controllers have these following annotations for documentation and improve user experience using the Swagger UI:
- `@Tag`: to set the controller's name.
- `@Operation`: to describe the endpoint purpose.
- `@SecurityRequirement`: to set the authorization method to access endpoints.

On the other hand, all `@RestControllerAdvice` have been documented using the annotation `@ResponseStatus` just like 
this:

```java
@ExceptionHandler(EntityNotFoundException.class)
@ResponseStatus(HttpStatus.NOT_FOUND)
public ResponseEntity<ExceptionResponse> handleError(EntityNotFoundException exception) {

    ExceptionResponse response = new ExceptionResponse(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            List.of(exception.getMessage())
    );

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
}
```

### Unify Swagger-UI from all microservices into API Gateway

Can't load Swagger UI for API Gateway. Seems like there is a compatibility issue between `spring-cloud-starter-gateway` 
and `springdoc-openapi-starter-webmvc-ui` dependencies. 

I tried replacing `springdoc-openapi-starter-webmvc-ui` by `springdoc-openapi-starter-webflux-ui` and was able to access 
to Swagger UI, but couldn't access to any other microservice's swagger from API Gateway UI.

Tried using a properties configuration like this:

```yaml
springdoc:
  enable-native-support: true
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    config-url: /v3/api-docs/swagger-config
    urls:
      - url: /v3/api-docs
        name: API Gateway Service
        primaryName: API Gateway Service
      - url: /microservice1/v3/api-docs
        name: Microservice 1
        primaryName: Microservice 1
      - url: /microservice2/v3/api-docs
        name: Microservice 2
        primaryName: Microservice 2
```

Should return to this point in the future.

All of this comes from these pages:
- [Swagger(OpenAPI Specification 3) Integration with Spring Cloud Gateway — Part 2](https://medium.com/@pubuduc.14/swagger-openapi-specification-3-integration-with-spring-cloud-gateway-part-2-1d670d4ab69a)
- [Cannot access api-docs of microservice from spring cloud gateway. Failed to load API definition](https://stackoverflow.com/questions/71108572/cannot-access-api-docs-of-microservice-from-spring-cloud-gateway-failed-to-load)