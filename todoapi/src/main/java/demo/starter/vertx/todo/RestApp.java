package demo.starter.vertx.todo;

import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/* 
 * A sample todo REST application.
 *
 * @author <a href="http://relai.blogspot.com/">Re Lai</a>
 */

public class RestApp extends Verticle {

    @Override
    public void start() {
        // Deploy the mongo persistor
        JsonObject dbConfig = new JsonObject()
            .putString("address", ToDoHandler.TODO_PERSISTOR)
            .putString("db_name", "todos");
        getContainer().deployModule("io.vertx~mod-mongo-persistor~2.1.0", 
            dbConfig);
        
        // Set up the route matcher
        ToDoHandler handler = new ToDoHandler(getVertx().eventBus());
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
