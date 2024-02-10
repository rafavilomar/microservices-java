# Implement JWT

Author: rafavilomar  
Status: `In review` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-02-09

## Contents

- Objective
- Goals
- Solution
    - Add JWT tokens functions
    - Add Spring Security Filters
- Considerations

## Objective

So far, Spring Security is not validating any request, therefore it's necessary to implement any validation process
before bring access to some resources. To resolve that I need to implement JWT to know user and privileges for each
request.

## Goals

- Handle access token with limited time to be used
- Handle refresh token flow
- Add authentication and authorization filters

## No goals

- Implement oauth 2.0
- No implement privilege validation

## Solution

### Add JWT tokens functions

Beside Spring Security, it was necessary to add the next library to Security module:

```xml

<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>4.4.0</version>
</dependency>
```

Once we have it, all necessaries functions have been added to a new `AuthService` to handle access and refresh token
flows. Access token have only 10 minutes of live time, and refresh token have 12 hours before expire.

#### Access token

Access token contains user's email and granted permissions based on assigned rol. Therefore, it can be used later to
validate resources access based on privilege validation.

```java

@Override
public String generateAccessToken(org.springframework.security.core.userdetails.User user) {
    log.info("Create new access token for user {}", user.getUsername());
    return JWT.create()
            .withSubject(user.getUsername()) // User's email is used like username
            .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
            .withClaim(TOKEN_PERMISSION_CLAIM, user.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList())
            .sign(Algorithm.HMAC256(jwtSecretKey.getBytes()));
}
```

#### Refresh token

Unlike the access token, the refresh token only contains the user's email:

```java

@Override
public String generateRefreshToken(org.springframework.security.core.userdetails.User user) {
    log.info("Create new refresh token for user {}", user.getUsername());
    return JWT.create()
            .withSubject(user.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
            .sign(Algorithm.HMAC256(jwtSecretKey.getBytes()));
}
```

This token can be used to generate a new access token when the old one is expired. For that, this function read the
authorization header to extract the refresh token, validate it, and generate a new access token based on user's email.

```java

@Override
public TokensResponseDTO refreshToken(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        log.error("Incorrect refresh token {}", authorizationHeader);
        throw new UnauthorizedException("Provide a valid refresh token");
    }

    try {
        String refreshToken = authorizationHeader.substring("Bearer ".length());

        String email = getEmail(refreshToken);
        User user = userService.getByEmail(email);
        String accessToken = generateAccessToken(user);

        log.info("Generate new access token using a refresh token for user {}", email);
        return TokensResponseDTO.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();

    } catch (Exception e) {
        log.error(e.getMessage());
        throw new UnauthorizedException("Provide a valid refresh token");
    }
}
```

### Add Spring Security Filters

#### Authorization `CustomAuthorizationFilter.java`

This filter is used to validate the given access token for each request to any protected resource. If the access token
is correct, the user information and permissions will be loaded to Spring Security context, so it can be accessed from
any system part at any time.

```java
/*
 * If the original request doesn't have the authorization header, or it isn't a Bearer token,
 * should return an unauthorized response
 */
String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
if(authorizationHeader ==null||!authorizationHeader.startsWith("Bearer ")){
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    responseBody = unauthorizedResponseInvalidToken();

    new ObjectMapper().writeValue(response.getOutputStream(),responseBody);
    return;
}

try{

  /*
   * If the token is present and is valid load the authentication to the security context
   * and go ahead with the original request
   */
  String token = authorizationHeader.substring("Bearer ".length());
  String email = authService.getEmail(token);
  UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          email,
          null,
          authService.getPermissions(token));
  SecurityContextHolder.getContext().setAuthentication(authenticationToken);
  log.info("Logged user {} trying to access: {}",email, request.getServletPath());
  filterChain.doFilter(request, response);
  return;

} catch (TokenExpiredException e){
    log.error("Error logging in: {}",e.getMessage());
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    responseBody = tokenExpiredResponse();

} catch (Exception e) {
    log.error("Error logging in: {}",e.getMessage());
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    responseBody = unauthorizedResponseInvalidToken();
}
```

On the other hand, if the resource isn't protected, the token validation will be skipped,

```java
/*
 * If any request is trying to access any public it should avoid all the filter validation and go ahead with
 * the original request
 */
if (isPublicPath(request.getServletPath())) {
    filterChain.doFilter(request, response);
    return;
}
```

So far this only apply for login and refresh token service

```java
private boolean isPublicPath(String path) {
    return path.contains("/login") || 
            path.contains("/api/v1/auth/refrescarToken");
}
```

#### Authentication `CustomAuthenticationFilter.java`

This filter get request body from login service, extract email and password, and call `AuthService` functions to 
validate user and build response. This response contains user basic information, access token and refresh token. It 
will have more information in the future.

This is the function that intercept any login request:

```java
@Override
public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {

    ObjectMapper objectMapper = new ObjectMapper();
    LoginRequestDto loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginRequest.email(),
            loginRequest.password()
    );
    return authenticationManager.authenticate(authenticationToken);
}
```

Once this function is executed, the next function to get user from the database is called

```java
// From AuthService that implement UserDetailsService interface.

@Override
public UserDetails loadUserByUsername(String email) {
    log.info("User {} is trying to access the system", email);
    return mapUserDetails(userService.getByEmail(email));
}

private org.springframework.security.core.userdetails.User mapUserDetails(User user) {\
  if (!user.isActive() || !user.getRole().isActive()) {
    log.error("This user can't login: {}", user);
    throw new UnauthorizedException("Can't login in this moment :( Please contact assistance department.");
  }
  List<SimpleGrantedAuthority> authorities = user.getRole()
          .getPermissions()
          .stream()
          .map(permission -> new SimpleGrantedAuthority(permission.getCode()))
          .toList();

  return new org.springframework.security.core.userdetails.User(
          user.getEmail(),
          user.getPassword(),
          authorities
  );
}
```

If the user exists and has the same password, the next function is called to build the response and return it to client.

```java
@Override
protected void successfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication authentication) throws IOException {

    User user = (User) authentication.getPrincipal();

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ResponseEntity<BaseResponse<LoginResponseDTO>> result = new BaseResponse<LoginResponseDTO>().buildResponseEntity(
            authService.login(user),
            HttpStatus.OK,
            "Welcome :)"
    );

    new ObjectMapper().writeValue(response.getOutputStream(), result.getBody());
}
```

## Considerations

For token and privileges validation in all modules will be necessary to handle some of this functions in the Common 
module.