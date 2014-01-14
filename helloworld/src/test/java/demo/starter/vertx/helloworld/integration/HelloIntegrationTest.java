package demo.starter.vertx.helloworld.integration;
/*  
 * Integration Test for HelloVerticle
 *
 * @author <a href="http://relai.blogspot.com/">Re Lai</a>
 */


import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.testtools.TestVerticle;
import static org.vertx.testtools.VertxAssert.*;

public class HelloIntegrationTest extends TestVerticle {

    @Test
    public void testHello() {

        HttpClient client = getVertx().createHttpClient()
            .setHost("localhost").setPort(8080);

        HttpClientRequest request = client.get("/",
            (HttpClientResponse resp) -> {
                assertEquals(200, resp.statusCode());

                resp.bodyHandler((Buffer data) -> {
                    getContainer().logger().info("Response: " + data.toString());
                    assertEquals("Hello World!", data.toString());
                    testComplete();
                });
            });

        request.end();
    }

    @Override
    public void start() {
        initialize();

        // Normally one should use "vertx.modulename" system property to retrieve the vertx module name.
        // However, in the case of a multi-module maven project, the property incorrectly points to the parent pom.
        // To workaround, "module.name" system property is added to pom.xml to point to the correct module.
        getContainer().deployModule(System.getProperty("module.name"),
            (AsyncResult<String> result) -> {
                assertTrue(result.succeeded());
                assertNotNull("deploymentID should not be null", result.result());
                startTests();
            });
    }

}
