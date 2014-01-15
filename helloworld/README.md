# Vert.x Starter I - Hello World!

We begin with our starter series with the tradition-honored "Hello World" example.

## Lambda and Java 8

By nature of event-driven programming, Vert.x extensively uses functional interfaces as event handlers. The following is a "Hello World" example:

        // "Hello World" in Java 7
        Handler<HttpServerRequest> handler = new Handler<HttpServerRequest>() {
            @Override public void handle(HttpServerRequest request) {
                request.response().end("Hello World!");
            }
        };

        getVertx().createHttpServer()
                  .requestHandler(handler)
                  .listen(8080, "localhost");

The `Handler` class is a functional interface, containing a single handler method. This design choice allows Vert.x to seamlessly interop with functional languages such as Groovy and JavaScript:
        
        // "Hello World" in Groovy
        vertx.createHttpServer()
             .requestHandler({request ->  request.response().end("Hello World!")})
             .listen(8080, "localhost");

Isn't this much less verbose? No wonder many Vert.x official examples are given in Groovy or JavaScript.

Fortunately for Java developers, Lambda finally made it into Java 8, and Vert.x has positioned itself well for this day. The following is the same [HelloVerticle](https://github.com/relai/vertx-starters/blob/master/helloworld/src/main/java/demo/starter/vertx/helloworld/HelloVerticle.java) in Java 8:

        // "Hello World" in Java 8
        getVertx().createHttpServer()
                  .requestHandler(request -> request.response().end("Hello World!"))
                  .listen(8080, "localhost");

Aren't stars all aligned for Vert.x? Isn't it simply the right framework at the right time?


## Integration Test 

Vert.x makes it easy to incorporate integration tests. [HelloIntegrationTest](https://github.com/relai/vertx-starters/blob/master/helloworld/src/test/java/demo/starter/vertx/helloworld/integration/HelloIntegrationTest.java) tests the HTTP server serves the correct content.

## Build  

This module is created using [vertx maven integration](http://vertx.io/maven_dev.html). 

Use `mvn install` to compile, test and package the Vert.x mod. To execute the mode, run command:

    mvn vertx:runMod

The command deploys the "Hello World" mod, bringing up the HTTP server at `http://localhost:8080`.



