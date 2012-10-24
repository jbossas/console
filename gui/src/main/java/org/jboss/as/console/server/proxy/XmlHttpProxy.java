/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.server.proxy;


import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Greg Murray
 * @author Heiko Braun
 */
public class XmlHttpProxy {

    public static String GET = "GET";
    public static String POST = "POST";
    public static String DELETE = "DELETE";
    public static String PUT = "PUT";

    private String userName = null;
    private String password = null;
    private static Logger logger;
    private String proxyHost = "";
    int proxyPort = -1;
    private Object config;
    private static String USAGE = "Usage:  -url service_URL  -id service_key [-url or -id required] -xslurl xsl_url [optional] -format json|xml [optional] -callback[optional] -config [optional] -resources base_directory_containing XSL stylesheets [optional]";
    private String authHeader;

    public XmlHttpProxy() {}

    private String contentType = "application/json";

    private Map<String, Cookie> cookies = new HashMap<String, Cookie>();

    int status = -1;

    public String getContentType() {
        return contentType;
    }

    public int getStatus() {
        return status;
    }

    public interface CookieCallback
    {
        Map<String, Cookie> getCookies();
    }

    public XmlHttpProxy(String proxyHost, int proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    public XmlHttpProxy(String proxyHost, int proxyPort,
                        String userName, String password) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.userName = userName;
        this.password = password;
    }

    /**
     * This method will go out and make the call and it will apply an XSLT Transformation with the
     * set of parameters provided.
     *
     * @param urlString - The URL which you are looking up
     * @param out - The OutputStream to which the resulting document is written
     * @param xslInputStream - An input Stream to an XSL style sheet that is provided to the XSLT processor. If set to null there will be no transformation
     * @param  paramsMap - A Map of parameters that are feed to the XSLT Processor. These params may be used when generating content. This may be set to null if no parameters are necessary.
     * @param method - The HTTP method used.
     *
     */
    public void processRequest(String urlString,
                               OutputStream out,
                               InputStream xslInputStream,
                               Map paramsMap,
                               Map headers,
                               String method,
                               String userName,
                               String password) throws IOException, MalformedURLException {
        doProcess(urlString, out, xslInputStream, paramsMap, headers,method, null,null, userName,password);
    }
    /**
     * This method will go out and make the call and it will apply an XSLT Transformation with the
     * set of parameters provided.
     *
     * @param urlString - The URL which you are looking up
     * @param out - The OutputStream to which the resulting document is written
     * @param authHeader
     *
     */
    public void doPost(String urlString,
                       OutputStream out,
                       InputStream xslInputStream,
                       Map paramsMap,
                       Map headers,
                       byte[] postData,
                       String postContentType,
                       String userName,
                       String password, String authHeader) throws IOException, MalformedURLException {
        this.authHeader = authHeader;
        doProcess(urlString, out, xslInputStream, paramsMap, headers, XmlHttpProxy.POST, postData, postContentType, userName, password);
    }

