package demo.starter.vertx.todo;

import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.Handler;

/** 
 * The todo REST api handler
 *
 * Backed by MongoDB via the mongo persistor.
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

public class ToDoHandler {

    private final EventBus eb;
    
    final static String TODO_PERSISTOR = "todo.mongopersistor";
    
    // REST Strings
    private final static String LOCATION = "location";

    // Mongodb system strings
    private final static String COLLECTION = "collection";
    private final static String ACTION = "action";
    private final static String FIND = "find";
    private final static String FINDONE = "findone";
    private final static String SAVE = "save";
    private final static String DELETE = "delete";
    private final static String STATUS = "status";
    private final static String MATCHER = "matcher";
    private final static String DOCUMENT = "document";
    private final static String MESSAGE = "message";
    private final static String RESULT = "result";
    private final static String RESULTS = "results";
    private final static String _ID = "_id";
    private final static String ID = "id";

    // Todos db specific settings
    private final static String ITEMS = "items";

    /**
     * Constructor.
     * 
     * @param eb The vert.x event bus.
     */
    public ToDoHandler(EventBus eb) {
        this.eb = eb;
    }
    
    /**
     * Gets all to-do items. 
     *
     * @param      req the HTTP server request
     */
    public void findAll(HttpServerRequest req) {
        JsonObject command = new JsonObject()
            .putString(ACTION, FIND)
            .putString(COLLECTION, ITEMS);
        executeCommand(command, req, (Message<JsonObject> reply) -> {
            JsonArray results = reply.body().getArray(RESULTS);
            for (Object item : results) {
                if (item instanceof JsonObject) {
                    encodeId((JsonObject) item);
                }
            }
            req.response().end(results.encode());
        });       
    }

    /**
     * Gets a to-do item by ID. 
     *
     * @param      req the HTTP server request
     */
    public void findById(HttpServerRequest req) {
        String id = req.params().get(ID);
        JsonObject command = new JsonObject()
            .putString(ACTION, FINDONE)
            .putString(COLLECTION, ITEMS)
            .putObject(MATCHER, new JsonObject().putString(_ID, id));
        executeCommand(command, req, (Message<JsonObject> reply) -> {
            JsonObject result = reply.body().getObject(RESULT);
            req.response().end(encodeId(result).encode());
        });
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
    public void create(final HttpServerRequest req) {
        Buffer body = new Buffer(0);
        req.dataHandler((Buffer data) -> {
            body.appendBuffer(data);
        });        
        req.endHandler((Void none) -> {
            JsonObject command = new JsonObject()
                .putString(ACTION, SAVE)
                .putString(COLLECTION, ITEMS)
                .putObject(DOCUMENT, new JsonObject(body.toString()));
            executeCommand(command, req, (Message<JsonObject> reply) -> {
                String id = reply.body().getString(_ID);
                String location = req.uri()+ "/" + id;
                JsonObject payload = new JsonObject().putString(ID, id);
                req.response().setStatusCode(201)
                              .putHeader(LOCATION, location)
                              .end(payload.encode());
            });
        });
    }

    /**
     * Updates a to-do item by ID
     * 
     * If successful, replies with status code 204 without a response payload.
     * 
     * @param      req the HTTP server request
     */
    public void update(HttpServerRequest req) {
        String id = req.params().get(ID);
        Buffer body = new Buffer(0);
        req.dataHandler((Buffer data) -> {
            body.appendBuffer(data);
        });
        req.endHandler((Void none) -> {
            JsonObject item = new JsonObject(body.toString());
            item.removeField(ID);
            item.putString(_ID, id);
            JsonObject command = new JsonObject()
                .putString(ACTION, SAVE)
                .putString(COLLECTION, ITEMS)
                .putObject(DOCUMENT, item);
            executeCommand(command, req, 
                reply -> req.response().setStatusCode(204).end());
        });
    }

    /**
     * Deletes a to-do item by ID
     * 
     * If successful, replies with status code 204 without a response payload.
     * 
     * @param      req the HTTP server request
     */
    public void delete(HttpServerRequest req) {
        final String id = req.params().get(ID);
        JsonObject command = new JsonObject()
            .putString(ACTION, DELETE)
            .putString(COLLECTION, ITEMS)
            .putObject(MATCHER, new JsonObject().putString(_ID, id));
         executeCommand(command, req,  
             reply -> req.response().setStatusCode(204).end());
    }

    /**
     * Transforms MongoDB "_id" field into "id" field.
     * 
     * @param obj A input JSON object queried from MongoDB. The primary key is 
     *            stored in "_id" field.
     * @return A JSON object 
     */
    private JsonObject encodeId(JsonObject obj) {
        return obj.putValue(ID, obj.removeField(_ID));
    }

    /**
     * Executes a command to the event bus against the mongo persistor.
     * 
     * @param command The JSON payload to be sent over the event bus.
     * @param request HTTP server request
     * @param handler The asynchronous event handler to the event bus response.
     */
    private void executeCommand(JsonObject command, HttpServerRequest request,
        Handler<Message<JsonObject>> handler) {
        eb.send(TODO_PERSISTOR, command, (Message<JsonObject> reply) -> {
            if ("ok".equals(reply.body().getString(STATUS))) {
                handler.handle(reply);
            } else {
                request.response().setStatusCode(500)
                    .end(reply.body().getString(MESSAGE));
            }
        });
    }

}
