package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.profiles.CurrentProfileSelection;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;

import java.util.List;

/**
 * If links come in from external contexts, the initialization might
 * get out of order. However the current profile must be selected, otherwise Baseadress.get()
 * will yield wrong results.
 *
 * @author Heiko Braun
 * @date 1/13/12
 */
public class EagerLoadProfiles implements AsyncCommand<Boolean> {

    @Override
    public void execute(final AsyncCallback<Boolean> callback) {

        BootstrapContext bootstrapContext = Console.getBootstrapContext();

        if(!bootstrapContext.isStandalone())
        {
            ProfileStore profileStore = Console.MODULES.getProfileStore();

            profileStore.loadProfiles(new SimpleCallback<List<ProfileRecord>>() {

                @Override
                public void onFailure(Throwable caught) {
                    callback.onSuccess(Boolean.FALSE);
                    throw new RuntimeException(caught);
                }

                @Override
                public void onSuccess(List<ProfileRecord> result) {

                    // default profile
                    if (!result.isEmpty()) {
                        selectDefaultProfile(result);
                    }

                    callback.onSuccess(Boolean.TRUE);
                }
            });

        }
        else
        {
            // standalone
            callback.onSuccess(Boolean.TRUE);
        }
    }

    private void selectDefaultProfile(List<ProfileRecord> result) {

        CurrentProfileSelection profileSelection = Console.MODULES.getCurrentSelectedProfile();
        if(!profileSelection.isSet())
        {
            String name = result.get(0).getName();
            System.out.println("Eager profile selection: "+name);
            profileSelection.setName(name);
        }
    }

}
