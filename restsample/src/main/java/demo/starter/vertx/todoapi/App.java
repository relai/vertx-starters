package demo.starter.vertx.todoapi;

import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

/* 
 * A sample todo REST application.
 *
 * @author <a href="http://relai.blogspot.com/">Re Lai</a>
 */

public class App extends Verticle {

    @Override
    public void start() {
        SimpleRouteHandler handler = new SimpleRouteHandler();
        RouteMatcher routeMatcher = new RouteMatcher()
                .get("/todos",        handler::findAll)
                .post("/todos",       handler::create)
                .get("/todos/:id",    handler::findById)
                .put("/todos/:id",    handler::update)
                .delete("/todos/:id", handler::delete);
        
        getVertx().createHttpServer()
                  .requestHandler(routeMatcher)
                  .listen(8080, "localhost");
    }
}
