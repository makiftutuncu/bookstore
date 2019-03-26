# API Documentation

Here is an overview of the APIs:

| Method | URL                                | Link                                       |
| ------ | ---------------------------------- | ------------------------------------------ |
| GET    | /authors                           | [Jump](#get-authors)                       |
| GET    | /authors/`authorId`                | [Jump](#get-authorsauthorid)               |
| GET    | /authors/`authorId`/books          | [Jump](#get-authorsauthoridbooks)          |
| GET    | /authors/`authorId`/books/`bookId` | [Jump](#get-authorsauthoridbooksbookid)    |
| GET    | /authors?name=`name`               | [Jump](#get-authorsnamename)               |
| GET    | /books                             | [Jump](#get-books)                         |
| GET    | /books?name=`name`                 | [Jump](#get-booksnamename)                 |
| POST   | /authors                           | [Jump](#post-authors)                      |
| POST   | /authors/`authorId`/books          | [Jump](#post-authorsauthoridbooks)         |
| PUT    | /authors/`authorId`                | [Jump](#put-authorsauthorid)               |
| PUT    | /authors/`authorId`/books/`bookId` | [Jump](#put-authorsauthoridbooksbookid)    |
| DELETE | /authors/`authorId`                | [Jump](#delete-authorsauthorid)            |
| DELETE | /authors/`authorId`/books/`bookId` | [Jump](#delete-authorsauthoridbooksbookid) |

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

All successful responses will have `200 OK` status unless explicitly mentioned below.

Below are more details about each endpoint.

---

### GET /authors

Returns a list of all authors as a Json array

#### Example Successful Response

```json
[
  {
    "id": "UUID-1",
    "name": "Author 1"
  },
  {
    "id": "UUID-2",
    "name": "Author 2"
  }
]
```

---

### GET /authors/`authorId`

Returns an author as a Json object

#### Example Successful Response

```json
{
  "id": "UUID-1",
  "name": "Author 1"
}
```

#### Possible Errors

| What      | When                             |
| --------- | -------------------------------- |
| Not found | There is no author with given id |

---

### GET /authors/`authorId`/books

**TODO: This is not implemented yet!**

Returns a list of books of an author as a Json array

#### Example Successful Response

```json
[
  {
    "id": "UUID-1",
    "isbn": "ISBN-1",
    "name": "Book 1",
    "author": "Author 1",
    "price": 100
  },
  {
    "id": "UUID-2",
    "isbn": "ISBN-2",
    "name": "Book 2",
    "author": "Author 1",
    "price": 200
  }
]
```

#### Possible Errors

| What      | When                             |
| --------- | -------------------------------- |
| Not found | There is no author with given id |

---

### GET /authors/`authorId`/books/`bookId`

Returns a book of an author as a Json object

#### Example Successful Response

```json
{
  "id": "UUID-1",
  "isbn": "ISBN-1",
  "name": "Book 1",
  "author": "Author 1",
  "price": 100
}
```

#### Possible Errors

| What      | When                                                                                         |
| --------- | -------------------------------------------------------------------------------------------- |
| Not found | There is no author with given id, no book with given id or no book whose author has given id |

---

### GET /authors?name=`name`

Returns authors with matching names as a Json array

#### Example Successful Response

```json
[
  {
    "id": "UUID-1",
    "name": "Author 1"
  },
  {
    "id": "UUID-2",
    "name": "Author 2"
  }
]
```

---

### GET /books

Returns a list of all books as a Json array

#### Example Successful Response

```json
[
  {
    "id": "UUID-1",
    "isbn": "ISBN-1",
    "name": "Book 1",
    "author": "Author 1",
    "price": 100
  },
  {
    "id": "UUID-2",
    "isbn": "ISBN-2",
    "name": "Book 2",
    "author": "Author 2",
    "price": 200
  }
]
```

---

### GET /books?name=`name`

Returns books with matching names as a Json array

#### Example Successful Response

```json
[
  {
    "id": "UUID-1",
    "isbn": "ISBN-1",
    "name": "Book 1",
    "author": "Author 1",
    "price": 100
  },
  {
    "id": "UUID-2",
    "isbn": "ISBN-2",
    "name": "Book 2",
    "author": "Author 2",
    "price": 200
  }
]
```

---

### POST /authors

Creates a new author with given data and returns created author

#### Example Payload

```json
{
  "name": "Author 1"
}
```

#### Example Successful Response

`201 Created` with following body

```json
{
  "id": "UUID-1",
  "name": "Author 1"
}
```

#### Possible Errors

| What           | When                                       |
| -------------- | ------------------------------------------ |
| Already exists | There is already an author with given name |

---

### POST /authors/`authorId`/books

Creates a new book of an author with given data and returns created book

#### Example Payload

```json
{
  "isbn": "ISBN-1",
  "name": "Book 1",
  "price": 100
}
```

#### Example Successful Response

`201 Created` with following body

```json
{
  "id": "UUID-1",
  "isbn": "ISBN-1",
  "name": "Book 1",
  "author": "Author 1",
  "price": 100
}
```

#### Possible Errors

| What           | When                                    |
| -------------- | --------------------------------------- |
| Required       | There is no author with given id        |
| Already exists | There is already a book with given isbn |

---

### PUT /authors/`authorId`

Updates an author with given data and returns updated author

#### Example Payload

```json
{
  "name": "Author 1"
}
```

#### Example Successful Response

```json
{
  "id": "UUID-1",
  "name": "Author 1"
}
```

#### Possible Errors

| What              | When                                       |
| ----------------- | ------------------------------------------ |
| Unexpected action | There is no author with given id           |
| Already exists    | There is already an author with given name |

---

### PUT /authors/`authorId`/books/`bookId`

Updates a book of an author with given data and returns updated book

#### Example Payload

```json
{
  "isbn": "ISBN-1",
  "name": "Book 1",
  "price": 100
}
```

#### Example Successful Response

```json
{
  "id": "UUID-1",
  "isbn": "ISBN-1",
  "name": "Book 1",
  "author": "Author 1",
  "price": 100
}
```

#### Possible Errors

| What              | When                                                                                         |
| ----------------- | -------------------------------------------------------------------------------------------- |
| Unexpected action | There is no author with given id, no book with given id or no book whose author has given id |

---

### DELETE /authors/`authorId`

Deletes an author

#### Example Successful Response

`200 OK` with no body

#### Possible Errors

| What              | When                                   |
| ----------------- | ---------------------------------------|
| Unexpected action | There is no author with given id       |
| In use            | There are books assigned to the author |

---

### DELETE /authors/`authorId`/books/`bookId`

Deletes a book of an author

#### Example Successful Response

`200 OK` with no body

#### Possible Errors

| What              | When                                                                                         |
| ----------------- | -------------------------------------------------------------------------------------------- |
| Unexpected action | There is no author with given id, no book with given id or no book whose author has given id |

