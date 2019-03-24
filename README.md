# Bookstore

## Table of Contents

1. [Introduction](#introduction)
2. [API Documentation](#api-documentation)
3. [Development](#development)
4. [Testing](#testing)
5. [Contributing](#contributing)
6. [License](#license)

## Introduction

Bookstore is a backend application written in Scala. It is a dummy REST API for authors and books. Its main purpose is to showcase how a backend application can be separated into layers and how they can be tested via different types of automated tests.

At its core, bookstore is a [Finch](<https://github.com/finagle/finch>) app. To see all external dependencies, check out [build.sbt](build.sbt) file.

## API Documentation

Here is an overview of the APIs:

| Method | URL                                | Description                               |
| ------ | ---------------------------------- | ----------------------------------------- |
| GET    | /authors                           | Get all authors                           |
| GET    | /authors/`authorId`                | Get author `authorId`                     |
| GET    | /authors/`authorId`/books/`bookId` | Get book `bookId` of author `authorId`    |
| GET    | /authors?name=`name`               | Get authors by `name`                     |
| GET    | /books                             | Get all books                             |
| GET    | /books?name=`name`                 | Get books by `name`                       |
| POST   | /authors                           | Create a new author                       |
| POST   | /authors/`authorId`/books          | Create a book for author `authorId`       |
| PUT    | /authors/`authorId`                | Update author `authorId`                  |
| PUT    | /authors/`authorId`/books/`bookId` | Update book `bookId` of author `authorId` |
| DELETE | /authors/`authorId`                | Delete author `authorId`                  |
| DELETE | /authors/`authorId`/books/`bookId` | Delete book `bookId` of author `authorId` |

All handled errors return an error Json in following format:

```json
{
  "code": 400,
  "error": "some-error-type",
  "message": "A human readable description of the error",
  "details": {
    "key": "value"
  }
}
```

with an HTTP status same as `code` field.

## Development and Running

Bookstore is built with SBT. So, standard SBT tasks like `clean`, `compile` and `run` can be used.

## Testing

To run all the tests, use `test` task of SBT.

To run specific test(s), use `testOnly fullyQualifiedTestClassName1 fullyQualifiedTestClassName2 ...`

## Contributing

Perhaps there is not much to develop here but all contributions are more than welcome. Please feel free to send a pull request for your contributions. Thank you.

## License

Bookstore is licensed with MIT License. See [LICENSE.md](LICENSE.md) for details.

