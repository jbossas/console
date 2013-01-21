package org.jboss.mbui.gui.behaviour;

import org.jboss.mbui.model.structure.QName;

/**
 * An executable behaviour. Corresponds to {@link org.jboss.mbui.model.behaviour.Behaviour} of the interface model.
 *
 * @author Heiko Braun
 * @date 11/16/12
 */
public class Procedure {

    private ModelDrivenCommand command;
    private QName requiredSource;
    private QName id;

    public Procedure(QName resource, QName source, ModelDrivenCommand command) {
        this.command = command;
        this.requiredSource = source;
        this.id = resource;
    }

    public Procedure(QName id, QName source) {
        this.id = id;
        this.requiredSource = source;
    }

    public void setCommand(ModelDrivenCommand command) {
        this.command = command;
    }

    public QName getRequiredSource() {
        return requiredSource;
    }

    public ModelDrivenCommand getCommand() {
        return command;
    }

    /**
     * The procedure is selected by matching id's
     * @return
     */
    public QName getId() {
        return id;
    }

    /**
     * If the id matches and a source id is given,
     * the source id needs to match as well (gate keeping)
     * @param source
     * @return
     */
    public boolean doesMatch(QName triggerId, Object source)
    {
        // first match by trigger id
        if(!getId().equals(triggerId))
            return false;

        // second match source classifier (if given)
        if(null==this.requiredSource)
        {
            return true;
        }
        else
        {
            return this.requiredSource.equalsIgnoreSuffix(source);
        }
    }
}
