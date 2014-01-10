# Vert.x for Starters - Hello World!

A "Hello World" example of a vert.x HTTP server.

## [HelloVerticle](https://github.com/relai/vertx-starters/blob/master/helloworld/src/main/java/demo/starter/vertx/helloworld/HelloVerticle.java)

A simple "Hello World" server:

        getVertx().createHttpServer()
                  .requestHandler((HttpServerRequest request) -> 
                        request.response().end("Hello World!"))
                  .listen(8080, "localhost");

It is hard to recognize this is actually Java code. Java in general, and vert.x in particular, has incorporated the current trends of fluent-style and lambda programming.

## Integration Test 

It is worth noting that vert.x makes it easy to incorporate integration tests as part of the build process. [HelloIntegrationTest](https://github.com/relai/vertx-starters/blob/master/helloworld/src/test/java/demo/starter/vertx/helloworld/integration/HelloIntegrationTest.java) tests our HTTP server serves the correct content.

## Build  

This module is created using [vertx maven integration](http://vertx.io/maven_dev.html). The following are common maven commands:

*  mvn clean
*  mvn install
*  mvn vertx:runMod

The last command brings up the HTTP server. You can point to it using a web browser:

http://localhost:8080



