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


import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to access services in hosted mode that
 * deployed to an external container. I.e. JBoss AS.<p/>
 *
 * Usage (web.xml):<br>
 *
 * <pre>
 *   &lt;servlet>
 *       &lt;servlet-name>gwtProxy&lt;/servlet-name>
 *       &lt;description>GWT Proxy&lt;/description>
 *       &lt;servlet-class>org.jboss.as.console.server.proxy.XmlHttpProxyServlet&lt;/servlet-class>
 *       &lt;init-param>
 *           &lt;param-name>config.name&lt;/param-name>
 *           &lt;param-value>gwt-proxy.json&lt;/param-value>
 *       &lt;/init-param>
 *       &lt;load-on-startup>1&lt;/load-on-startup>
 *   &lt;/servlet>
 *
 *   &lt;servlet-mapping>
 *       &lt;servlet-name>gwtProxy&lt;/servlet-name>
 *       &lt;url-pattern>/app/proxy/*&lt;/url-pattern>
 *   &lt;/servlet-mapping>
 *
 * </pre>
 *
 * <p/>
 *
 * gwt-proxy.properties:<br>
 * <pre>
 *    service.id=domain-api
 *    service.url=http://127.0.0.1:9990/domain-api
 *    service.passThrough=true
 * </pre>
 *
 * @author Greg Murray
 * @author Heiko Braun
 */
public class XmlHttpProxyServlet extends HttpServlet
{

    public static String REMOTE_USER = "REMOTE_USER";

    private static String XHP_LAST_MODIFIED = "xhp_last_modified_key";
    private static String DEFAULT_CONFIG = "gwt-proxy.properties";

    private static boolean allowXDomain = false;
    private static boolean requireSession = false;
    private static boolean createSession = false;
    private static String defaultContentType = "application/dmr-encoded;charset=UTF-8";
    private static boolean rDebug = false;
    private Logger logger = null;
    private XmlHttpProxy xhp = null;
    private ServletContext ctx;
    private List<Map<String,Object>> services = null;
    private String resourcesDir = "/resources/";
    private String classpathResourcesDir = "/META-INF/resources/";
    private String headerToken = "jmaki-";
    private String testToken = "xtest-";

    private static String testUser;
    private static String testPass;

    private static String setCookie;
    private String configResource = null;
    private String authHeader;

    public XmlHttpProxyServlet() {
        if (rDebug) {
            logger = getLogger();
        }

    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ctx = config.getServletContext();
        // set the response content type
        if (ctx.getInitParameter("responseContentType") != null) {
            defaultContentType = ctx.getInitParameter("responseContentType");
        }
        // allow for resources dir over-ride at the xhp level otherwise allow
        // for the jmaki level resources
        if (ctx.getInitParameter("jmaki-xhp-resources") != null) {
            resourcesDir = ctx.getInitParameter("jmaki-xhp-resources");
        } else if (ctx.getInitParameter("jmaki-resources") != null) {
            resourcesDir = ctx.getInitParameter("jmaki-resources");
        }
        // allow for resources dir over-ride
        if (ctx.getInitParameter("jmaki-classpath-resources") != null) {
            classpathResourcesDir = ctx.getInitParameter("jmaki-classpath-resources");
        }
        String requireSessionString = ctx.getInitParameter("requireSession");
        if (requireSessionString == null) requireSessionString = ctx.getInitParameter("jmaki-requireSession");
        if (requireSessionString != null) {
            if ("false".equals(requireSessionString)) {
                requireSession = false;
                getLogger().severe("XmlHttpProxyServlet: intialization. Session requirement disabled.");
            } else if ("true".equals(requireSessionString)) {
                requireSession = true;
                getLogger().severe("XmlHttpProxyServlet: intialization. Session requirement enabled.");
            }
        }
        String xdomainString = ctx.getInitParameter("allowXDomain");
        if (xdomainString == null) xdomainString = ctx.getInitParameter("jmaki-allowXDomain");
        if (xdomainString != null) {
            if ("true".equals(xdomainString)) {
                allowXDomain = true;
                getLogger().severe("XmlHttpProxyServlet: intialization. xDomain access is enabled.");
            } else if ("false".equals(xdomainString)) {
                allowXDomain = false;
                getLogger().severe("XmlHttpProxyServlet: intialization. xDomain access is disabled.");
            }
        }
        String createSessionString = ctx.getInitParameter("jmaki-createSession");
        if (createSessionString != null) {
            if ("true".equals(createSessionString)) {
                createSession = true;
                getLogger().severe("XmlHttpProxyServlet: intialization. create session is enabled.");
            } else if ("false".equals(xdomainString)) {
                createSession = false;
                getLogger().severe("XmlHttpProxyServlet: intialization. create session is disabled.");
            }
        }
        // if there is a proxyHost and proxyPort specified create an HttpClient with the proxy
        String proxyHost = ctx.getInitParameter("proxyHost");
        String proxyPortString = ctx.getInitParameter("proxyPort");
        if (proxyHost != null && proxyPortString != null) {
            int proxyPort = 8080;
            try {
                proxyPort= new Integer(proxyPortString).intValue();
                xhp = new XmlHttpProxy(proxyHost, proxyPort);
            } catch (NumberFormatException nfe) {
                getLogger().severe("XmlHttpProxyServlet: intialization error. The proxyPort must be a number");
                throw new ServletException("XmlHttpProxyServlet: intialization error. The proxyPort must be a number");
            }
        } else {
            xhp = new XmlHttpProxy();
        }

        // config override
        String servletName = config.getServletName();
        String configName = config.getInitParameter("config.name");
        configResource = configName!=null ? configName : DEFAULT_CONFIG;
        //System.out.println("Configure "+servletName + " through "+configResource);
    }

