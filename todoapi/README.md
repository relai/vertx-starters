# Vert.x Starter II - TO-do REST Service

The to-do service is a REST web service backed by MongoDB. It fully supports CRUD of to-do items.

## Service Dispatch

REST calls are dispatched by RouteMatcher:

        ToDoHandler handler = new ToDoHandler(getVertx().eventBus());
        RouteMatcher routeMatcher = new RouteMatcher()
                .get("/todos",        handler::findAll)
                .post("/todos",       handler::create)
                .get("/todos/:id",    handler::findById)
                .put("/todos/:id",    handler::update)
                .delete("/todos/:id", handler::delete);

Wow! This really reminds of Node.js. Java and Vert.x has come a long way!

## Integration with MongoDB

The data persistency to the MongoDB is done with the help of io.vertx~mod-mongo-persistor. To use the mongo persistor mod, one first needs to deploy the module: 

        JsonObject dbConfig = new JsonObject()
            .putString("address", ToDoHandler.TODO_PERSISTOR)
            .putString("db_name", "todos");
        getContainer().deployModule("io.vertx~mod-mongo-persistor~2.1.0", 
            dbConfig);

Invocation to the persistor is made through the Vert.x event bus. The following shows how to find an item by Id:

        // req - HttpServerRequest
        String id = req.params().get("id");
        JsonObject command = new JsonObject()
            .putString("action", "findone")
            .putString("collection", "item")
            .putObject("matcher", new JsonObject().putString("_id", id));
        eventBus.send("todo.mongopersistor", command, (Message<JsonObject> reply) -> {
            JsonObject result = reply.body().getObject("result");
            req.response().end(result.encode());
        });

All communication is made via the event bus. This is a nice architecture choice by Vert.x, making the client code only loosely coupled with the mods it depends. 

MongoDB uses `_id` to store a record ID, which is a bit idiosyncratic and should be hidden from our clients. `ToDoHandler` takes the responsibility of translating `_id` to `id`.

## Dependency Management

It is a good idea to explicitly declare the project's dependency in its mod.json:

    "includes": "io.vertx~mod-mongo-persistor~2.1.0"

## Integration Test

The `ToDoRestIntTest` tests fully all CRUD operations.

## To-do REST Service Sample Data

The following are sample service calls:

POST http://localhost:8080/todos 

    Request Payload: `{"order":1, "title":"Buy coffee"", "completed":false}`

    HTTP/1.1 201 OK
    Location: /todos/55e1eaaf-772a-4c0c-a3c8-ebf770b7c0f0
    Content-Length: 45
    {"id":"55e1eaaf-772a-4c0c-a3c8-ebf770b7c0f0"}

GET http://localhost:8080/todos

    HTTP/1.1 200 OK
    Content-Length: 96
    [{"title":"Buy coffee","order":1,"completed":false,"id":"55e1eaaf-772a-4c0c-a3c8-ebf770b7c0f0"}]


GET http://localhost:8080/todos/55e1eaaf-772a-4c0c-a3c8-ebf770b7c0f0 

    HTTP/1.1 200 OK
    Content-Length: 94
    {"title":"Buy coffee","order":1,"completed":false,"id":"55e1eaaf-772a-4c0c-a3c8-ebf770b7c0f0"}


PUT http://localhost:8080/todos/55e1eaaf-772a-4c0c-a3c8-ebf770b7c0f0

    Request Payload: {"title":"Buy coffee","order":1,"completed":true,"id":"55e1eaaf-772a-4c0c-a3c8-ebf770b7c0f0"}

    HTTP/1.1 204 OK
    Content-Length: 0

DELETE http://localhost:8080/todos/55e1eaaf-772a-4c0c-a3c8-ebf770b7c0f0

    HTTP/1.1 204 OK
    Content-Length: 0

