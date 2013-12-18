package demo.starter.vertx.helloworld;

/* 
 * A "Hello World" sample of a vert.x http server.
 *
 * @author <a href="http://relai.blogspot.com/">Re Lai</a>
 */

import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;

public class HelloVerticle extends Verticle {

    @Override
    public void start() {
        HttpServer server = getVertx().createHttpServer();

        server.requestHandler((HttpServerRequest request) -> {
            request.response().putHeader("Content-Type", "text/plain");
            request.response().end("Hello World!");
        });

        server.listen(8080, "localhost");
    }
}
