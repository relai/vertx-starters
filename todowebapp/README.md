# Vert.x Starter - To Do Web Application 

A sample application to track your to-do items:

* A rich-client application based on the [Backbone.js TodoMVC] (https://github.com/tastejs/todomvc/tree/gh-pages/architecture-examples/backbone) by Addy Osmani
* The client application is backed by todos REST API
* The REST API is, in turn, backed by MongoDB 

## Web Server

The web server is based on the [Vert.x web server] (https://github.com/vert-x/mod-web-server).

Static contents placed inside the web root directory are served. The directory is defaulted to be `web`.

Dynamic contents, the to-do REST service, are served with the help of `RouteMatcher`:

      ToDoHandler handler = new ToDoHandler(getVertx().eventBus());
      RouteMatcher matcher = new RouteMatcher()
                .get("/todos",        handler::findAll)
                .post("/todos",       handler::create)
                .get("/todos/:id",    handler::findById)
                .put("/todos/:id",    handler::update)
                .delete("/todos/:id", handler::delete);        
      matcher.noMatch(staticHandler());

## To DO REST Service Backed by MongoDB

The todo rest service in the this sample is improved to be backed by MongoDB. It is achieved by ToDoHandler.

ToDoHandler communicates with the MongoDB using io.vertx~mod-mongo-persistor. 

MongoDB uses "_id" to store a record ID, which is a bit idiosyncratic and should be hidden from our clients. ToDoHandler translates "_id" to "id".

From the client-side, the REST service behaves the same as "todoapi". 


## Rich Client Application

The rich client application uses the [Backbone.js TodoMVC] (https://github.com/tastejs/todomvc/tree/gh-pages/architecture-examples/backbone) by Addy Osmani, part of TodoMVC sample applications.

The rich client application is almost taken as is. The only code change is to switch the data source from local storage to REST:

      // Backed by todos REST api instead of local storage
      //localStorage: new Backbone.LocalStorage('todos-backbone'),             
      url: "/todos",

The content is packaged into the "web" folder inside the mod via the maven resources plugin.

          <!-- Copy web content into the mod web folder -->
          <execution>
             <id>copy-mod-webcontent</id>
             <phase>process-classes</phase>
             <goals>
               <goal>copy-resources</goal>
             </goals>
             <configuration>
               <overwrite>true</overwrite>
               <outputDirectory>${mods.directory}/${module.name}/web</outputDirectory>
               <resources>
                 <resource>                  
                   <directory>src/main/webapp</directory>
                 </resource>
               </resources>
             </configuration>
           </execution>

## Integration Test

The integration test includes loading the index html page and sanity test of the REST API.
