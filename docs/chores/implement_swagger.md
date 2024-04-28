# Update loyalty microservice
Author: rafavilomar  
Status: `Draft` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-04-19

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