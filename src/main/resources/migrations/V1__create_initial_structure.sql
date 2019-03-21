CREATE TABLE "authors" (
  "id"   UUID NOT NULL PRIMARY KEY,
  "name" TEXT NOT NULL UNIQUE
);

CREATE TABLE "books" (
  "id"         UUID    NOT NULL PRIMARY KEY,
  "name"       TEXT    NOT NULL,
  "isbn"       TEXT    NOT NULL UNIQUE,
  "author_id"  UUID    NOT NULL REFERENCES "authors"("id"),
  "price"      INTEGER NOT NULL
);
