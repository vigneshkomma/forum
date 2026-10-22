# Forum

A small single-forum application: accounts, posts, comments, and likes on both.

**Stack:** Spring Boot 4.1 · Java 17 · Spring MVC + Thymeleaf · Spring Security (form login, BCrypt) · Spring Data JPA / Hibernate · PostgreSQL · Flyway

## Features

- Register / log in / log out (BCrypt-hashed passwords, CSRF-protected forms)
- Main page lists every post, newest first, with comment and like counts
- Authenticated users can create posts and comment
- Like/unlike posts and comments; one like per user is enforced by the database
- Pagination (10 posts per page)
- Validation with inline error messages
- Custom 400 / 403 / 404 / 500 pages, responsive styling

## Run it

### 1. Start PostgreSQL

```bash
docker run --name forum-postgres \
  -e POSTGRES_DB=forum \
  -e POSTGRES_USER=forum \
  -e POSTGRES_PASSWORD=forum \
  -p 5432:5432 \
  -d postgres:16
```

Or, if you prefer Compose:

```bash
docker compose up -d
```

### 2. Start the app

```bash
cp .env.example .env    # then put your DB credentials in .env
./mvnw spring-boot:run
```

The Maven wrapper is not executable in this checkout, so use `bash ./mvnw spring-boot:run` if needed.

Open <http://localhost:8080>.

Flyway creates the tables on first startup from
`src/main/resources/db/migration/V1__create_forum_schema.sql`. Hibernate runs with
`ddl-auto=validate`, so a mismatch between an entity and the migration fails fast at startup.

### Configuration

Copy the example and fill in your local credentials:

```bash
cp .env.example .env
```

`.env` is git-ignored and loaded automatically at startup
(`spring.config.import=optional:file:./.env[.properties]`).

| Variable | Default |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/forum` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | *(empty)* |

Environment variables work too and take precedence over `.env`.

## Tests

```bash
./mvnw test
```

The test suite is a web-slice suite (`@WebMvcTest`) and does **not** need a database. It renders
every page and checks the security rules (public pages, CSRF token, login redirect).

## What lives where

```
src/main/java/com/example/forum
├── config/       SecurityConfig, PasswordConfig
├── domain/       JPA entities (User, Post, Comment, PostLike, CommentLike, …)
├── dto/          Read models (PostSummary, CommentView, LikeResult)
├── exception/    ResourceNotFoundException, DuplicateAccountException
├── repository/   Spring Data repositories
├── security/     ForumUserDetails (principal carrying the user id)
├── service/      Transactions and business rules
└── web/          Controllers, global advice, form objects

src/main/resources
├── db/migration/ Flyway SQL (schema source of truth)
├── static/css/   app.css
└── templates/    Thymeleaf pages + fragments
```

## Routes

| Method | Path | Access |
|---|---|---|
| GET | `/` | public |
| GET | `/posts/{id}` | public |
| GET | `/register`, `/login` | public |
| POST | `/register` | public |
| POST | `/login` | public |
| GET | `/posts/new` | logged in |
| POST | `/posts` | logged in |
| POST | `/posts/{id}/comments` | logged in |
| POST | `/posts/{id}/like` | logged in |
| POST | `/posts/{postId}/comments/{commentId}/like` | logged in |
| POST | `/logout` | logged in |

## Known limitations

- No edit or delete for posts/comments (the DB cascades are ready for delete actions).
- Comments are flat; nesting would need a self-reference on `comments`.
- The like toggle reads then writes, so two simultaneous clicks by the same user can surface a
  primary-key conflict. The data stays correct; an `INSERT ... ON CONFLICT DO NOTHING` would make
  it fully idempotent.
- Usernames and emails are stored lowercase to keep case-insensitive uniqueness simple.

## Troubleshooting

- **`release version 17 not supported`** — a JRE is not enough; install a JDK 17+.
- **Schema validation error on startup** — confirm the `forum` database exists and Flyway ran;
  the app never creates tables itself.
- **Start over** — `docker rm -f forum-postgres`, then run the `docker run` command again.
