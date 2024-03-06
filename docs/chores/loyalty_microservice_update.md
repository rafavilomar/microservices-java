# Update loyalty microservice
Author: rafavilomar  
Status: `Finished` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-02-26

## Contents
- Objective
- Solution
  - Use jakarta library
  - Remove deprecated JPA calls

## Objective

Modules are now using version 3.2.1 of spring-boot-starter-parent, that means that javax library is no longer available 
and need to be replaced with jakarta for persistence and validation annotations. So all javax importation on Loyalty 
needs to be replaced by jakarta.

## Solution

### Use jakarta library

All javax imports have been replaced by jakarta for services, controllers and entities files annotations. For example:

```java
// Old version
import javax.validation.Valid;

// New version
import jakarta.validation.Valid;
```

### Remove deprecated JPA calls

Points redemption and accumulation rules services are using the deprecated `getbyId()` function to retrieve entities 
from database. This calls must be replaced by the `findById()` function or any other existing function that call this 
one.

#### Accumulation Points Rule

```java
// Before
AccumulationPointsRule ruleToActivate = repository.getById(idAccumulationPointsRule);

// After
AccumulationPointsRule ruleToActivate = getById(idAccumulationPointsRule);
```

Instead of the `getById()` function from JPA repository, now is calling one that implements `findById()` as required.

```java
@Override
public AccumulationPointsRule getById(long idAccumulationPointsRule) {
    return repository.findById(idAccumulationPointsRule)
            .orElseThrow(() -> new EntityNotFoundException("Accumulation Points Rule not found"));
}
```

#### Points Redemption Rule

Just like accumulation rules, the `getById()` from JPA repository has been replaced by `findById()`.

```java
// Before
PointsRedemptionRule ruleToActivate = repository.getById(idAccumulationPointsRule);

// After
PointsRedemptionRule ruleToActivate = getById(idAccumulationPointsRule);
```

```java
@Override
public PointsRedemptionRule getById(long idAccumulationPointsRule) {
    return repository.findById(idAccumulationPointsRule)
            .orElseThrow(() -> new EntityNotFoundException("Accumulation Points Rule not found"));
}
```