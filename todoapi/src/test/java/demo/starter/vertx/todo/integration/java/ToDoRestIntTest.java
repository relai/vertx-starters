package demo.starter.vertx.todo.integration.java;

/*  
 * Integration Test for todoapi App
 *
 * @author <a href="http://relai.blogspot.com/">Re Lai</a>
 */

import org.junit.Test;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class ToDoRestIntTest extends TestVerticle {
   
    private String itemId;
    
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
    public void testItemCRUD() {
        // Starting with creating an item first
        // We'll then chain tests agaist the item to update, get and delete
        HttpClientRequest request = createHttpClient().post("/todos",             
            (HttpClientResponse resp) -> {            
                assertEquals(201, resp.statusCode());

                resp.bodyHandler((Buffer data) -> {
                    container.logger().info("create: " + data.toString());
            
                    JsonObject payload = new JsonObject(data.toString());
                    itemId = payload.getString("id");
                    
                    updateItem();
                });
            }
        );

        JsonObject task = new JsonObject()
            .putString("title", "Create integration tests")
            .putNumber("order", 100)
            .putBoolean("completed", false);
        Buffer content = new Buffer(task.toString());

        request.headers()
            .add("Content-Length", String.valueOf(content.length()));
        request.write(content)
            .end();
    }

   
    private void updateItem() {        
        HttpClientRequest request = createHttpClient().put("/todos/" + itemId, 
            (HttpClientResponse resp) -> {
                container.logger().info("Put status: " + resp.statusCode());
                
                getItem();
        });

        JsonObject task = new JsonObject()
            .putNumber("order", 100)
            .putString("title", "Create integration tests")
            .putBoolean("completed", true);
        Buffer content = new Buffer(task.toString());
        request.headers()
               .add("Content-Length", String.valueOf(content.length()));
        request.write(content)
               .end();
    }


    private void getItem() {
        createHttpClient().get("/todos/" + itemId,
            (HttpClientResponse resp) -> {
                assertEquals(200, resp.statusCode());
                
                resp.bodyHandler((Buffer data) -> {
                    String payload = data.toString();
                    getContainer().logger().info("get: " + payload);
                    assertTrue(payload.contains(itemId));
                    
                    deleteItem();
                });
            }
        ).end();
    }
    

    private void deleteItem() {
        createHttpClient().delete("/todos/" + itemId, 
            (HttpClientResponse resp) -> {
                getContainer().logger().info("delete: " + resp.statusCode());
                testComplete();            
            }
        ).end();
    }

    @Override
    public void start() {
        initialize();
        
        // Normally one should use "vertx.modulename" system property to retrieve the vertx module name.
        // However, in the multi-module case, the property incorrectly points to the parent pom.
        // To workaround, "module.name" system property is added to pom.xml
        // to point to the correct module name.
        getContainer().deployModule(System.getProperty("module.name"),
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
