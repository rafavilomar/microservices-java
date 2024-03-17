# Purchase Flow

Author: rafavilomar  
Status: `Developing` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-03-17

## Contents

- Objective
- Goals
- No goals
- Overview
- Solution
  - Create Shopping microservice

## Objective

For each purchase is necessary to handle product inventory, loyalty points and email notifications. So, it's necessary 
to use Grpc and Kafka to microservices interactions from a new microservice called Shopping.

## Goals

- Add a new microservice Shopping
- Use grpc and kafka for microservices communication.
- Validate inventory and lot points.
- Send email notification

## No goals

- Add a shopping cart.
- Customize notification email.

## Overview

![Purchase flow](../images/purchase_flow.png)

## Solution

### Create Shopping microservice