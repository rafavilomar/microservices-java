# Update product microservice
Author: rafavilomar  
Status: `Developing` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-02-01

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

### Add endpoint to create users

## Considerations

This is just to add the http service, for now it's not necessary to validate privileges or handle JWT tokens.