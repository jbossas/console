package org.jboss.as.console.client.mbui.aui.aim;

import org.jboss.as.console.client.mbui.aui.aim.assets.EventProduction;

import java.util.Set;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public abstract class Transition implements TriggerSource {

    private QName id;

    protected Transition(QName id) {
        this.id = id;
    }

    public QName getId() {
        return id;
    }

    private EventProduction eventProduction =
            new EventProduction(TriggerType.Transition);

    @Override
    public boolean doesTrigger() {
        return eventProduction.doesTrigger();
    }

    @Override
    public void setOutputs(Trigger<TriggerType>... events) {
        this.eventProduction.setOutputs(events);
    }

    public Set<Trigger<TriggerType>> getOutputs()
    {
        return eventProduction.getOutputs();
    }
}
