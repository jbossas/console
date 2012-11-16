/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.mbui.aui.aim;

import org.jboss.as.console.client.mbui.aui.aim.as7.Form;
import org.jboss.as.console.client.mbui.aui.mapping.Predicate;
import org.jboss.as.console.client.mbui.aui.mapping.as7.ResourceMapping;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.jboss.as.console.client.mbui.TestNamespace.NAMESPACE;
import static org.jboss.as.console.client.mbui.aui.aim.EventType.*;
import static org.jboss.as.console.client.mbui.aui.aim.EventType.System;
import static org.jboss.as.console.client.mbui.aui.aim.EventType.Transition;
import static org.jboss.as.console.client.mbui.aui.aim.TemporalOperator.Choice;
import static org.jboss.as.console.client.mbui.aui.aim.TemporalOperator.OrderIndependance;
import static org.jboss.as.console.client.mbui.aui.mapping.MappingType.RESOURCE;
import static org.junit.Assert.*;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 10/26/2012
 */
public class InteractionUnitTest
{
    InteractionUnit cut;

    @Before
    public void setUp()
    {
        this.cut = new TestableInteractionUnit(NAMESPACE, "test", "Test");
    }

    @Test
    public void verifyBehaviourConstraints()
    {

        Container container = new Container(NAMESPACE, "parent", "Parent", TemporalOperator.OrderIndependance);

        Input textInput = new Input(NAMESPACE, "firstName", "Firstname");
        Input submit = new Input(NAMESPACE, "submit", "Submit");

        container.add(textInput);
        container.add(submit);

        assertFalse("Should not produce events by default", submit.doesProduceEvents());

        Event<EventType> submitEvent = new Event<EventType>(NAMESPACE, "submitName", Interaction);
        submit.setProducedEvents(submitEvent);

        assertTrue("submit should produce events", submit.doesProduceEvents());

        assertFalse("submit should not consume interaction events",
                container.isTriggeredBy(new Event<EventType>(NAMESPACE, "pressCancel", Interaction))
        );

        Behaviour handleSubmit = new Behaviour(NAMESPACE, "onSubmitName", submitEvent);
        assertTrue("Behaviour should be triggered by submitEvent", handleSubmit.isTriggeredBy(submitEvent));

        // verify integrity
        // integrity is given when any produced type is matched by a consumer
        final Set<Behaviour> behaviours = new HashSet<Behaviour>();
        behaviours.add(handleSubmit);

        container.accept(new InteractionUnitVisitor() {
            @Override
            public void startVisit(Container container) {
                if(container.doesProduceEvents())
                    assertTrue(isBehaviourDeclared(container));
            }

            @Override
            public void visit(InteractionUnit interactionUnit) {
                if(interactionUnit.doesProduceEvents())
                    assertTrue(isBehaviourDeclared(interactionUnit));
            }

            @Override
            public void endVisit(Container container) {

            }

            boolean isBehaviourDeclared(InteractionUnit unit)
            {
                boolean isDeclared = false;

                for(Behaviour candidate : behaviours)
                {
                    Set<Event<EventType>> producedTypes = unit.getProducedEvents();
                    for(Event<EventType> event : producedTypes)
                    {
                        if(candidate.isTriggeredBy(event))
                        {
                            isDeclared = true;
                            break;
                        }
                    }

                    if(isDeclared) break;
                }

                return isDeclared;
            }
        });
    }

    @Test
    public void behaviourResolution()
    {
        Event<EventType> submitEvent = new Event<EventType>(NAMESPACE, "submitName", Interaction);
        Event<EventType> deviceRotation = new Event<EventType>(NAMESPACE, "deviceRotation", System);
        Event<EventType> loadData = new Event<EventType>(NAMESPACE, "loadData", Transition);

        Behaviour behaviour = new Behaviour(NAMESPACE, "onSubmitName", submitEvent);

        assertTrue("Behaviour can be triggered by submitEvent", behaviour.isTriggeredBy(submitEvent));

        assertFalse("Behaviour should not be triggered by deviceRotation", behaviour.isTriggeredBy(deviceRotation));
        assertFalse("Behaviour should not be triggered by loadData", behaviour.isTriggeredBy(loadData));

        final StringBuffer sharedState = new StringBuffer("");

        behaviour.setCondition(new Condition()
        {
            @Override
            public boolean isMet()
            {
                return true;
            }
        });

        behaviour.addTransition(new FunctionCall()
        {
            @Override
            public void perform()
            {
                sharedState.append("updated");
            }
        });

        if (behaviour.isTriggeredBy(loadData))
        {
            behaviour.execute();
            assertTrue("sharedState should be updated", sharedState.toString().equals("updated"));
        }
    }

    @Test
    public void findMapping()
    {
        Form basicAttributes = new Form(NAMESPACE, "basicAttributes", "Basic Attributes");
        InteractionUnit root = new Builder()
            .start(new Container(NAMESPACE, "root", "Root", OrderIndependance))
            .addMapping(new ResourceMapping(NAMESPACE).setAddress("root"))
                .add(new Select(NAMESPACE, "table", "Table"))
                .start(new Container(NAMESPACE, "forms", "Forms", Choice))
                    .add(basicAttributes)
                    .addMapping(new ResourceMapping(NAMESPACE).setAddress("basicAttributes"))
                    .add(new Form(NAMESPACE, "extendedAttributes", "Basic Attributes"))
                .end()
            .end().build();

        ResourceMapping mapping = basicAttributes.findMapping(RESOURCE);
        assertNotNull(mapping);
        assertEquals("basicAttributes", mapping.getAddress());

        mapping = basicAttributes.findMapping(RESOURCE, new Predicate<ResourceMapping>()
        {
            @Override
            public boolean appliesTo(final ResourceMapping candidate)
            {
                return "root".equals(candidate.getAddress());
            }
        });
        assertNotNull(mapping);
        assertEquals("root", mapping.getAddress());
    }
}
