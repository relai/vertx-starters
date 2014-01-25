#Vert.x for Starters

This is a series of sample projects that build event-driven web applications using Vert.x. 

* [Hello World](https://github.com/relai/vertx-starters/tree/master/helloworld): a simple HTTP server
* [To-do REST API](https://github.com/relai/vertx-starters/tree/master/todoapi): a to-do REST service backed up MongoDB
* [To-do Web Application](https://github.com/relai/vertx-starters/tree/master/todowebapp): a to-do list management rich-client application, backed by the previous to-do REST service

The tutorial also shows how to develop Vert.x applications by multi-modules. 

## Java 8

All samples are written in Java 8 to take advantage of the [lambda](http://relai.blogspot.com/2013/11/java-lambda-cheat-sheet.html) feature. A more in-depth discussion regarding Vert.x and lambda programming is made in the ["Hello World"](https://github.com/relai/vertx-starters/tree/master/helloworld) module.

To run the examples, make sure to install JDK 8, which is still a preview version for now. Most popular IDE's already have early support for Java 8 and Lambda. I have personally tried Eclipse and NetBeans. Bboth are decent, and NetBeans is more stable for now.


## Maven Integration

The series is developed as a multi-module maven project based on [vertx maven integration](http://vertx.io/maven_dev.html). 

The project is structured as three modules:

       <modules>
            <module>helloworld</module>
	    <module>todoapi</module>
	    <module>todowebapp</module>
       </modules>  

Shared elements of sub-module `pom's` are extracted into the parent [`pom`](https://github.com/relai/vertx-starters/blob/master/pom.xml) as much as possible in order to avoid duplication and inconsistency:

* properties for dependencies and versions,
* common dependencies,
* plugins for compiling, resource and packaging.


The following are common maven commands:

*  `mvn clean`      To clean up
*  `mvn verify`     To execute integration tests
*  `mvn install`    To compile, test, package and install

