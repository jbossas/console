package org.jboss.mbui.gui.behaviour.as7;

import org.jboss.as.console.client.auth.CurrentUser;
import org.jboss.as.console.client.domain.profiles.CurrentProfileSelection;
import org.jboss.mbui.gui.behaviour.StatementContext;

import javax.inject.Inject;

/**
 * A default context for statements that reside with the core framework.<br/>
 * Historically this have been GIN singleton classes that carry state like the selected profile, host and server,
 * but also the current user and other "global" statements.
 *
 * @author Heiko Braun
 * @date 2/6/13
 */
public class CoreGUIContext implements StatementContext {

    public final static String USER = "global.user";
    public final static String SELECTED_PROFILE = "selected.profile";
    public final static String SELECTED_HOST = "selected.host";
    public final static String SELECTED_SERVER = "selected.server";


    private CurrentProfileSelection profileSelection;
    private CurrentUser userSelection;
    private StatementContext delegate = null;


    @Inject
    public CoreGUIContext(CurrentProfileSelection profileSelection, CurrentUser userSelection) {
        this.profileSelection = profileSelection;
        this.userSelection = userSelection;
    }

    public CoreGUIContext(CurrentProfileSelection profileSelection, CurrentUser userSelection, StatementContext delegate) {
        this(profileSelection, userSelection);
        this.delegate = delegate;
    }

    @Override
    public String resolve(String key) {
        if(USER.equals(key))
            return userSelection.getUserName();
        else if(delegate!=null)
            return delegate.resolve(key);

        return null;
    }

    @Override
    public String[] resolveTuple(String key) {
        if(SELECTED_PROFILE.equals(key) && profileSelection.isSet())
            return new String[] {"profile", profileSelection.getName()};
        return null;
    }
}
