# Tickets API

This project implements a RESTful API for managing support tickets, including creating tickets, listing tickets with filtering options, updating ticket status, and adding comments.

## Setup and Run Instructions

1.  **Prerequisites:**
    *   Java Development Kit (JDK) 17 or higher
    *   Maven

2.  **Clone the repository:**
    ```bash
    git clone <repository_url>
    cd ticketsapi
    ```

3.  **Build the project:**
    ```bash
    ./mvnw clean install
    ```

4.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    The application will start on `http://localhost:8080`.

## Testing Instructions

To run the unit and integration tests:

```bash
./mvnw test
```

## Design Decisions

*   **Layered Architecture:** This helps achieve a modular and maintainable application design. It makes it easier to understand, test, and scale the codebase.
*   **In-memory Storage:** For simplicity, the application uses in-memory storage (`ConcurrentHashMap`) for persistence.
*   **DTO Pattern usage:**  This decouples the public API from the internal data model (data model changes won't break the API). It also prevents accidentally exposing sensitive or internal data structure. Although some endpoints don't use a DTO such as when retrieving all tickets but this is deliberate due to lack of information on what fields we want to expose. alternatively we can Use a TicketRespose DTO object to expose the wanted fields. 
*   **Comment Visibility:** Added an endpoint to get all the comments for a ticket with a userType argument to Enforced rules for public and internal comments, ensuring users only see public comments.
*   **Centralized Exception Handling:** Intercepts exceptions thrown from anywhere in the application and translates them into consistent, user-friendly HTTP error responses and it also keeps the business logic in the service layer clean.
*   **Mapper Layer:** Utilizes MapStruct for mapping between DTOs and entities for performance and type safety due to its compile-time code generation.

## AI Tool Usage and Validation Steps

*  **Initial Setup:** Used AI to generate an initial project with all the necessary packages.
*  **Data Modeling:** Used AI to generate payload DTO classes with the necessary fields. Manually verified and added correct validation.
*  **Tests and Tests Fixes:** Used AI to generate baseline tests for the application then manually refined them. Although with more time we could've had more robust and thorough tests. Also used AI to help identify source of failed tests and correct them.
*  **Refactoring :** for example : `canTransitionTo` initially was in the `TicketStatus` class. The AI was instructed to move the `canTransitionTo` to a new `TicketStatusService.java` and update `TicketService` to use the new service.
*  **Documentation:** The AI generated a first version of the `README.md` file, with setup and run instructions and testing instructions.
