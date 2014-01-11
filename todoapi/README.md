# Vert.x Starter - TO-do REST Service

This is a sample application of to-do REST service. The data is only held in memory instead of being persisted in a database.


## Service Dispatch

REST calls are dispatched by RouteMatcher:

        SimpleRouteHandler handler = new SimpleRouteHandler();
        RouteMatcher routeMatcher = new RouteMatcher()
                .get("/todos",        handler::findAll)
                .post("/todos",       handler::create)
                .get("/todos/:id",    handler::findById)
                .put("/todos/:id",    handler::update)
                .delete("/todos/:id", handler::delete);

This really reminds of Node.js. Vert.x, and Java, really have come a long way!!

## Integration Test

The `ToDoRestIntTest` tests fully all CRUD operations.

## To-do REST Service Sample Data

The following is a list of the sample data of CRUD operations of the todos REST service:

### GET http://localhost:8080/todos

 HTTP/1.1 200 OK
 Content-Length: 64

`[{"id":"1234","order":1,"title":"Buy coffee","completed":false}]`


### GET http://localhost:8080/todos/1234 

 HTTP/1.1 200 OK
 Content-Length: 62

`{"id":"1234","order":1,"title":"Buy coffee","completed":false}`


### POST http://localhost:8080/todos 

Request Payload: `{"order":2, "title":"Buy milk"", "completed":false}`

HTTP/1.1 201 OK
Location: /todos/e08150ca-d0ca-4973-b4b7-43a74c835e68
Content-Length: 45

`{"id":"e08150ca-d0ca-4973-b4b7-43a74c835e68"}`


### PUT http://localhost:8080/todos/1234

Request Payload: `{"id":"1234","order":1,"title":"Buy coffee","completed":true}`


HTTP/1.1 204 OK
Content-Length: 0

### DELETE http://localhost:8080/todos/1234 

HTTP/1.1 204 OK
Content-Length: 0

