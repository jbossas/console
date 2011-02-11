package org.jboss.as.console.client;

import com.google.gwt.user.client.History;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class BootstrapContext {

    public static final String INITIAL_TOKEN = "initial_token";

    private Map<String,String> ctx = new HashMap<String,String>();

    @Inject
    public BootstrapContext() {
        String token = History.getToken();
        if(token!=null && !token.equals("") && !token.equals(NameTokens.signInPage))
            setProperty(INITIAL_TOKEN, token);
    }

    public void setProperty(String key, String value)
    {
        ctx.put(key, value);
    }

    public String getProperty(String key)
    {
        return ctx.get(key);
    }

    public boolean hasProperty(String key)
    {
        return getProperty(key)!=null;
    }
}
