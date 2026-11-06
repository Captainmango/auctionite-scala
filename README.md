# auctionite-scala

An auction site built with [Play Framework](https://www.playframework.com/) (Scala 3), PostgreSQL, and Redis.

## Stack

- **Play Framework 3** on Scala 3.9 with Guice DI
- **PostgreSQL 18** for persistence
- **Redis 8** for caching
- **Flyway** for database migrations
- **scalafmt** / **scalafix** for formatting and linting

## Getting started

1. Copy `.env.example` to `.env` and adjust if needed (defaults work with docker-compose).
2. Start the database and cache:
   ```sh
   docker compose up -d
   ```
3. Apply migrations:
   ```sh
   sbt flywayMigrate
   ```
4. Run the app:
   ```sh
   sbt run
   ```

## Migrations

Create a new empty migration under `conf/db/migration`:

```sh
sbt 'newMigration <description>'
```

## Testing

```sh
sbt test
```

Tests run against a separate database (`TEST_DATABASE_NAME`, default `auctionite_test`).

## Formatting

```sh
sbt scalafmtAll    # format
sbt scalafixAll    # lint/fix
```
