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
package org.jboss.mbui.client.aui.aim;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.jboss.mbui.client.aui.aim.EventType.*;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 10/26/2012
 */
public class InteractionUnitTest
{
    InteractionUnit unit;

    @Before
    public void setUp() throws Exception
    {
        this.unit = new TestableInteractionUnit("test");
    }

    @Test
    public void newInstance()
    {
        assertNotNull(unit.getEntityContext());
        assertEquals("test" + InteractionUnit.ENTITY_CONTEXT_SUFFIX, unit.getEntityContext().getId());
    }

    @Test
    public void testVerifyEventTypeConstraints() {
        Container container = new Container("parent", TemporalOperator.OrderIndependance);

        Input textInput = new Input("firstName");
        Input submit = new Input("submit");

        container.add(textInput);
        container.add(submit);

        assertFalse("Should not produce events by default", submit.doesProduceEvents());

        Event<TypeInteraction> submitEvent = new Event<TypeInteraction>("submitNameEvent");
        submit.setProducedEvents(submitEvent);

        assertTrue("submit should produce events", submit.doesProduceEvents());

        assertFalse("submit should not consume interaction events",
                container.consumes(new Event<TypeInteraction>("pressCancel"))
        );

    }

    @Test
    public void testBehaviourResolution() {
        Event<TypeInteraction> submitEvent = new Event<TypeInteraction>("submitNameEvent");
        Event<TypeSystem> deviceRotation= new Event<TypeSystem>("deviceRotation");

        Behaviour behaviour = new Behaviour(submitEvent, "onSubmitName");

        assertTrue("Behaviour should not be triggered by deviceRotation", behaviour.consumes(deviceRotation));
    }
}
