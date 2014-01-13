package demo.starter.vertx.todo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/* 
 * A sample asynchronous route handler for todo rest API.
 *
 * The data is stored in-memory instead of being persisted. 
 *
 * The sample payload of a todo item:
 *   {"id": "1234", "order": 1, "title": "Buy coffee", "completed": false}
 *
 * The following is a formal Avro schema of an item
 *
 *   {"name": "item",
 *    "namespace": "demo.starter.vertx.todoapi"
 *    "type": "record",
 *    "fields" : [
 *        {"name": "id",    "type": "string"},
 *        {"name": "order", "type": "int"},
 *        {"name": "title", "type": "string"},
 *        {"name": "completed", "type": "boolean"}
 *    ]
 *   }
 *      
 * @author <a href="http://relai.blogspot.com/">Re Lai</a>
 */

class SimpleRouteHandler {
    private final static String ID = "id";
    private final static String LOCATION = "Location";


    /**
     * Gets all to-do items. 
     *
     * @param      req the HTTP server request
     */
    void findAll(HttpServerRequest req) {
        JsonArray result = new JsonArray();
        for (JsonObject obj : store.values()) {
            result.addObject(obj);
        }
        req.response().end(result.encode());
    }
   
    /**
     * Gets a to-do item by ID. 
     *
     * @param      req the HTTP server request
     */
    void findById(HttpServerRequest req) {
        String id = req.params().get(ID);        
        JsonObject payload = store.get(id);
        req.response().end(payload.encode());
    }

    /**
     * Creates a to-do item. 
     * 
     * If successful, replies with status code 201. The response "location" 
     * header refers to the newly created item. The response payload is the item
     * ID.
     * 
     * @param      req the HTTP server request
     */
    void create(final HttpServerRequest req) {
        final Buffer body = new Buffer(0);

        req.dataHandler((Buffer data) -> {
            body.appendBuffer(data);
        });

        req.endHandler((Void none) -> {
            JsonObject item = new JsonObject(body.toString());
            String id = UUID.randomUUID().toString();
            item.putString(ID, id);
            store.put(id, item);
            
            JsonObject payload = new JsonObject().putString(ID, id);
            String location = req.uri()+ "/" + id;
            req.response().setStatusCode(201)
                          .putHeader(LOCATION, location)
                          .end(payload.encode());
        });
    }
    
    /**
     * Updates a to-do item by ID
     * 
     * If successful, replies with status code 204 without a response payload.
     * 
     * @param      req the HTTP server request
     */
    void update(HttpServerRequest req) {
        String id = req.params().get(ID);
        Buffer body = new Buffer(0);

        req.dataHandler((Buffer data) -> {
            body.appendBuffer(data);
        });

        req.endHandler((Void none) -> {     
            JsonObject task = new JsonObject(body.toString());
            store.put(id, task);
            req.response().setStatusCode(204)
                          .end();
        });
    }

    /**
     * Deletes a to-do item by ID
     * 
     * If successful, replies with status code 204 without a response payload.
     * 
     * @param      req the HTTP server request
     */
    void delete(HttpServerRequest req) {
        String id = req.params().get(ID);
        store.remove(id);
        req.response().setStatusCode(204)
                      .end();
    }

    private static Map<String, JsonObject> initCache() {
        Map<String, JsonObject> data = new ConcurrentHashMap<>();
        JsonObject buyMilk = new JsonObject()
            .putString(ID, "1234")
            .putNumber("order", 1)
            .putString("title", "Buy coffee")
            .putBoolean("completed", false);
        data.put(buyMilk.getString(ID), buyMilk);
        return data;
    }

    private static final Map<String, JsonObject> store = initCache();
}
