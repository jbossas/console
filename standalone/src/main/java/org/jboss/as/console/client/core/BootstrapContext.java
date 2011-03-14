package org.jboss.as.console.client.core;

import com.google.gwt.user.client.History;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.shared.Preferences;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
    public class BootstrapContext {

    public static final String INITIAL_TOKEN = "initial_token";
    public static final String STANDALONE = "standalone_usage";

    private Map<String,String> ctx = new HashMap<String,String>();

    private static final String[] persistentProperties = new String[] {
            STANDALONE
    };

    @Inject
    public BootstrapContext() {
        String token = History.getToken();
        if(token!=null && !token.equals("") && !token.equals(NameTokens.signInPage))
            setProperty(INITIAL_TOKEN, token);

        loadPersistedProperties();
    }

    private void loadPersistedProperties() {
        for(String key : persistentProperties)
        {
            String pref = Preferences.get(key);
            if(pref!=null)
                setProperty(key, pref);
        }
    }

    public void setProperty(String key, String value)
    {
        if(isPersistent(key))
            Preferences.set(key, value);

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

    public PlaceRequest getDefaultPlace() {
       PlaceRequest defaultPlace  = hasProperty(STANDALONE) ?
               new PlaceRequest(NameTokens.serverConfig) : new PlaceRequest(NameTokens.ProfileMgmtPresenter);
        return defaultPlace;
    }

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
}
