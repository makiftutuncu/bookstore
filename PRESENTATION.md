# Testing a Web Backend Application

Backend applications, or applications in general, are often divided into different layers. Bookstore is no exception. It is a typical MVC (model-view-controller) application. It has

* model layer where authors and books are modeled by Scala case classes and database tables
* controller layer where there are CRUD (create-read-update-delete) HTTP endpoints
* view layer where models are mapped to Jsons.

Since bookstore is a backend application and an API, it doesn't have a UI. It only consumes and produces simple Jsons.

In addition to the MVC layers, bookstore has another layer called **service layer**. It is where validations on input data (if any) and business logic is implemented.