    /**
     * This method will go out and make the call and it will apply an XSLT Transformation with the
     * set of parameters provided.
     *
     * @param urlString - The URL which you are looking up
     * @param out - The OutputStream to which the resulting document is written
     * @param xslInputStream - An input Stream to an XSL style sheet that is provided to the XSLT processor. If set to null there will be no transformation
     * @param paramsMap - A Map of parameters that are feed to the XSLT Processor. These params may be used when generating content. This may be set to null if no parameters are necessary.
     * @param method - the HTTP method used.
     * @param postData - A String of the bodyContent to be posted. A doPost will be used if this is parameter is not null.
     * @param postContentType - The request contentType used when posting data. Will not be set if this parameter is null.
     * @param userName - userName used for basic authorization
     * @param password - password used for basic authorization
     */
    public void doProcess(String urlString,
                          OutputStream out,
                          InputStream xslInputStream,
                          Map paramsMap,
                          Map headers,
                          String method,
                          byte[] postData,
                          String postContentType,
                          String userName,
                          String password) throws IOException, MalformedURLException {

        if (paramsMap == null) {
            paramsMap = new HashMap();
        }

        String format = (String)paramsMap.get("format");
        if (format == null) {
            format = "xml";
        }

        InputStream in = null;
        BufferedOutputStream os = null;

        HttpClient httpclient = null;

        CookieCallback callback = new CookieCallback()
        {

            public Map<String, Cookie> getCookies()
            {
                return accessCookies();
            }
        };

        if (userName != null && password != null)
        {
            httpclient = new HttpClient(proxyHost, proxyPort, urlString, headers, method, userName, password, callback);
        }
        else
        {
            httpclient = new HttpClient(proxyHost, proxyPort, urlString, headers, method, callback);
        }

        // post data determines whether we are going to do a get or a post
        if (postData == null) {
            in = httpclient.getInputStream();
        } else {
            in = httpclient.doPost(postData, postContentType, authHeader);
        }

        // Set-Cookie header
        if(httpclient.getSetCookieHeader()!=null)
        {
            String cookie = httpclient.getSetCookieHeader();
            System.out.println("'Set-Cookie' header: "+ cookie);
            String[] values = cookie.split(";");

            Cookie c = new Cookie();
            for(String v : values)
            {
                String[] tuple = v.split("=");
                if("Path".equals( tuple[0].trim()))
                    c.path = tuple[1];
                else
                {
                    c.name = tuple[0].trim();
                    c.value = tuple[1];
                }
            }


            List<String> toBeRemoved = new ArrayList<String>();
            Iterator it = cookies.keySet().iterator();
            while(it.hasNext())
            {
                Cookie exists = cookies.get(it.next());
                if(exists.name.equals(c.name))
                {
                    String msg = exists.value.equals(c.value) ?
                            "Replace with same value: "+exists.value :
                            "Replace with different value: "+exists.value +"->"+c.value;

                    System.out.println("Cookie '"+exists.name+"' exists: " + msg);
                    // avoid doubles
                    toBeRemoved.add(exists.name);
                }
            }

            // clean up
            for(String s : toBeRemoved)
            {
                cookies.remove(s);
            }

            cookies.put(c.name, c);
        }

        int responseCode = httpclient.getResponseCode();
        if(responseCode != 200)
        {
            if(401== responseCode || 403==responseCode)
            {
                // authentication required
                throw new AuthenticationException(responseCode, httpclient.getHeader("WWW-Authenticate"));
            }
            else if(307==responseCode)
            {
                // redirect
                throw new RedirectException(httpclient.getHeader("Location"));
            }
            else if(503==responseCode)
            {
                // service unavailable
                throw new ServiceUnavailableException(httpclient.getHeader("Retry-After"));
            }
            else
            {
                throw new IOException("Failed to open input stream, status: "+responseCode);
            }
        }

        // read the encoding from the incoming document and default to UTF-8
        // if an encoding is not provided
        String ce = httpclient.getContentEncoding();
        if (ce == null) {
            String ct = httpclient.getContentType();
            if (ct != null) {
                int idx = ct.lastIndexOf("charset=");
                if (idx >= 0) {
                    ce = ct.substring(idx+8);
                } else {
                    ce = "UTF-8";
                }
            } else {
                ce = "UTF-8";
            }
        }
        // get the content type
        this.contentType = httpclient.getContentType();
        this.status = httpclient.getResponseCode();

        // write out the content type
        //http://www.ietf.org/rfc/rfc4627.txt

        try {

            // response stream

            byte[] buffer = new byte[1024];
            int read = 0;
            if (xslInputStream == null) {
                while (true) {
                    read = in.read(buffer);
                    if (read <= 0) break;
                    out.write(buffer, 0, read );
                }
            } else {
                transform(in, xslInputStream, paramsMap, out, ce);
            }
        } catch (Exception e) {
            getLogger().severe("XmlHttpProxy transformation error: " + e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    private Map<String,Cookie> accessCookies()
    {
        return cookies;
    }

    /**
     * Do the XSLT transformation
     */
    public void transform( InputStream xmlIS,
                           InputStream xslIS,
                           Map params,
                           OutputStream result,
                           String encoding) {
        try {
            TransformerFactory trFac = TransformerFactory.newInstance();
            Transformer transformer = trFac.newTransformer(new StreamSource(xslIS));
            Iterator it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = (String)it.next();
                transformer.setParameter(key, (String)params.get(key));
            }
            transformer.setOutputProperty("encoding", encoding);
            transformer.transform(new StreamSource(xmlIS), new StreamResult(result));
        } catch (Exception e) {
            getLogger().severe("XmlHttpProxy: Exception with xslt " + e);
        }
    }

    /**
     *
     * CLI to the XmlHttpProxy
     */
    /* public static void main(String[] args)
       throws IOException, MalformedURLException {

     getLogger().info("XmlHttpProxy 1.8");
     XmlHttpProxy xhp = new XmlHttpProxy();

     if (args.length == 0) {
       System.out.println(USAGE);
     }

     String method = XmlHttpProxy.GET;
     InputStream xslInputStream = null;
     String serviceKey = null;
     String urlString = null;
     String xslURLString = null;
     String format = "xml";
     String callback = null;
     String urlParams = null;
     String configURLString = "xhp.json";
     String resourceBase = "file:src/conf/META-INF/resources/xsl/";
     String username = null;
     String password = null;

     // read in the arguments
     int index = 0;
     while (index < args.length) {
       if (args[index].toLowerCase().equals("-url") && index + 1 < args.length) {
         urlString = args[++index];
       } else if (args[index].toLowerCase().equals("-key") && index + 1 < args.length) {
         serviceKey = args[++index];
       } else if (args[index].toLowerCase().equals("-id") && index + 1 < args.length) {
         serviceKey = args[++index];
       } else if (args[index].toLowerCase().equals("-callback") && index + 1 < args.length) {
         callback = args[++index];
       }  else if (args[index].toLowerCase().equals("-xslurl") && index + 1 < args.length) {
         xslURLString = args[++index];
       } else if (args[index].toLowerCase().equals("-method") && index + 1 < args.length) {
         method = args[++index];
       } else if (args[index].toLowerCase().equals("-username") && index + 1 < args.length) {
         username = args[++index];
       } else if (args[index].toLowerCase().equals("-password") && index + 1 < args.length) {
         password = args[++index];
       } else if (args[index].toLowerCase().equals("-urlparams") && index + 1 < args.length) {
         urlParams = args[++index];
       } else if (args[index].toLowerCase().equals("-config") && index + 1 < args.length) {
         configURLString = args[++index];
       } else if (args[index].toLowerCase().equals("-resources") && index + 1 < args.length) {
         resourceBase = args[++index];
       }
       index++;
     }

     if (serviceKey != null) {
      try {
        InputStream is = (new URL(configURLString)).openStream();
        JSONObject services = loadServices(is);
        JSONObject service = services.getJSONObject(serviceKey);
        // default to the service default if no url parameters are specified
        if (urlParams == null && service.has("defaultURLParams")) {
          urlParams = service.getString("defaultURLParams");
        }
        String serviceURL = service.getString("url");
        // build the URL properly
        if (urlParams != null && serviceURL.indexOf("?") == -1){
          serviceURL += "?";
        } else if (urlParams != null){
          serviceURL += "&";
        }
        String apiKey = "";
        if (service.has("apikey")) apiKey = service.getString("apikey");
        urlString = serviceURL + apiKey +  "&" + urlParams;
        if (service.has("xslStyleSheet")) {
          xslURLString = service.getString("xslStyleSheet");
          // check if the url is correct of if to load from the classpath

        }
      } catch (Exception ex) {
        getLogger().severe("XmlHttpProxy Error loading service: " + ex);
        System.exit(1);
      }
    } else if (urlString == null) {
      System.out.println(USAGE);
      System.exit(1);
    }
     // The parameters are feed to the XSL Stylsheet during transformation.
     // These parameters can provided data or conditional information.
     Map paramsMap = new HashMap();
     if (format != null) {
       paramsMap.put("format", format);
     }
     if (callback != null) {
       paramsMap.put("callback", callback);
     }

     if (xslURLString != null) {
       URL xslURL = new URL(xslURLString);
       if (xslURL != null) {
         xslInputStream  = xslURL.openStream();
       } else {
         getLogger().severe("Error: Unable to locate XSL at URL " + xslURLString);
       }
     }
     xhp.processRequest(urlString, System.out, xslInputStream, paramsMap, null, method, username, password);
   } */

    public static Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(XmlHttpProxy.class.getName());
        }
        return logger;
    }

    public static ProxyConfig loadServices(InputStream is)
    {
        return ProxyConfig.parse(is);
    }

    public class Cookie
    {
        String name;
        String value;
        String path;
    }


    public class AuthenticationException extends IOException {
        int code;
        String authHeader;

        public AuthenticationException(int code, String authHeader) {
            this.code = code;
            this.authHeader = authHeader;
        }

        public int getCode() {
            return code;
        }

        public String getAuthHeader() {
            return authHeader;
        }
    }
}