# Token validation from Common module

Author: rafavilomar  
Status: `Developing` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-02-25

## Contents

- Objective
- Solution
  - Move token validation functions
- Considerations

## Objective

For now just Security module can validate access for its own requests, so it's necessary to move those function to 
Common module and avoid duplicated features.

## Solution

### Move token validation function

Functions to decode token, read the user email and get all permissions have been moved to `TokenValidationService` 
interface on the Common module. This interface only consists of static methods that can be changed.

#### Get the email from token

```java
static String getEmail(@NotNull String token, @NotNull String jwtSecretKey) {
    return decodedJWT(token, jwtSecretKey).getSubject();
}
```

#### Get permissions from token

```java
static List<SimpleGrantedAuthority> getPermissions(@NotNull String token, @NotNull String jwtSecretKey) {

    String[] permissions = decodedJWT(token, jwtSecretKey)
            .getClaim(TOKEN_PERMISSION_CLAIM)
            .asArray(String.class);

    return Arrays.stream(permissions)
            .map(SimpleGrantedAuthority::new)
            .toList();
}
```

#### Decode token

```java
static DecodedJWT decodedJWT(@NotNull String token, @NotNull String jwtSecretKey) {
    JWTVerifier verifier = JWT
            .require(Algorithm.HMAC256(jwtSecretKey.getBytes()))
            .build();
    return verifier.verify(token);
}
```

## Considerations

All modules can import these functions when its necessary. Most of the case will be for validate privileges. This 
feature will be implemented in the future.