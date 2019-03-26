# Bookstore

## Table of Contents

1. [Introduction](#introduction)
2. [Configuration](#configuration)
3. [Development](#development)
4. [Testing](#testing)
5. [Contributing](#contributing)
6. [License](#license)

## Introduction

Bookstore is a backend application written in Scala. It is a dummy REST API for authors and books. Its main purpose is to showcase how a backend application can be separated into layers and how they can be tested via different types of automated tests. For more details, see [PRESENTATION.md](PRESENTATION.md).

At its core, bookstore is a [Finch](<https://github.com/finagle/finch>) app. To see all external dependencies, check out [build.sbt](build.sbt) file.

A detailed API documentation is in [API.md](API.md).

## Configuration

Bookstore can be configured via [application.conf](src/main/resources/application.conf) and [test.conf](src/test/resources/test.conf) files for running and testing respectively. You can also override any config with following environment variables.

| Variable Name     | Data Type | Description                |
| ----------------- | --------- | -------------------------- |
| BOOKSTORE_DB_HOST | String    | Database host              |
| BOOKSTORE_DB_PORT | Int       | Database port              |
| BOOKSTORE_DB_NAME | String    | Database name              |
| BOOKSTORE_DB_USER | String    | Database user              |
| BOOKSTORE_DB_PASS | String    | Database user's password   |
| BOOKSTORE_PORT    | Int       | Application's running port |

## Development and Running

Bookstore is built with SBT. So, standard SBT tasks like `clean`, `compile` and `run` can be used.

In order to get the database set up, you may simply use `docker-compose` by doing

```docker-compose up -d```

This will fire up a PostgreSQL database for running the application and another one for running tests.

## Testing

To run all the tests, use `test` task of SBT.

To run specific test(s), use `testOnly fullyQualifiedTestClassName1 fullyQualifiedTestClassName2 ...`

## Contributing

Perhaps there is not much to develop here but all contributions are more than welcome. Please feel free to send a pull request for your contributions. Thank you.

## License

Bookstore is licensed with MIT License. See [LICENSE.md](LICENSE.md) for details.

