# AGENTS.md

## Project Snapshot
- Stack: Spring Boot `4.0.5`, Java `17`, Maven Wrapper (`.mvn/wrapper/maven-wrapper.properties` pins Maven `3.9.14`).
- Build file: `pom.xml` (single module, artifact `org.trainbeans:TrainOrder`).
- Main package root is `org.trainbeans.trainorder`; all new classes must stay under this root.
- Runtime: H2 in-memory database (`trainorderdb`), Thymeleaf views, Spring MVC, Spring JDBC, Actuator.

## Architecture and Data Flow
```
Browser → TrainOrderController (web)
              ↓
          TrainOrderService (service)
              ↓
          TrainOrderRepository (data) → H2 in-memory via NamedParameterJdbcTemplate
```
- **`model/TrainOrder.java`** — Lombok `@Data @Builder` domain object; one field per Form 19 blank.
- **`data/TrainOrderRepository.java`** — JDBC CRUD; uses `NamedParameterJdbcTemplate`. When calling `jdbc.update(…, keys, new String[]{"id"})` always pass the key-column array — H2 returns ALL generated columns otherwise and `getKey()` throws.
- **`service/TrainOrderService.java`** — thin service delegating to the repository.
- **`web/TrainOrderController.java`** — MVC controller at `/orders`; routes: list, new, create, edit/{id}, update/{id}, print/{id}, delete/{id}.
- **`web/HomeController.java`** — redirects `/` → `/orders`.
- **`src/main/resources/schema.sql`** — DDL for `train_orders`; run via `spring.sql.init.mode=always`.
- **`src/main/resources/templates/orders/`** — `list.html`, `form.html` (shared create/edit), `print.html` (Form 19 replica with `@media print`).
- **`src/main/resources/static/css/style.css`** — screen + print styles for the Form 19 paper replica.

## Testing and Developer Workflow
- Baseline test command (verified, 13 tests): `./mvnw -q test`
- Common local loop: `./mvnw spring-boot:run` → http://localhost:8080, `./mvnw test`, `./mvnw package`
- H2 console available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:trainorderdb`, user `sa`, no password).
- Actuator health: `http://localhost:8080/actuator/health`.

## Spring Boot 4 Test Annotation Packages (differ from SB3)
- `@JdbcTest`  → `org.springframework.boot.jdbc.test.autoconfigure.JdbcTest`
- `@WebMvcTest` → `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
- `@MockitoBean` → `org.springframework.test.context.bean.override.mockito.MockitoBean`
- Slice test starters in `pom.xml`: `spring-boot-starter-jdbc-test`, `spring-boot-starter-webmvc-test`.

## Project-Specific Conventions
- `pom.xml` excludes Lombok from the final artifact; Lombok annotation processing is wired via `maven-compiler-plugin`.
- `application.properties` owns all runtime settings (datasource URL, H2 console, init mode, actuator exposure). No connection details in Java code.
- Form 19 field names map directly to `train_orders` columns; `instructions` is a `CLOB` and stored as `String` in Java.
- `.gitignore` ignores `HELP.md`; track guidance only in `AGENTS.md` or `README.md`.
- Line-ending policy: `.gitattributes` sets `/mvnw` LF, `*.cmd` CRLF.

## Agent Working Agreement
- Add new layers under `org.trainbeans.trainorder.<layer>` (web, service, data, model).
- Keep `./mvnw -q test` green after every non-trivial change.
- Data is lost on restart (H2 in-memory); document any change to persistence in `application.properties` and here.
