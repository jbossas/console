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
package org.jboss.mbui.model.structure;

import org.jboss.mbui.gui.behaviour.Integrity;
import org.jboss.mbui.gui.behaviour.IntegrityErrors;
import org.jboss.mbui.gui.behaviour.Procedure;
import org.jboss.mbui.gui.behaviour.as7.BehaviourMap;
import org.jboss.mbui.model.behaviour.Behaviour;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.mapping.Predicate;
import org.jboss.mbui.model.mapping.as7.DMRMapping;
import org.jboss.mbui.model.structure.as7.Form;
import org.jboss.mbui.model.structure.impl.Builder;
import org.junit.Before;
import org.junit.Test;

import static org.jboss.mbui.TestNamespace.NAMESPACE;
import static org.jboss.mbui.model.behaviour.ResourceType.Interaction;
import static org.jboss.mbui.model.behaviour.ResourceType.Presentation;
import static org.jboss.mbui.model.mapping.MappingType.DMR;
import static org.jboss.mbui.model.structure.TemporalOperator.Choice;
import static org.jboss.mbui.model.structure.TemporalOperator.OrderIndependance;
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

        Input textInput = new Input(NAMESPACE, "firstName", "Firstname");
        Input submit = new Input(NAMESPACE, "submitButton", "Submit");
        Input close = new Input(NAMESPACE, "closeButton", "Close Dialogue");

        InteractionUnit container = new Builder()
                .start(new Container(NAMESPACE, "window", "Window", TemporalOperator.OrderIndependance))
                .add(textInput)
                .add(submit)
                .add(close)
                .end()
                .build();

        assertFalse("Should not produce events by default", submit.doesProduce());

        final Resource<ResourceType> submitEvent = new Resource<ResourceType>(NAMESPACE, "submit", Interaction);
        submit.setOutputs(submitEvent);

        assertTrue("submit should produce events", submit.doesProduce());
        assertFalse("submit should not consume interaction events",
                container.doesConsume(new Resource<ResourceType>(NAMESPACE, "pressCancel", Interaction))
        );


        TestProcedure handleSubmit = new TestProcedure(NAMESPACE, "handleSubmit")
        {
            {
                setInputs(submitEvent);
            }
        };
        assertTrue("Behaviour should be triggered by submitEvent", handleSubmit.doesConsume(submitEvent));

        final Resource<ResourceType> presentationData = new Resource<ResourceType>(NAMESPACE, "data", Presentation);
        TestProcedure resourcePresentation = new TestProcedure(NAMESPACE, "updateDisplay")
        {
            {
                setOutputs(presentationData);
            }
        };
        container.setInputs(presentationData);

        // integrity checks
        final BehaviourMap<TestProcedure> behaviours = new BehaviourMap<TestProcedure>();
        behaviours.add(handleSubmit);
        behaviours.add(resourcePresentation);

        // the integrity check will pass
        try {
            verifyIntegrity(container, behaviours);
        } catch (IntegrityErrors e) {
            throw new AssertionError("Should not raise error: "+e.getMessage());
        }

        // create a derivation that causes the integrity check to fail
        Resource<ResourceType> closeEvent = new Resource<ResourceType>(NAMESPACE, "dialog-close", Interaction);
        close.setOutputs(closeEvent);

        try {
            verifyIntegrity(container, behaviours);
        } catch (IntegrityErrors err) {

            java.lang.System.out.print(err.getMessage());
            // all good, this is expected
        }
    }

    private void verifyIntegrity(InteractionUnit container, BehaviourMap behaviours)
            throws IntegrityErrors {
        Integrity.check(container, behaviours);
    }

    @Test
    public void behaviourResolution()
    {
        final Resource<ResourceType> submitEvent = new Resource<ResourceType>(NAMESPACE, "submitName", Interaction);
        Resource<ResourceType> deviceRotation = new Resource<ResourceType>(NAMESPACE, "deviceRotation", Interaction);
        Resource<ResourceType> loadData = new Resource<ResourceType>(NAMESPACE, "loadData", Interaction);

        Behaviour behaviour = new TestProcedure(NAMESPACE, "onSubmitName")
        {
            {
                setInputs(submitEvent);
            }

        };

        assertTrue("Behaviour can be triggered by submitEvent", behaviour.doesConsume(submitEvent));

        assertFalse("Behaviour should not be triggered by deviceRotation", behaviour.doesConsume(deviceRotation));
        assertFalse("Behaviour should not be triggered by loadData", behaviour.doesConsume(loadData));

    }

    @Test
    public void findMapping()
    {
        Form basicAttributes = new Form(NAMESPACE, "basicAttributes", "Basic Attributes");
        InteractionUnit root = new Builder()
                .start(new Container(NAMESPACE, "root", "Root", OrderIndependance))
                .mappedBy(new DMRMapping().setAddress("root"))
                .add(new Select(NAMESPACE, "table", "Table"))
                .start(new Container(NAMESPACE, "forms", "Forms", Choice))
                .add(basicAttributes)
                .mappedBy(new DMRMapping().setAddress("basicAttributes"))
                .add(new Form(NAMESPACE, "extendedAttributes", "Basic Attributes"))
                .end()
                .end().build();

        // TODO: find resource mapping type & namespace is what we actual needs I think.
        DMRMapping mapping = basicAttributes.findMapping(DMR, new Predicate<DMRMapping>() {
            @Override
            public boolean appliesTo(DMRMapping candidate) {
                return candidate.getNamespace().equals(NAMESPACE);
            }
        });
        assertNotNull(mapping);
        assertEquals("basicAttributes", mapping.getAddress());

        mapping = basicAttributes.findMapping(DMR, new Predicate<DMRMapping>()
        {
            @Override
            public boolean appliesTo(final DMRMapping candidate)
            {
                return "root".equals(candidate.getAddress());
            }
        });
        assertNotNull(mapping);
        assertEquals("root", mapping.getAddress());
    }


    @Test
    public void testProcedureEquality()
    {
        Procedure proc1 = new TestProcedure(QName.valueOf("foo.bar:proc")) {};
        Procedure proc2 = new TestProcedure(QName.valueOf("foo.bar:proc")) {};
        Procedure proc3 = new TestProcedure(QName.valueOf("foo.bar:proc"), QName.valueOf("some:origin")) {};
        Procedure proc4 = new TestProcedure(QName.valueOf("foo.bar:proc2")) {};
        Procedure proc5 = new TestProcedure(QName.valueOf("foo.bar:proc"), QName.valueOf("some:origin")) {};

        assertEquals(proc1, proc2);
        assertFalse(proc2.equals(proc3));
        assertFalse(proc1.equals(proc4));
        assertEquals(proc3, proc5);
    }
}
