# Implement JWT
Author: rafavilomar  
Status: `Developing` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-02-08

## Contents
- Objective
- Goals
- Solution
  - Add Spring Security Filters
  - Add JWT tokens functions
- Considerations

## Objective

So far, Spring Security is not validating any request, therefore it's necessary to implement any validation process 
before bring access to some resources. To resolve that I need to implement JWT to know user and privileges for each request.

## Goals
- Handle access token with limited time to be used
- Handle refresh token flow
- Add authentication and authorization filters

## No goals
- Implement oauth 2.0
- No implement privilege validation

## Solution

### Add Spring Security Filters

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

## Considerations

