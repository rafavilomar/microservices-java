# Update product microservice
Author: rafavilomar  
Status: `In review` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-01-29

## Contents
- Objective
- Overview
- Solution
  - Use jakarta library
  - Use post http method to filter products
- Considerations

## Objective

Modules are now using version 3.2.1 of spring-boot-starter-parent, that means that javax library is no longer available 
and need to be replaced with jakarta for persistence and validation annotations. So all javax importation on Product needs 
to be replaced by jakarta.

## Solution

### Use jakarta library

All javax imports have been replaced by jakarta for services, controllers and entities files annotations. For example:

```java
// Old version
import javax.validation.Valid;

// New version
import jakarta.validation.Valid;
```

### Use post http method to filter products

Filter products method needs too many parameters to perform the request, that's why it uses `@RequestBody` instead of 
`@RequestParams` avoiding large urls.

It's also common for this kind of services to use `Post` instead of `Get` http method and handle parameters as an object. 
Just like this:

```java
@PostMapping("/filter")
public ResponseEntity<BaseResponse<Page<ProductResponse>>> filter(@RequestBody FilterProductRequest request) {

    log.info("Filter products {}", request);
    Page<ProductResponse> payload = service.filter(request);

    BaseResponse<Page<ProductResponse>> response = new BaseResponse<>();
    return response.buildResponseEntity(
            payload,
            HttpStatus.OK,
            "Products found"
    );
}
```