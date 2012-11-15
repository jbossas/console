package org.jboss.as.console.client.core.bootstrap;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Iterator;

/**
 * @author Heiko Braun
 * @date 11/15/12
 */
public abstract class BoostrapStep {


    public abstract void execute(Iterator<BoostrapStep> iterator, AsyncCallback<Boolean> outcome);

    protected final void next(
            final Iterator<BoostrapStep> iterator, final AsyncCallback<Boolean> outcome)
    {
        if (iterator.hasNext())
        {
            BoostrapStep nextAction = iterator.next();

            Log.debug("Bootstrap: "+nextAction.getClass());
            nextAction.execute(iterator, outcome);
        }
    }
}
