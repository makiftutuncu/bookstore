# Testing a Web Backend Application

## Introduction and Layers

Backend applications, or applications in general, are often divided into different layers. Bookstore is no exception. It is a typical MVC (model-view-controller) application. It has

* model - where authors and books are modeled by Scala case classes and database tables
* view - where models are mapped to Jsons
* controller - where there are CRUD (create-read-update-delete) HTTP endpoints

Since bookstore is a backend application and an API, it doesn't have a UI. It only consumes and produces simple Jsons.

In addition to standard MVC layers, bookstore additional layers.

* service layer - where validations on input data (if any) and business logic is implemented
* DAO (data acceess object) - where all access to database happen

## Test Types

There are different types of automated tests for a backend appplication. They vary according to their scopes, their approaches and methodologies. To list some, there are

* unit tests - very small scope, whitebox approach, mock everything else
* integration tests - bigger scope (integration of more than one components), whitebox but not strict, no mocks unless absolutely neccessary
* functional tests - even bigger scope (from almost outside world), blackbox, no mocks unless absolutely neccessary
* end-to-end API tests - biggest scope (from outside world, all components), blackbox, no mocks

While types of tests needed depends on the project, here's an illustration (test pyramid) explaining different test types along with their running speed, cost of finding an error with the tests and the amount of tests needed for that type.

![https://martinfowler.com/bliki/TestPyramid.html](test-pyramid.png)

To summarize the pyramid:

* Unit tests run much faster, finding problems with unit tests are cheap because it finds problems during development and there needs to be lost of unit tests covering every possible unit.
* Service tests (these can be integration and functional tests for backends) run slower, finding problems with service tests can cost you more than unit tests and there are usually less service tests than unit tests.
* UI tests (manual tests or end-to-end tests) run much slower and they cost much more. They are usually the least amount of tests in the mix.

## The Approach in Bookstore

There are no tests for view layer or models because there is no custom logic or anything other than plain data.

There are unit tests for service layer and other utilities. These don't call real components. Other components like DB, DAOs and other services are all mocked.

There are integration tests for DAO layer. These do call real components. Only exception is when an unexpected database error is needed. Only this case uses a mock database. Other than that, all components are real, including an actual DB.

There are functional tests for controllers. They are blackbox tests according to API documentation. They give input, they expect a certain output. Occasionally, they check the integrity of the data.

Finally there are end to end API tests with a Postman Collection. These are also blackbox tests and nothing more than previously set up HTTP requests.

