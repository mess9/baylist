# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BayList is a Spring Boot Telegram bot (https://t.me/buylistFABot) that takes a shopping or to-do list typed in Telegram, sorts each line into a category using the user's own dictionary, and creates the tasks in the user's Todoist project `buylist`. Users can also send tasks to friends who have Todoist set up.

Stack: Java 21, Spring Boot 3.4.0, telegrambots 7.10 (long polling), Spring Data JDBC (not JPA; entities are records), PostgreSQL + Liquibase, OpenFeign (Todoist), Spring Security + JWT, Spring Cache. There is also a SolidJS/Vite frontend in `frontend/`.

`readme.md` is out of date: it still describes Oracle, JPA and OpenAI. Trust the code and this file over it.

## Commands

```bash
mvn package -DskipTests=true          # build (this is what CI runs)
mvn spring-boot:run                   # run the bot
mvn test                              # all tests
mvn test -Dtest=DictionaryTest        # one class
mvn test -Dtest=DictionaryTest#testSplitInputTasks   # one method
mvn allure:serve                      # Allure report from target/allure-results
```

Frontend (`frontend/`): `npm run dev`, `npm run build`, `npm run storybook`.

Environment variables: `TOKEN_TG` (required at startup, because `Bot` builds its Telegram client in the constructor), `POSTGRES_DB_PASSWORD`, and optionally `JWT_SECRET` and `JWT_EXPIRATION`.

## Things to know before running anything

- **There is no local or test database profile.** `application.yml` points the datasource at the remote PostgreSQL server (`152.70.174.74:5432/buylist`), and `src/test/resources/application.yaml` does not override it. Running the app or the tests uses that shared database and calls the real Todoist API.
- Tests are `@SpringBootTest` integration tests built on `BaseTest`. They get a Todoist token by loading the developer's user (`FIL_USER_ID`) from the database (`@FilToken` → `DefaultToken`). Projects, sections and tasks created through `TodoistFeignClient` are recorded by the AspectJ aspect `CatchCreatedEntity` (`src/test/resources/META-INF/aop.xml`) and deleted in `ClearTestData` after all tests.
- Surefire adds `-javaagent` flags pointing at the mockito-core 5.14.2 and aspectjweaver jars in the local Maven repository, so those jars must already be in `~/.m2`.
- JUnit runs test classes and methods in parallel (`junit-platform.properties`), so tests must not depend on shared mutable state.

## Architecture

### Update pipeline (`telegram/Bot.java`)

For each Telegram update, `Bot.consume` runs these steps in order:
1. Wraps the update in `ChatValue`, which carries the request and the reply (`message`, `editMessage`, `forwardMessage`) through the whole pipeline.
2. Calls `UserService.checkUser`, which loads or creates the `User` and their `Dialog`. New users start in `State.START`.
3. Calls `CommandChecker.checkCommandInput`, which maps slash and keyboard commands (the `Commands` enum) to a `State`. Callbacks skip this step.
4. Calls `stateHandlers.get(state).handle(chatValue)`, then saves the user, attaches the default menu, and sends the reply.

### State machine

- `State` is stored per user in the `dialogs` table. `ChatValue.getState()` loads it lazily, and **`ChatValue.setState()` writes it to the database immediately** through `DialogService`.
- Handlers implement `DialogHandler` and are wired by hand in the `Map<State, DialogHandler>` bean in `telegram/handler/config/StateHandlerConfig`. **A new handler must be registered there**, and its command added to `CommandChecker` if it has one. Every `State` must have a handler: `Bot` looks it up with `stateHandlers.get(state)`, so a state without one throws a null-pointer exception. When removing a `State`, add a migration that moves users out of it in `dialogs`, because an unknown enum value breaks loading the dialog.
- `Bot.consume` ignores updates that are neither a message nor a callback (blocking the bot, edited messages, and so on).
- Inline-button callbacks are defined in the `Callbacks` enum, and handlers dispatch on them. `MenuService` and `TgButtonService` build the keyboards.

### Task submission

`DefaultHandler` → `TodoistService.sendTasksToTodoist`:
1. `syncBuyListData` fetches the projects, sections and tasks of the Todoist project named `buylist` (case-insensitive). It mirrors the tasks into the `tasks` table (`TaskService.syncTasks`) and stores the snapshot in the in-memory `todoistStateMap`.
2. `DictionaryService.splitInputTasks` splits the input on newlines. `parseInputWithDict` then assigns each line to a category by **exact, case-insensitive match** against the variants in that category. Lines that match nothing go to `UNKNOWN_CATEGORY` (`"other"`).
3. Categories map to Todoist sections. Missing sections are created, and tasks already in the project are not added again.
4. When a dictionary variant is added later, `changeCategoryForExistTasks` moves existing tasks into the matching section.

### Other notes

- **Todoist client:** `api/TodoistFeignClient` calls the Todoist **v1 API** (`todoist.baseUrl` = `https://api.todoist.com/api/v1`; the REST v2 API was retired). Pass the token as `"Bearer " + token` (`User.bearerToken()`). Errors go through `FeignErrorDecoder` → `TodoistApiException`.
- **Caches:** `user`, `dict` and `categories` use Spring Cache. Within the same class, `DictionaryService` calls its own cached methods through `context.getBean(DictionaryService.class)` (the `self` pattern) so the cache proxy is not bypassed. Keep this pattern, and evict the `dict`/`categories` caches whenever the dictionary changes.
- **Friends:** `FriendsDao` uses `JdbcClient` with hand-written SQL instead of a repository, and the friend relationship goes both ways. `TodoistService.checkRecipients` decides who receives the tasks.
- **REST API:** under `/api` (`AuthController`, `TaskController`, `UserController`). `/api/auth/telegram` issues a JWT after Telegram auth (`TelegramAuthenticationFilter`, `JwtService`). `/api/auth/**`, `/api/webhook/**` and `/actuator/**` are public.
- **Constants (`dto/Constants`):** `BUYLIST_PROJECT = "buylist"`, `UNKNOWN_CATEGORY = "other"`, `FIL_USER_ID` (the developer's Telegram ID; tests use this account's Todoist token).
- **Telegram limits:** see `src/main/resources/telegram_limits.md`. Long output is paged at `LIMIT_CHAR_FOR_ONE_PAGE = 4000` characters.

## Database

- Liquibase changelog: `src/main/resources/db/changelog/db.changelog-master.xml`, with migrations in `migrations/` (numbered YAML files). Add a new numbered file; do not edit applied ones.
- Repositories are Spring Data JDBC `CrudRepository` interfaces over `@Table` records in `db/entity`.

## Deployment

- `.github/workflows/deploy.yml` runs on every push to `master`. It builds with tests skipped, copies the jar over SCP and restarts the `buylist.service` systemd unit. **The jar name `baylist-0.0.7.jar` is hard-coded in the workflow**, so update it whenever the `pom.xml` version changes.
- The frontend is deployed by a separate workflow when a `front_v*` tag is pushed (built with npm on the server and served by nginx).

## Conventions

- Lombok everywhere: `@RequiredArgsConstructor`, `@FieldDefaults(makeFinal = true, level = PRIVATE)`, `@Slf4j`. Code is mostly indented with tabs.
- User-facing bot text, comments and commit messages are in Russian.
