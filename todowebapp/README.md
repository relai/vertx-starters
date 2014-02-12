####[Vertx Starter Series](https://github.com/relai/vertx-starters)
# Part III - To Do Web Application 

In the last part of the tutorial, we build a sample application to track your to-do items:

* A rich-client application based on the [Backbone.js TodoMVC] (https://github.com/tastejs/todomvc/tree/gh-pages/architecture-examples/backbone) by Addy Osmani
* The client application is backed by the [to-do REST API](https://github.com/relai/vertx-starters/tree/master/todoapi).

## Project Structure

**The main source code**:

- [`App.java`](src/main/java/demo/starter/vertx/todo/App.java): the main Vert.x verticle of this module, deploying the web server and the mongo persistor.
- [`WebServer.java`](src/main/java/demo/starter/vertx/todo/WebServer.java): the web server to serve both the static web content and the to-do RESTful service.
- [`webapp`](src/main/webapp): the folder for static web content, which is packaged into the `web` folder in the mod.
- [`mod.json`](src/main/resources/mod.json): the mod descriptor.
- [`ToDoIntTest.java`](src/test/java/demo/starter/vertx/todo/integration/java/ToDoIntTest.java): integration test of the web application, for both the `index.html` page and the REST API.

## Dependency Management

The module has compile-time dependency to the following two mods:

* io.vertx~mod-web-server
* demo.starter.vertx~todoapi

The dependency is declared [`mod.json`](src/main/resources/mod.json), and marked as "provided" in [pom.xml](pom.xml) as provided. 

The module also depends on the mongo persistor at the deployment time, which is also declared in [`mod.json`](src/main/resources/mod.json).

## Web Server

The web server is based on the [Vert.x web server] (https://github.com/vert-x/mod-web-server). 

The web server servers all static contents placed inside the web root directory, which is defaulted to be `web`. Dynamic contents are served with the help of [`RouteMatcher`](https://github.com/relai/vertx-starters/blob/master/todowebapp/src/main/java/demo/starter/vertx/todo/WebServer.java):

      ToDoHandler handler = new ToDoHandler(getVertx().eventBus());
      RouteMatcher matcher = new RouteMatcher()
                .get("/todos",        handler::findAll)
                .post("/todos",       handler::create)
                .get("/todos/:id",    handler::findById)
                .put("/todos/:id",    handler::update)
                .delete("/todos/:id", handler::delete)       
                .noMatch(staticHandler());

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
