package org.jboss.as.console.client;

import org.jboss.as.console.client.core.BootstrapContext;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * @author Heiko Braun
 * @date 7/25/11
 */
public class BootstrapTest {

    static String[] defaultUrls = new String[] {
           "http://localhost:9990/console",
           "http://localhost:9990/console/index.html",
           "http://localhost:9990/",
           "http://localhost:9990"
    };

    static String portlessUrl = "http://localhost/console";

    @Test
    public void parseDefaultModuleUrls() {

      for(String url : defaultUrls)
      {
          String httpEndpoint = BootstrapContext.extractHttpEndpointUrl(url);
          assertEquals("http://localhost:9990/", httpEndpoint);
      }
    }

     @Test
    public void parsePortlessModuleUrl() {

         String httpEndpoint = BootstrapContext.extractHttpEndpointUrl(portlessUrl);
         assertEquals("http://localhost:80/", httpEndpoint);
     }
}
