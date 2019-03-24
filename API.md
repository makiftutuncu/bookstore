# API Documentation

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
| Already exists    | There is already a book with given isbn                                                      |

---

### DELETE /authors/`authorId`

Deletes an author

#### Example Successful Response

`200 OK` with no body

---

### DELETE /authors/`authorId`/books/`bookId`

Deletes a book of an author

#### Example Successful Response

`200 OK` with no body
