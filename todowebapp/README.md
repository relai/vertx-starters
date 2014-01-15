# Vert.x Starter III - To Do Web Application 

A sample application to track your to-do items:

* A rich-client application based on the [Backbone.js TodoMVC] (https://github.com/tastejs/todomvc/tree/gh-pages/architecture-examples/backbone) by Addy Osmani
* The client application is backed by the [to-do REST API](https://github.com/relai/vertx-starters/tree/master/todoapi).


## Web Server

The web server is based on the [Vert.x web server] (https://github.com/vert-x/mod-web-server). 

Static contents placed inside the web root directory are served. The directory is defaulted to be `web`.

Dynamic contents are served with the help of [`RouteMatcher`](https://github.com/relai/vertx-starters/blob/master/todowebapp/src/main/java/demo/starter/vertx/todo/WebServer.java):

      ToDoHandler handler = new ToDoHandler(getVertx().eventBus());
      RouteMatcher matcher = new RouteMatcher()
                .get("/todos",        handler::findAll)
                .post("/todos",       handler::create)
                .get("/todos/:id",    handler::findById)
                .put("/todos/:id",    handler::update)
                .delete("/todos/:id", handler::delete)       
                .noMatch(staticHandler());

## Dependency Management

The compile-time dependencies are declared in [pom.xml](https://github.com/relai/vertx-starters/blob/master/todowebapp/pom.xml):

    <dependency>
      <groupId>io.vertx</groupId>
      <artifactId>mod-web-server</artifactId>
      <version>${vertx.mod-web-server.version}</version>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>demo.starter.vertx</groupId>
      <artifactId>todoapi</artifactId>
      <version>${project.version}</version>
      <scope>provided</scope>
    </dependency>

They are marked as "provided", as they are only needed for compile, but not packaged into the mod.

The deployment-time dependencies are declared in mod.json:

  "includes": "demo.starter.vertx~todoapi~1.0-SNAPSHOT,io.vertx~mod-web-server~2.0.0-final"

Vert.x downloads and installs the dependent mods accordingly at the time of deployment. 

Vert.x also manages transitive dependencies. The todoapi mod itself depends on `io.vertx~mod-mongo-persistor~2.1.0`, but we do not need to explicitly declare here. Vert.x figures this out based on its `mod.json`.


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

The [integration test](https://github.com/relai/vertx-starters/blob/master/todowebapp/src/test/java/demo/starter/vertx/todo/integration/java/ToDoIntTest.java) covers the index html page loading and sanity test of the REST API.
