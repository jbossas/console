package org.jboss.as.console.client.domain.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Heiko Braun
 * @date 2/7/11
 */
public class ProfileSelectionEvent extends GwtEvent<ProfileSelectionEvent.ProfileSelectionListener> {

    public static final Type TYPE = new Type<ProfileSelectionListener>();

    private String profileName;

    public ProfileSelectionEvent(String profileName) {
        super();
        this.profileName = profileName;
    }

    @Override
    public Type<ProfileSelectionListener> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ProfileSelectionListener listener) {
        listener.onProfileSelection(profileName);
    }

    public String getProfileName() {
        return profileName;
    }

    public interface ProfileSelectionListener extends EventHandler {
        void onProfileSelection(String profileName);
    }
}
