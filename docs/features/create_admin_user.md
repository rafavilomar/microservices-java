# Update product microservice
Author: rafavilomar  
Status: `Developing` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-02-02

## Contents
- Objective
- Solution
  - Refactor user validation and persistence
  - Add endpoint to create users
- Considerations

## Objective
So far, there is only a service to create users as s customer. It's necessary to add another service to create internal 
users like admins, assistants, and others. For that, some codes from create customer could be refactored.

## Solution

### Refactor user validation and persistence

To avoid duplicated code, email validation and user persistence were moved to the new `registerUser` function, just like 
this:

```java
public User registerUser(RegisterUserRequest newUser) {
    log.info("Register new user {}", newUser);
    if (userRepository.existsByEmail(newUser.email()))
        throw new BadRequestException("Seems this email is already registered. Try login!");

    Role role = roleService.findByName(newUser.roleName());
    return userRepository.save(User.builder()
            .email(newUser.email())
            .password(passwordEncoder.encode(newUser.password()))
            .role(role)
            .build());
}
```

### Add endpoint to create users

## Considerations

This is just to add the http service, for now it's not necessary to validate privileges or handle JWT tokens.