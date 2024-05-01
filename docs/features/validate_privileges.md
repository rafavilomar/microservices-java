# Validate Privileges

Author: rafavilomar  
Status: `Finished` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-05-01

## Contents

- Objective
- Solution
    - Use annotations to validate privileges with Security module
- Considerations

## Objetive

As a continuation of doc [Token validation from Common module](token_validation_from_common_module.md), there are many 
http services that must be protected by privileges using the implemented classes in that feature.

## Solution

### Use annotations to validate privileges with Security module

All controllers are using the following annotation to validate specific privilege before bring access to the service. In
this example, the user must have the privilege `GET_CUSTOMER` to access to `Get customer by ID` service.

```java
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Customer")
@Slf4j
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final ICustomerService service;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GET_CUSTOMER')")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<BaseResponse<CustomerResponse>> getById(@PathVariable("id") long id) {
        log.info("Get customer by id {}", id);
        CustomerResponse customer = service.getById(id);

        BaseResponse<CustomerResponse> response = new BaseResponse<>();
        return response.buildResponseEntity(
                customer,
                HttpStatus.OK,
                "Customer found"
        );
    }
    
    // other services here...
}
```

To accomplish that each microservice must have the Spring Security dependency to validate every necessary request, so the following 
dependency to each missing pom.xml:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Once we added the dependency, it was necessary to create a general Spring Security configuration class like this:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(
                        new CustomAuthorizationFilter(jwtSecretKey),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
```

The most important things in this class are:
1. The `jwtSecretKey` storage the secret key to decode bearer tokens from each request.
2. The class `CustomAuthorizationFilter` is added like a filter for all request. This class read the request header, get the token and then validate it to load the logged user.

```java
@Slf4j
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final String jwtSecretKey;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        ResponseEntity<ExceptionResponse> responseBody;
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        /*
         * If the original request doesn't have the authorization header, or it isn't a Bearer token,
         * should return an unauthorized response
         */
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            responseBody = unauthorizedResponseInvalidToken();

            new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
            return;
        }

        try {

            /*
             * If the token is present and is valid load the authentication to the security context
             * and go ahead with the original request
             */
            String token = authorizationHeader.substring("Bearer ".length());
            String email = TokenValidationService.getEmail(token, jwtSecretKey);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    TokenValidationService.getPermissions(token, jwtSecretKey));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.info("Logged user {} trying to access: {}", email, request.getServletPath());
            filterChain.doFilter(request, response);
            return;

        } catch (TokenExpiredException e) {
            log.error("Error logging in: {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            responseBody = tokenExpiredResponse();

        } catch (Exception e) {
            log.error("Error logging in: {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            responseBody = unauthorizedResponseInvalidToken();
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
    }
    
    // private functions here to build error responses...
    
}
```

## Considerations

These privileges, or permissions, come from `Permission` entity on Security module. The also are loaded to each access 
token on the `AuthService` class as claims. To learn more see:

- [Implement JWT](implement_jwt.md)
- [How tokens are validated from `Common` module](token_validation_from_common_module.md)