    private void getServices(HttpServletResponse res)
    {
        services = new ArrayList<Map<String,Object>>();

        InputStream is = XmlHttpProxyServlet.class.getClassLoader()
                .getResourceAsStream(configResource);

        if(null==is)
            throw new IllegalStateException("Failed to load proxy configuration: "+configResource);

        try {
            PropertyResourceBundle bundle = new PropertyResourceBundle(is);
            HashMap<String, Object> serviceConfig = new HashMap<String, Object>();
            serviceConfig.put(ProxyConfig.ID, "default");
            serviceConfig.put(ProxyConfig.URL, bundle.getString("service.url"));
            serviceConfig.put(ProxyConfig.PASSTHROUGH, Boolean.valueOf(bundle.getString("service.passthrough")));
            services.add(serviceConfig);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load proxy configuration");

        }

    }

    public void doDelete(HttpServletRequest req, HttpServletResponse res) {
        doProcess(req,res, XmlHttpProxy.DELETE);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        doProcess(req,res, XmlHttpProxy.GET);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) {
        doProcess(req,res, XmlHttpProxy.POST);
    }

    public void doPut(HttpServletRequest req, HttpServletResponse res) {
        doProcess(req,res, XmlHttpProxy.PUT);
    }

    public void doProcess(HttpServletRequest req, HttpServletResponse res, String method)
    {

        boolean isPost = XmlHttpProxy.POST.equals(method);


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bodyContent = null;

        OutputStream out = null;
        PrintWriter writer = null;

        try {

            BufferedInputStream in = new BufferedInputStream(req.getInputStream());
            int next = in.read();
            while (next > -1) {
                bos.write(next);
                next = in.read();
            }

            bodyContent = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try
        {
            HttpSession session = null;
            // it really does not make sense to use create session with require session as
            // the create session will always result in a session created and the requireSession
            // will always succeed. Leaving the logic for now.
            if (createSession) {
                session = req.getSession(true);
            }
            if (requireSession) {
                // check to see if there was a session created for this request
                // if not assume it was from another domain and blow up
                // Wrap this to prevent Portlet exeptions
                session = req.getSession(false);
                if (session == null) {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }
            // check if the services have been loaded or if they need to be reloaded
            if (services == null || configUpdated()) {
                getServices(res);
            }
            String urlString = null;
            String xslURLString = null;
            String userName = null;
            String password = null;
            String format = "json";
            String callback = req.getParameter("callback");
            String urlParams = req.getParameter("urlparams");
            String countString = req.getParameter("count");
            boolean passthrough = false;
            // encode the url to prevent spaces from being passed along
            if (urlParams != null) {
                urlParams = urlParams.replace(' ', '+');
            }
            // get the headers to pass through
            Map headers = null;
            // Forward all request headers starting with the header token jmaki-
            // and chop off the jmaki-
            Enumeration hnum = req.getHeaderNames();

            // test hack

            while (hnum.hasMoreElements()) {
                String name = (String)hnum.nextElement();
                if (name.startsWith(headerToken))
                {
                    if (headers == null) headers = new HashMap();

                    String value = "";
                    // handle multi-value headers
                    Enumeration vnum = req.getHeaders(name);
                    while (vnum.hasMoreElements()) {
                        value += (String)vnum.nextElement();
                        if (vnum.hasMoreElements()) value += ";";
                    }
                    String sname = name.substring(headerToken.length(), name.length());
                    headers.put(sname,value);
                }
                else if (name.equals("Accept"))
                {
                    if (headers == null) headers = new HashMap();

                    String value = "";
                    // handle multi-value headers
                    Enumeration vnum = req.getHeaders(name);
                    while (vnum.hasMoreElements()) {
                        value += (String)vnum.nextElement();
                        if (vnum.hasMoreElements()) value += ";";
                    }
                    headers.put(name,value);
                }
                else if(name.startsWith(testToken))
                {
                    // hack test capabilities for authentication
                    if("xtest-user".equals(name)) testUser = req.getHeader("xtest-user");
                    if("xtest-pass".equals(name)) testPass = req.getHeader("xtest-pass");
                }

                // auth header
                else if(name.equals("Authorization"))
                {
                    // hack test capabilities for authentication
                    authHeader = req.getHeader("Authorization");
                }
            }

            try
            {
                String actualServiceKey = "default";
                Map<String,Object> service = null;
                for(Map svc : services)
                {
                    if(svc.get(ProxyConfig.ID).equals(actualServiceKey))
                    {
                        service = svc;
                        break;
                    }
                }
                if (service!=null)
                {

                    String serviceURL = (String)service.get(ProxyConfig.URL);
                    if(null==serviceURL)
                        throw new IllegalArgumentException(configResource+": service url is mising");

                    if (service.containsKey(ProxyConfig.PASSTHROUGH))
                        passthrough = (Boolean)service.get(ProxyConfig.PASSTHROUGH);


                    if(null==testUser)
                    {
                        //System.out.println("Ignore service configuration credentials");
                        if (service.containsKey("username")) userName = (String)service.get("username");
                        if (service.containsKey("password")) password = (String)service.get("password");
                    }
                    else
                    {
                        userName = testUser;
                        password = testPass;
                    }

                    String apikey = "";
                    if (service.containsKey("apikey")) apikey = (String)service.get("apikey");
                    if (service.containsKey("xslStyleSheet")) xslURLString = (String)service.get("xslStyleSheet");

                    // default to the service default if no url parameters are specified
                    if(!passthrough)
                    {
                        if (urlParams == null && service.containsKey("defaultURLParams")) {
                            urlParams = (String)service.get("defaultURLParams");
                        }

                        // build the URL
                        if (urlParams != null && serviceURL.indexOf("?") == -1){
                            serviceURL += "?";
                        } else  if (urlParams != null) {
                            serviceURL += "&";
                        }

                        urlString = serviceURL + apikey;
                        if (urlParams != null) urlString += "&" + urlParams;
                    }

                    if(passthrough)
                    {
                        StringBuffer sb = new StringBuffer();
                        sb.append(serviceURL);

                        // override service url and url params
                        String path = req.getRequestURI();
                        String servletPath = req.getServletPath();
                        path = path.substring(path.indexOf(servletPath)+servletPath.length(), path.length());

                        StringTokenizer tok = new StringTokenizer(path, "/");
                        while(tok.hasMoreTokens())
                        {
                            String token = tok.nextToken();
                            if(token.indexOf(";")!=-1)
                                sb.append("/").append(token);    // ;JSESSIONID=XYZ
                            else
                                sb.append("/").append(URLEncoder.encode(token));
                        }

                        if(req.getQueryString()!=null)
                            sb.append("?").append(req.getQueryString());

                        urlString = sb.toString();
                    }
                }
                else
                {
                    writer = res.getWriter();
                    writer.write("XmlHttpProxyServlet Error : service for id '" + actualServiceKey + "' not  found.");
                    writer.flush();
                    return;
                }
            }
            catch (Exception ex)
            {
                getLogger().severe("XmlHttpProxyServlet Error loading service: " + ex);
                res.setStatus(500);
            }

            Map paramsMap = new HashMap();
            paramsMap.put("format", format);
            // do not allow for xdomain unless the context level setting is enabled.
            if (callback != null && allowXDomain) {
                paramsMap.put("callback", callback);
            }
            if (countString != null) {
                paramsMap.put("count", countString);
            }

            InputStream xslInputStream = null;

            if (urlString == null) {
                writer = res.getWriter();
                writer.write("XmlHttpProxyServlet parameters:  id[Required] urlparams[Optional] format[Optional] callback[Optional]");
                writer.flush();
                return;
            }
            // support for session properties and also authentication name
            if (urlString.indexOf("${") != -1) {
                urlString = processURL(urlString, req, res);
            }

            out = res.getOutputStream();

            if (!isPost)
            {
                xhp.processRequest(urlString, out, xslInputStream, paramsMap, headers, method, userName, password);
            }
            else
            {
                if (bodyContent == null)
                    getLogger().info("XmlHttpProxyServlet attempting to post to url " + urlString + " with no body content");
                xhp.doPost(urlString, out, xslInputStream, paramsMap, headers, bodyContent, req.getContentType(), userName, password, authHeader);
            }

            res.setContentType(xhp.getContentType());
            res.setStatus(xhp.getStatus());

        }
        catch (Exception iox)
        {
            iox.printStackTrace();
            getLogger().severe("XmlHttpProxyServlet: caught " + iox);


            if(iox instanceof XmlHttpProxy.AuthenticationException)
            {
                XmlHttpProxy.AuthenticationException authEx = (XmlHttpProxy.AuthenticationException)iox;
                res.setHeader("WWW-Authenticate", authEx.getAuthHeader());
                res.setStatus(authEx.getCode());
            }
            else
            {
                res.setStatus(500);
            }
            /*try {
               writer = res.getWriter();
               writer.write("XmlHttpProxyServlet error loading service for " + serviceKey + " . Please notify the administrator.");
               writer.flush();
            } catch (java.io.IOException ix) {
               ix.printStackTrace();
            }*/
            return;
        }
        finally
        {
            try
            {
                if (out != null) out.close();
                if (writer != null) writer.close();
            } catch (java.io.IOException iox){}
        }
    }

    /* Allow for a EL style replacements in the serviceURL
     *
     * The constant REMOTE_USER will replace the contents of ${REMOTE_USER}
     * with the return value of request.getRemoteUserver() if it is not null
     * otherwise the ${REMOTE_USER} is replaced with a blank.
     *
     * If you use ${session.somekey} the ${session.somekey} will be replaced with
     * the String value of the session varialble somekey or blank if the session key
     * does not exist.
     *
    */
    private String processURL(String url, HttpServletRequest req, HttpServletResponse res) {
        String serviceURL = url;
        int start = url.indexOf("${");
        int end = url.indexOf("}", start);
        if (end != -1) {
            String prop = url.substring(start + 2, end).trim();
            // no matter what we will remove the ${}
            // default to blank like the JSP EL
            String replace = "";
            if (REMOTE_USER.equals(prop)) {
                if (req.getRemoteUser() != null) replace = req.getRemoteUser();
            }
            if (prop.toLowerCase().startsWith("session.")) {
                String sessionKey = prop.substring("session.".length(), prop.length());
                if (req.getSession().getAttribute(sessionKey) != null) {
                    // force to a string
                    replace = req.getSession().getAttribute(sessionKey).toString();
                }
            }
            serviceURL = serviceURL.substring(0, start) +
                    replace +
                    serviceURL.substring(end + 1, serviceURL.length());
        }
        // call recursively to process more than one instance of a ${ in the serviceURL
        if (serviceURL.indexOf("${") != -1) serviceURL = processURL(serviceURL, req, res);
        return serviceURL;
    }

    /**
     * Check to see if the configuration file has been updated so that it may be reloaded.
     */
    private boolean configUpdated() {
        try {
            URL url = ctx.getResource(resourcesDir + configResource);
            URLConnection con;
            if (url == null) return false ;
            con = url.openConnection();
            long lastModified = con.getLastModified();
            long XHP_LAST_MODIFIEDModified = 0;
            if (ctx.getAttribute(XHP_LAST_MODIFIED) != null) {
                XHP_LAST_MODIFIEDModified = ((Long)ctx.getAttribute(XHP_LAST_MODIFIED)).longValue();
            } else {
                ctx.setAttribute(XHP_LAST_MODIFIED, new Long(lastModified));
                return false;
            }
            if (XHP_LAST_MODIFIEDModified < lastModified) {
                ctx.setAttribute(XHP_LAST_MODIFIED, new Long(lastModified));
                return true;
            }
        } catch (Exception ex) {
            getLogger().severe("XmlHttpProxyServlet error checking configuration: " + ex);
        }
        return false;
    }

    public Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger("jmaki.services.xhp.Log");

            // TODO: the logger breaks the GWT tests, because it writes to stderr
            // we'll turn it off for now.
            //System.out.println("WARN: XHP proxy logging is turned off");
            logger.setLevel(Level.OFF);
        }
        return logger;
    }

    private void logMessage(String message) {
        if (rDebug) {
            getLogger().info(message);
        }
    }
}