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

package org.jboss.as.console.client.core;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.shared.Preferences;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class BootstrapContext implements ApplicationProperties {

    private Map<String,String> ctx = new HashMap<String,String>();

    private static final String[] persistentProperties = new String[] {
            //STANDALONE
    };
    private String initialPlace = null;

    @Inject
    public BootstrapContext() {
        /*String token = History.getToken();
        if(token!=null && !token.equals("") && !token.equals(NameTokens.signInPage))
            setProperty(INITIAL_TOKEN, token);
        */
        loadPersistedProperties();

        String devHost = org.jboss.as.console.client.Build.DEV_HOST;

        String domainApi = GWT.isScript() ? getBaseUrl()+"management" : "http://"+devHost+":8888/app/proxy";
        setProperty(DOMAIN_API, domainApi);


        String deploymentApi = GWT.isScript() ? getBaseUrl()+"management/add-content" : "http://"+devHost+":8888/app/upload";
        setProperty(DEPLOYMENT_API, deploymentApi);

        String logoutApi = GWT.isScript() ? getBaseUrl()+"logout" : "http://"+devHost+":8888/app/logout";
        setProperty(LOGOUT_API, logoutApi);


        //Log.info("Domain API Endpoint: " + domainApi);
    }

    private String getBaseUrl() {
        // extract host
        String base = GWT.getHostPageBaseURL();
        return extractHttpEndpointUrl(base);

    }

    public static String extractHttpEndpointUrl(String base) {
        String protocol = base.substring(0, base.indexOf("//")+2);
        String remainder = base.substring(base.indexOf(protocol)+protocol.length(), base.length());

        String host = null;
        String port = null;

        int portDelim = remainder.indexOf(":");
        if(portDelim !=-1 )
        {
            host = remainder.substring(0, portDelim);
            String portRemainder = remainder.substring(portDelim+1, remainder.length());
            if(portRemainder.indexOf("/")!=-1)
            {
                port = portRemainder.substring(0, portRemainder.indexOf("/"));
            }
            else
            {
                port = portRemainder;
            }
        }
        else
        {
            host = remainder.substring(0, remainder.indexOf("/"));
            if ("https://".equalsIgnoreCase(protocol)) {
                port = "443";
            } else {
                port = "80";
            }
        }

        // default url
        return protocol + host + ":" + port + "/";
    }

    private void loadPersistedProperties() {
        for(String key : persistentProperties)
        {
            String pref = Preferences.get(key);
            if(pref!=null)
                setProperty(key, pref);
        }
    }

    @Override
    public void setProperty(String key, String value)
    {
        if(isPersistent(key))
            Preferences.set(key, value);

        ctx.put(key, value);
    }

    @Override
    public String getProperty(String key)
    {
        return ctx.get(key);
    }

    @Override
    public boolean hasProperty(String key)
    {
        return getProperty(key)!=null;
    }

    public PlaceRequest getDefaultPlace() {

        PlaceRequest defaultPlace  = getProperty(STANDALONE).equals("true") ?
                new PlaceRequest(NameTokens.StandaloneServerPresenter) : new PlaceRequest(NameTokens.DomainRuntimePresenter);
        return defaultPlace;
    }

    @Override
    public void removeProperty(String key) {

        if(isPersistent(key))
            Preferences.clear(key);

        ctx.remove(key);
    }

    boolean isPersistent(String key)
    {
        boolean b = false;
        for(String s : persistentProperties)
        {
            if(s.equals(key))
            {
                b=true;
                break;
            }
        }

        return b;

    }

    @Override
    public boolean isStandalone() {
        return getProperty(BootstrapContext.STANDALONE).equals("true");
    }

    public void setInitialPlace(String nameToken) {
        this.initialPlace = nameToken;
    }

    public String getInitialPlace() {
        return initialPlace;
    }

    public String getLogoutUrl() {
        String url = getProperty(LOGOUT_API);

        if(!GWT.isScript())
            url += "?gwt.codesvr=" + Window.Location.getParameter("gwt.codesvr");
        return url;
    }
}
