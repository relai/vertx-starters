package demo.starter.vertx.todo;

import org.vertx.java.core.http.RouteMatcher;
import org.vertx.mods.web.WebServerBase;

/* 
 * The Web Serer
 *
 * @author <a href="http://relai.blogspot.com/">Re Lai</a>
 */

public class WebServer extends WebServerBase {
    @Override
    protected RouteMatcher routeMatcher() {
        ToDoHandler handler = new ToDoHandler(getVertx().eventBus());
        RouteMatcher matcher = new RouteMatcher()
                .get("/todos",        handler::findAll)
                .post("/todos",       handler::create)
                .get("/todos/:id",    handler::findById)
                .put("/todos/:id",    handler::update)
                .delete("/todos/:id", handler::delete)        
                .noMatch(staticHandler());
        return matcher;
    }
}
