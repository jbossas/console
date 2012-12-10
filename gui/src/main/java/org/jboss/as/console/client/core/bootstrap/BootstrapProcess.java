package org.jboss.as.console.client.core.bootstrap;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.SimpleCallback;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Heiko Braun
 * @date 12/7/11
 */
public class BootstrapProcess {

    private LinkedList<BoostrapStep> hooks = new LinkedList<BoostrapStep>();

    public void addHook(BoostrapStep hook) {
        hooks.add(hook);
    }

    public void execute(final AsyncCallback<Boolean> outcome) {

        Iterator<BoostrapStep> iterator = hooks.iterator();
        BoostrapStep first = iterator.next();

        Log.debug("Bootstrap: " + first.getClass());

        first.execute(iterator, new SimpleCallback<Boolean>() {

            int numResponses;
            boolean overallResult = true;

            @Override
            public void onSuccess(Boolean successful) {
                numResponses++;
                overallResult = (overallResult&&successful);

                if (numResponses == hooks.size()) {
                    outcome.onSuccess(overallResult);
                }
            }
        });
    }


}
