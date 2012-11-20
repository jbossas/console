package org.jboss.as.console.client.mbui.cui.behaviour;

import com.google.gwt.user.client.Command;
import org.jboss.as.console.client.mbui.aui.aim.QName;

/**
 * @author Heiko Braun
 * @date 11/16/12
 */
public class BehaviourExecution {

    private Command command;
    private QName requiredSource;
    private QName triggerId;


    public BehaviourExecution( QName triggerId, Command command) {
        this.command = command;
        this.triggerId = triggerId;
    }

    public BehaviourExecution( QName triggerId, QName requiredSource, Command command) {
        this.command = command;
        this.requiredSource = requiredSource;
        this.triggerId = triggerId;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public QName getRequiredSource() {
        return requiredSource;
    }

    public void setRequiredSource(QName requiredSource) {
        this.requiredSource = requiredSource;
    }

    /**
     * The behaviour is selected by matching trigger id's
     * @return
     */
    public QName getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(QName triggerId) {
        this.triggerId = triggerId;
    }

    /**
     * If the trigger id matches and a source id is given,
     * the source id needs to match as well (gate keeping)
     * @param source
     * @return
     */
    public boolean doesMatch(QName triggerId, Object source)
    {
        // first match by trigger id
        if(!getTriggerId().equals(triggerId))
            return false;

        // second match sourceId (if given)
        if(null==this.requiredSource)
            return true;
        else
            return this.requiredSource.equals(source);
    }
}
