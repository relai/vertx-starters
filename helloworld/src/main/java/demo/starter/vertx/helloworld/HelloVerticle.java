package demo.starter.vertx.helloworld;

/* 
 * A "Hello World" sample of a vert.x http server.
 *
 * @author <a href="http://relai.blogspot.com/">Re Lai</a>
 */
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;

public class HelloVerticle extends Verticle {

    @Override
    public void start() {
        getVertx().createHttpServer()
                  .requestHandler((HttpServerRequest request) -> 
                        request.response().end("Hello World!"))
                  .listen(8080, "localhost");
    }
}
