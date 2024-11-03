# todo_app_api

## Authentication Endpoints
URL: http://localhost:5000/api/authenticate
Method: POST
Description: Authenticates a user and returns a JWT token with user details
Request Body: {
  "email": "user@example.com",
  "password": "password123"
}

## User Endpoints
URL: http://localhost:5000/api/signup
Method: POST
Description: Registers a new user and returns a JWT token.
Request Body: {
  "email": "user@example.com",
  "password": "password123",
  "name": "John Doe"
}

##ToDo Endpoints

URL: http://localhost:5000/api/todo
Method: POST
Description: Creates a new to-do item for the authenticated user.
Headers: Requires JWT token in the Authorization header.
Request Body: {
    "title": "Project 1",
    "description": "Complete all tasks for the project",
    "dueDate": "2024-11-20T23:59:59",
    "priority":4    
}

URL: http://localhost:5000/api/todo
Method: PUT
Description: Updates an existing to-do item.
Headers: Requires JWT token in the Authorization header.
Request Body: {
    "id":302,
    "title": "project 1",
    "description": "Complete all tasks for the project",
    "dueDate": "2024-11-18T23:59:59",
    "priority":4,
    "completed":true    
}

URL: http://localhost:5000/api/todo/{id}
Method: GET
Description: Retrieves a to-do item by its ID.
Headers: Requires JWT token in the Authorization header.

URL: http://localhost:5000/api/todo
Method: GET
Description: Retrieves all to-do items of the authenticated user, with optional pagination and sorting.
Headers: Requires JWT token in the Authorization header.
Query Parameters: page, size, sortBy, direction, search

URL: http://localhost:5000/api/todo/{id}
Method: DELETE
Description: Deletes a to-do item by its ID.
Headers: Requires JWT token in the Authorization header.

URL: http://localhost:5000/api/todo/{id}/completion-status
Method: PUT
Description: Updates the completion status of a to-do item.
Headers: Requires JWT token in the Authorization header.
Query Parameters:isCompleted
