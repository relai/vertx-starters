####[Vertx Starter Series](https://github.com/relai/vertx-starters)
# Part II: To-do REST Service

The to-do service is a REST web service backed by MongoDB. It fully supports CRUD of to-do items.

## Project Structure

**Source Code**:

- [`RestApp.java`](src/main/java/demo/starter/vertx/todo/RestApp.java): the main Vert.x verticle of this module, providing the to-do RESTful service.
- [`ToDoHandler.java`](src/main/java/demo/starter/vertx/todo/ToDoHandler.java): the event handler to serve HTTP request by asynchronous query of the MongoDB.
- [`ToDoRestIntTest.java`](src/test/java/demo/starter/vertx/todo/integration/java/ToDoRestIntTest.java): integration test of the REST API, including all CRUD operations.
- [`mod.json`](src/main/resources/mod.json): the mod descriptor.

**Build Files**:

- [`pom.xml`](pom.xml): the Maven build file.
- [`mod.xml`](src/main/assembly/mod.xml): the configuration for the Maven assembly plugin to build the Vert.x mod zip.


## Service Dispatch

REST calls are dispatched by [RouteMatcher](src/main/java/demo/starter/vertx/todo/RestApp.java):

        ToDoHandler handler = new ToDoHandler(getVertx().eventBus());
        RouteMatcher routeMatcher = new RouteMatcher()
                .get("/todos",        handler::findAll)
                .post("/todos",       handler::create)
                .get("/todos/:id",    handler::findById)
                .put("/todos/:id",    handler::update)
                .delete("/todos/:id", handler::delete);

Wow! This really reminds of Node.js. Java and Vert.x have come a long way!

## Integration with MongoDB

The data persistency to the MongoDB is done with the help of io.vertx~mod-mongo-persistor. 

To use the mongo persistor mod, one first needs to deploy the module: 

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

MongoDB uses `_id` to store a record ID, which is a bit idiosyncratic and should be hidden from our clients. [`ToDoHandler`](https://github.com/relai/vertx-starters/blob/master/todoapi/src/main/java/demo/starter/vertx/todo/ToDoHandler.java) takes the responsibility of translating `_id` to `id`.

## Dependency Management and Maven

It is of interest to examine the different approaches that can be utilized to manage dependencies.

### Vert.x Container Dependency

A Vert.x application in general explicitly depends on the Vert.x container, starting from the compile time. This dependency is introduced when the `Verticle` class is extended.

Using Maven terms, this is a "provided" dependency, as it is implicitly guaranteed by the runtime container. In this sample, the dependencies are declared in the parent `pom`:

        <!--Vertx provided dependencies-->
        <dependency>
          <groupId>io.vertx</groupId>
          <artifactId>vertx-core</artifactId>
          <version>${vertx.version}</version>
          <scope>provided</scope>
        </dependency> 
        ...  

### Dependent Mods

Dependent mods are explicitly declared in [`mod.json`](https://github.com/relai/vertx-starters/blob/master/todoapi/src/main/resources/mod.json):

    "includes": "io.vertx~mod-mongo-persistor~2.1.0"

In our example, this is purely a deployment dependency. Loose coupling thanks to the event bus eliminates compile-time dependency. As a result, this dependency is not needed to be declared in pom.

The code can be further refactored to centralize the version control inside `pom`. This can be achieved by [Maven resource filtering](http://maven.apache.org/plugins/maven-resources-plugin/examples/filter.html) as follows:

    "includes": "io.vertx~mod-mongo-persistor~${mod.mongopersistor.version}"

The maven property `mod.mongoperisitor.version` is defined in the parent `pom`.


### Dependent Jars

Compile and runtime dependent jars declared in pom are packaged into the mod lib folder. This is achieved by the maven dependency plugin as defined in the parent [`pom`](https://github.com/relai/vertx-starters/blob/master/pom.xml):

        <plugin>
           <groupId>org.apache.maven.plugins</groupId>
           <artifactId>maven-dependency-plugin</artifactId>
           <version>${maven.dependency.plugin.version}</version>
           <executions>
             <execution>
               <id>copy-mod-dependencies-to-target</id>
               <phase>process-classes</phase>
               <goals>
                 <goal>copy-dependencies</goal>
               </goals>
               <configuration>
                 <outputDirectory>${mods.directory}/${module.name}/lib</outputDirectory>
                 <includeScope>runtime</includeScope>
               </configuration>
             </execution>
           </executions>
         </plugin>

Our sample does not have dependent jars.


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

