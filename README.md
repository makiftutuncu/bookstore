# Bookstore

## Table of Contents

1. [Introduction](#introduction)
2. [API Documentation](#api-documentation)
3. [Development](#development)
4. [Testing](#testing)
5. [Contributing](#contributing)
6. [License](#license)

## Introduction

TODO

## API Documentation

Here is an overview of the APIs:

| Method | URL                                | Description                                         |
| ------ | ---------------------------------- | --------------------------------------------------- |
| GET    | /authors                           | Get all authors                                     |
| GET    | /authors?name=`name`               | Get authors by `name`                               |
| GET    | /authors/`authorId`                | Get author with `authorId`                          |
| POST   | /authors                           | Create a new author                                 |
| PUT    | /authors/`authorId`                | Update author with `authorId`                       |
| DELETE | /authors/`authorId`                | Delete author with `authorId`                       |
| GET    | /books                             | Get all books                                       |
| GET    | /books?name=`name`                 | Get books by `name`                                 |
| GET    | /authors/`authorId`/books/`bookId` | Get book with `bookId` of author with `authorId`    |
| POST   | /authors/`authorId`/books          | Create a book for author with `authorId`            |
| PUT    | /authors/`authorId`/books/`bookId` | Update book with `bookId` of author with `authorId` |
| DELETE | /authors/`authorId`/books/`bookId` | Delete book with `bookId` of author with `authorId` |

TODO

## Development

TODO

## Testing

TODO

## Contributing

TODO

## License

TODO

