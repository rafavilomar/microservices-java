# Implement JWT
Author: rafavilomar  
Status: `Draft` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-02-02

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

## Considerations

