package org.jboss.mbui.gui.behaviour;

import org.jboss.mbui.model.behaviour.Consumer;
import org.jboss.mbui.model.behaviour.Producer;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.structure.QName;
import org.jboss.mbui.model.structure.impl.ResourceConsumption;
import org.jboss.mbui.model.structure.impl.ResourceProduction;

import java.util.Set;

/**
 * An executable behaviour.
 * Corresponds to {@link org.jboss.mbui.model.behaviour.Behaviour} of the interface model.
 *
 * @author Heiko Braun
 * @date 11/16/12
 */
public abstract class Procedure implements Consumer, Producer {

    private ModelDrivenCommand command;
    private QName requiredOrigin;
    private QName id;

    private ResourceProduction production = new ResourceProduction();
    private ResourceConsumption consumption = new ResourceConsumption();

    protected InteractionCoordinator coordinator;
    protected StatementContext statementContext;
    protected Precondition precondition;

    public final static Precondition NOT_GUARDED = new Precondition() {
        @Override
        public boolean isMet(StatementContext context) {
            return true;
        }
    };

    public Procedure(QName id, QName requiredOrigin) {
        this.id = id;
        this.requiredOrigin = requiredOrigin;
        this.precondition = NOT_GUARDED;
    }

    public void setCommand(ModelDrivenCommand command) {
        this.command = command;
    }

    public void setPrecondition(Precondition precondition) {
        this.precondition = precondition;
    }

    Precondition getPrecondition() {
        return precondition;
    }

    public QName getRequiredOrigin() {
        return requiredOrigin;
    }

    public ModelDrivenCommand getCommand() {
        return command;
    }

    public QName getId() {
        return id;
    }

    void setCoordinator(InteractionCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    void setStatementContext(StatementContext statementContext) {
        this.statementContext = statementContext;
    }

    // --- Consumer ----

    /**
     * Does consume if the resource and the origin (if given) matches.
     *
     * @param resource the actual resource to be consumed
     * @return boolean
     */
    @Override
    public boolean doesConsume(Resource<ResourceType> resource) {
        boolean resourceMatches = consumption.doesConsume(resource);
        boolean originMatches = (requiredOrigin == null) || requiredOrigin.equalsIgnoreSuffix(resource.getSource());
        return resourceMatches && originMatches;
    }

    @Override
    public Set<Resource<ResourceType>> getInputs() {
        return consumption.getInputs();
    }

    @Override
    public void setInputs(Resource<ResourceType>... resources) {
        consumption.setInputs(resources);
    }

    // --- Consumer ----
    
    @Override
    public boolean doesProduce() {
        return production.doesProduce();
    }

    @Override
    public void setOutputs(Resource<ResourceType>... resources) {
        production.setOutputs(resources);
    }

    @Override
    public Set<Resource<ResourceType>> getOutputs() {
        return production.getOutputs();
    }
}
