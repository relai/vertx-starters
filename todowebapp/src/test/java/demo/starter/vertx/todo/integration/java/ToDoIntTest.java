package demo.starter.vertx.todo.integration.java;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.testtools.TestVerticle;
import static org.vertx.testtools.VertxAssert.*;

/*  
 * Integration Test for todo web app
 *
 * @author <a href="http://relai.blogspot.com/">Re Lai</a>
 */

public class ToDoIntTest extends TestVerticle {

        
    @Test
    public void loadIndexPage() {
        createHttpClient().get("/",
            (HttpClientResponse resp) -> {
                assertEquals(200, resp.statusCode());
                resp.bodyHandler((Buffer data) -> {
                    String page = data.toString();
                    assertTrue(page.indexOf("Re Lai") > 0);                    
                    testComplete();
                });
            }
        ).end();
    }
    
    @Test
    public void testRestAPI() {
        createHttpClient().get("/todos",
            (HttpClientResponse resp) -> {
                assertEquals(200, resp.statusCode());
                resp.bodyHandler((Buffer data) -> {
                    getContainer().logger().info("get all: " + data.toString());
                    testComplete();
                });
            }
        ).end();
    }

   

    @Override
    public void start() {
        initialize();
        getContainer().deployModule(System.getProperty("vertx.modulename"),
            (AsyncResult<String> asyncResult) -> {
                assertTrue(asyncResult.succeeded());
                assertNotNull("deploymentID should not be null", 
                              asyncResult.result());
                startTests();
        });
    }


    private HttpClient createHttpClient() {
        return getVertx().createHttpClient()
                         .setHost("localhost")
                         .setPort(8080);
    }
}
