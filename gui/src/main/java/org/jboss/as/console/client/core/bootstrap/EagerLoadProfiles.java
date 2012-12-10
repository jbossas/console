package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.profiles.CurrentProfileSelection;

import java.util.Iterator;
import java.util.List;

/**
 * If links come in from external contexts, the initialization might
 * get out of order. However the current profile must be selected, otherwise Baseadress.get()
 * will yield wrong results.
 *
 * @author Heiko Braun
 * @date 1/13/12
 */
public class EagerLoadProfiles extends BoostrapStep {

    private ProfileStore profileStore;
    private CurrentProfileSelection profileSelection;

    public EagerLoadProfiles(ProfileStore profileStore, CurrentProfileSelection profileSelection) {
        this.profileStore = profileStore;
        this.profileSelection = profileSelection;
    }

    @Override
    public void execute(final Iterator<BoostrapStep> iterator, final AsyncCallback<Boolean> outcome) {

        BootstrapContext bootstrapContext = Console.getBootstrapContext();

        if(!bootstrapContext.isStandalone())
        {
            profileStore.loadProfiles(new SimpleCallback<List<ProfileRecord>>() {

                @Override
                public void onFailure(Throwable caught) {
                    outcome.onSuccess(Boolean.FALSE);
                    next(iterator, outcome);
                }

                @Override
                public void onSuccess(List<ProfileRecord> result) {

                    // default profile
                    if (!result.isEmpty()) {
                        selectDefaultProfile(result);
                    }

                    outcome.onSuccess(Boolean.TRUE);
                    next(iterator, outcome);
                }
            });

        }
        else
        {
            // standalone
            outcome.onSuccess(Boolean.TRUE);
            next(iterator, outcome);
        }

    }

    private void selectDefaultProfile(List<ProfileRecord> result) {

        if(!profileSelection.isSet())
        {
            String name = result.get(0).getName();
            System.out.println("Eager profile selection: "+name);
            profileSelection.setName(name);
        }
    }

}
