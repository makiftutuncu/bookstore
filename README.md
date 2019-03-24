# Bookstore

## Table of Contents

1. [Introduction](#introduction)
2. [API Documentation](API.md)
3. [Development](#development)
4. [Testing](#testing)
5. [Contributing](#contributing)
6. [License](#license)

## Introduction

Bookstore is a backend application written in Scala. It is a dummy REST API for authors and books. Its main purpose is to showcase how a backend application can be separated into layers and how they can be tested via different types of automated tests.

At its core, bookstore is a [Finch](<https://github.com/finagle/finch>) app. To see all external dependencies, check out [build.sbt](build.sbt) file.

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

