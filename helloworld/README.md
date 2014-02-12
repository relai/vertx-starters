####[Vertx Starter Series](https://github.com/relai/vertx-starters)
# Part I: Hello World!

Our starter series begins with the traditional "Hello World" example.

## Project Structure

The standard Vert.x [project structure](http://vertx.io/dev_guide.html) is followed. 

**The source code**:

- [`HelloVerticle.java`](https://github.com/relai/vertx-starters/blob/master/helloworld/src/main/java/demo/starter/vertx/helloworld/HelloVerticle.java): 
the main Vert.x verticle of this module, creating an HTTP server to serve "Hell World!".
- [`mod.json`](https://github.com/relai/vertx-starters/blob/master/helloworld/src/main/resources/mod.json): the mod descriptor
- [`HelloIntegrationTest.java`](https://github.com/relai/vertx-starters/blob/master/helloworld/src/test/java/demo/starter/vertx/helloworld/integration/HelloIntegrationTest.java):
the integration test of the module.

**The build files**:

* [`pom.xml`](https://github.com/relai/vertx-starters/blob/master/helloworld/pom.xml): the Maven build file.
* [`mod.xml`](https://github.com/relai/vertx-starters/blob/master/helloworld/src/main/assembly/mod.xml): the configuration for the Maven assembly plugin to build the Vert.x mod zip.

## Lambda and Java 8

By its nature of event-driven programming, Vert.x extensively uses functional interfaces as event handlers. The following is a "Hello World" example:

        // "Hello World" in Java 7
        Handler<HttpServerRequest> handler = new Handler<HttpServerRequest>() {
            @Override public void handle(HttpServerRequest request) {
                request.response().end("Hello World!");
            }
        };

        getVertx().createHttpServer()
                  .requestHandler(handler)
                  .listen(8080, "localhost");
             

The `Handler` class is a functional interface. This design choice allows Vert.x to seamlessly interop with functional languages such as Groovy and JavaScript:
        
        // "Hello World" in Groovy
        vertx.createHttpServer()
             .requestHandler {request ->  request.response().end("Hello World!")}
             .listen(8080, "localhost");

This is much better. No wonder many Vert.x official examples are given in Groovy or JavaScript.

Fortunately for Java developers, [lambda](http://relai.blogspot.com/2013/11/java-lambda-cheat-sheet.html) finally made it in Java 8. Vert.x has positioned itself well to fully take advantage of it. The following is the same [HelloVerticle](https://github.com/relai/vertx-starters/blob/master/helloworld/src/main/java/demo/starter/vertx/helloworld/HelloVerticle.java) in Java 8:

        // "Hello World" in Java 8
        getVertx().createHttpServer()
                  .requestHandler(request -> request.response().end("Hello World!"))
                  .listen(8080, "localhost");

Aren't all stars aligned for Vert.x? It is such a wonderful framework, just at the right time.


## Maven Integration

With all common elements extracted into the parent `pom`, the project `pom` is quite simple and straightforward.

Common Maven commands:
* `mvn verify` to execute integration tests.
* `mvn install` to compile, test and package the Vert.x mod. 
* `mvn vertx:runMod` to deploy and run the Vert.x mod. The command brings up the HTTP server at `http://localhost:8080` in this example.

## Tests and Debugging

Vert.x makes it easy to incorporate unit tests and integration tests. This module contains one integration test, [HelloIntegrationTest](https://github.com/relai/vertx-starters/blob/master/helloworld/src/test/java/demo/starter/vertx/helloworld/integration/HelloIntegrationTest.java), testing whether the HTTP server serves the correct content. The module does not have unit tests.

Tests are invoked as part of `mvn install`. To execute integration tests specifically, run `mvn verify`. 

You can [run or debug](http://vertx.io/dev_guide.html#run-tests-in-your-ide) the integration test directly within your IDE, without special set-up.

