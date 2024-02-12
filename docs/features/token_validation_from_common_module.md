# Token validation from Common module

Author: rafavilomar  
Status: `Draft` *[Draft, Developing, In review, Finished]*  
Last updated: 2024-02-11

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

## Considerations

All modules can import these functions when its necessary. Most of the case will be for validate privileges. This 
feature will be implemented in the future.