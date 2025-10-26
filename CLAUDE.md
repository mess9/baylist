# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BayList is a multi-functional Spring Boot Telegram bot with Todoist integration and AI capabilities. The bot helps users
manage task lists by accepting input via Telegram, parsing tasks with a customizable dictionary, categorizing them, and
sending them to Todoist.

**Current Version:** 0.0.7

## Technology Stack

- Java 21
- Spring Boot 3.4.0
- Telegram Bot API 7.10.0
- Spring AI (OpenAI integration - currently disabled)
- PostgreSQL (migrated from Oracle)
- Spring Data JDBC (migrated from JPA)
- Liquibase for database migrations
- Spring Cache
- Spring Cloud OpenFeign for external API calls
- Spring Security with JWT
- Allure for test reporting

## Required Environment Variables

- `TOKEN_TG` - Telegram bot token
- `POSTGRES_DB_PASSWORD` - PostgreSQL database password
- `JWT_SECRET` - JWT secret key (optional, has default)
- `JWT_EXPIRATION` - JWT expiration time in milliseconds (optional, defaults to 24 hours)

## Development Commands

### Build and Test

```bash
# Build the project (skip tests)
mvn package -DskipTests=true

# Run tests
mvn test

# Run a specific test class
mvn test -Dtest=DictionaryTest

# Run a specific test method
mvn test -Dtest=DictionaryTest#testSplitInputTasks

# Generate Allure test report
mvn allure:serve
```

### Run Application

```bash
# Run Spring Boot application
mvn spring-boot:run
```

### Database

- Database migrations are managed by Liquibase
- Changelog location: `src/main/resources/db/changelog/db.changelog-master.xml`
- Spring Data JDBC is used (not JPA), so entities are records with `@Table` annotations
- On startup, Liquibase will automatically apply pending migrations

## Architecture

### State Machine Pattern

The bot uses a state-based dialog system to handle user interactions:

- User states are stored in the `Dialog` entity (database) and cached by `UserService`
- Each state has a corresponding handler implementing `DialogHandler` interface
- Handlers are located in `org.baylist.telegram.handler` package
- State transitions occur based on user input (text commands or callback buttons)
- States are defined in `State` enum: DEFAULT, START, CLEAR, VIEW, AI, DICT_SETTING, FRIENDS, etc.

**Key Flow:**

1. `BaylistBot` receives Telegram updates
2. Update is wrapped in `ChatValue` DTO with user context
3. `UserService` loads user state from cache or database
4. Appropriate handler is invoked via Spring ApplicationContext
5. Handler processes input and updates state if needed

### Core Components

**Telegram Layer** (`org.baylist.telegram`)

- `BaylistBot` - Main bot entry point, receives updates
- `handler/*` - State-specific handlers (DefaultHandler, StartHandler, AiHandler, etc.)
- Each handler implements `DialogHandler` interface

**Service Layer** (`org.baylist.service`)

- `UserService` - User management with caching (@Cacheable)
- `TodoistService` - Todoist API integration, task categorization
- `DictionaryService` - Category/variant management and task parsing
- `MenuService` - Telegram keyboard generation
- `CommonResponseService` - Common response utilities
- `DialogService` - State persistence

**Database Layer** (`org.baylist.db`)

- Entities are Java records with Spring Data JDBC annotations
- `User` - User data including Todoist token
- `Category` - Task categories
- `Variant` - Task variants within categories
- `Dialog` - User state tracking
- `Task` - Task history
- `History` - Action history

**API Layer** (`org.baylist.api`)

- `TodoistFeignClient` - Feign client for Todoist REST API v2
- `AuthController` - JWT authentication endpoints
- `TaskController` - REST endpoints for task management
- `ProbeController` - Health check

**AI Layer** (`org.baylist.ai`)

- Currently disabled (commented out)
- Was integrated with Spring AI and OpenAI
- Provided functions for AI to manipulate tasks, dictionary, and friends

### Data Flow: Task Submission

1. User sends task list to Telegram bot
2. `DefaultHandler.checkAndInput()` validates input
3. `TodoistService.sendTasksToTodoist()` is called
4. `DictionaryService.parseInputWithDict()` categorizes tasks using user's dictionary
5. Tasks are submitted to Todoist API via `TodoistFeignClient`
6. Result is sent back to user via Telegram

### Caching Strategy

- `UserService` caches user data with @Cacheable("user")
- `DictionaryService` caches dictionary with @Cacheable("dict")
- Cache eviction occurs on updates (@CacheEvict, @CachePut)
- In-memory state maps used for Todoist data (`ConcurrentHashMap`)

### Friend System

Users can add friends and send tasks to them:

- `FriendsDao` manages bidirectional friend relationships
- A user can send tasks to friends who have Todoist configured
- Friends can be added, removed, and listed

## Testing

- Tests are organized in `src/test/java/org/baylist/tests`
- `BaseTest` provides common test infrastructure
- Test categories: AI tests, API tests, DB tests, Dictionary tests
- Allure annotations used for test reporting
- AspectJ weaver configured for test instrumentation
- Tests interact with actual Todoist API (integration tests)

## Deployment

- Automated CI/CD via GitHub Actions (`.github/workflows/deploy.yml`)
- On push to master: Maven build, create artifact, SCP to remote server, restart systemd service
- Remote server runs `buylist.service` systemd unit
- Port 8080 exposed

## Code Patterns and Conventions

- Lombok used extensively: @RequiredArgsConstructor, @FieldDefaults, @Slf4j
- Records used for immutable entities and DTOs
- Enums for constants (State, Callbacks, Commands, Action)
- Spring dependency injection via constructor injection
- Transaction management with @Transactional
- Exception handling via @ControllerAdvice

## Notable Implementation Details

- The bot maintains backward compatibility after Oracle to PostgreSQL migration
- Dialog platform property renamed from "tg" to "telegram"
- User ID for developer (Phil) is hardcoded as constant: FIL_USER_ID
- Telegram keyboards are built with InlineKeyboardMarkup
- Task categorization uses fuzzy matching against user dictionary
- Todoist project name constant: "BuyList"
- Unknown category constant: "другое" (other)

## Recent Changes

Recent commits indicate:

- Migration from Oracle to PostgreSQL
- Migration from Spring Data JPA to Spring Data JDBC
- Test fixes
- Deployment configuration fixes
