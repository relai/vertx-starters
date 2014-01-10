package demo.starter.vertx.todoapi.integration.java;

/*  
 * Integration Test for todoapi App
 *
 * @author <a href="http://relai.blogspot.com/">Re Lai</a>
 */

import static org.vertx.testtools.VertxAssert.assertNotNull;
import static org.vertx.testtools.VertxAssert.assertTrue;
import static org.vertx.testtools.VertxAssert.testComplete;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import static org.vertx.testtools.VertxAssert.assertEquals;

public class ToDoRestIntTest extends TestVerticle {

    @Test
    public void getAll() {
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

    @Test
    public void getItem1234() {
        createHttpClient().get("/todos/1234",
            (HttpClientResponse resp) -> {
                assertEquals(200, resp.statusCode());
                resp.bodyHandler((Buffer data) -> {
                    getContainer().logger().info("get 1234: " + data.toString());
                    testComplete();
                });
            }
        ).end();
    }

    @Test
    public void createTask() {
        HttpClientRequest request = createHttpClient().post("/todos",             
            (HttpClientResponse resp) -> {            
                assertEquals(201, resp.statusCode());

                resp.bodyHandler((Buffer data) -> {
                    container.logger().info("create: " + data.toString());
                    testComplete();
                });
            }
        );

        JsonObject task = new JsonObject()
            .putString("title", "Walk dogs")
            .putNumber("order", 2)
            .putBoolean("completed", false);
        Buffer content = new Buffer(task.toString());

        request.headers()
            .add("Content-Length", String.valueOf(content.length()));
        request.write(content)
            .end();
    }

    @Test
    public void updateTask() {        
        HttpClientRequest request = createHttpClient().put("/todos/2", 
            (HttpClientResponse resp) -> {
                container.logger().info("Put status: " + resp.statusCode());
                testComplete();
        });

        JsonObject task = new JsonObject()
            .putNumber("order", 2)
            .putString("title", "Have some fun")
            .putBoolean("completed", false);
        Buffer content = new Buffer(task.toString());
        request.headers()
               .add("Content-Length", String.valueOf(content.length()));
        request.write(content)
               .end();
    }

    @Test
    public void deleteTask() {
        createHttpClient().delete("/todos/2", 
            (HttpClientResponse resp) -> {
                getContainer().logger().info("delete 2: " + resp.statusCode());
                testComplete();            
            }
        ).end();
    }

    @Override
    public void start() {
        initialize();
        getContainer().deployModule(System.getProperty("vertx.modulename"),
        //getContainer().deployVerticle("demo.jdk8.TaskRestVerticle", 
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
