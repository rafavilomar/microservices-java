# Update product microservice
Author: rafavilomar  
Status: `Draft` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-01-29

## Contents
- Objective
- Overview
- Solution
  - Use jakarta library
  - Use post method to filter products
- Considerations

## Objective

Modules are now using version 3.2.1 of spring-boot-starter-parent, that means that javax library is no longer available 
and need to be replaced with jakarta for persistence and validation annotations. So all javax importation on Product needs 
to be replaced by jakarta.

## Solution

### Use jakarta library

### Use post method to filter products

## Considerations