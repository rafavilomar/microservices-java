# Update loyalty microservice
Author: rafavilomar  
Status: `Developing` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-02-26

## Contents
- Objective
- Solution
  - Use jakarta library
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