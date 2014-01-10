package demo.starter.vertx.todo;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/* 
 * The verticle of the To Do Web Application
 *
 * @author <a href="http://relai.blogspot.com/">Re Lai</a>
 */

public class App extends Verticle {

    static final String TODO_PERSISTOR = "todo.mongopersistor";
    
    @Override
    public void start() {
        // Deploy the mongo persistor
        JsonObject dbConfig = new JsonObject()
            .putString("address", TODO_PERSISTOR)
            .putString("db_name", "todos");
        getContainer().deployModule("io.vertx~mod-mongo-persistor~2.1.0", 
            dbConfig);
        
        // Deploy the web server
        JsonObject webConfig = new JsonObject()
            .putString("host", "localhost")
            .putBoolean("route_matcher", true)
            .putNumber("port", 8080);
        getContainer().deployVerticle("demo.starter.vertx.todo.WebServer", 
            webConfig);        
    }
}
