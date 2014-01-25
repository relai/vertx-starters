package demo.starter.vertx.todo.integration.java;

/**  
 * Integration Test for todoapi App
 *
 * @author <a href="http://relai.blogspot.com/">Re Lai</a>
 */

import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    /**
     * Tests get all items.
     */
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

    /**
     * Tests CRUD operations of an item. It starts with creating an item first. 
     * It then updates, gets and deletes the item.
     */
    @Test
    public void testItemCRUD() {
          
        // Starts with creating an item by POST
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

    // Updates an item by PUT 
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


    // Gets an item by GET
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
    

    // Deletes an item by DELETE
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

        getContainer().deployModule(System.getProperty("vertx.modulename"),
            (AsyncResult<String> asyncResult) -> {
                assertTrue(asyncResult.succeeded());
                assertNotNull("deploymentID should not be null", 
                              asyncResult.result());
                
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ToDoRestIntTest.class.getName()).log(Level.SEVERE, null, ex);
            }                
                startTests();
        });
    }

    private HttpClient createHttpClient() {
        return getVertx().createHttpClient()
                         .setHost("localhost")
                         .setPort(8080);
    }

}
