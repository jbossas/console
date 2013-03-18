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

import static org.jboss.mbui.TestNamespace.NAMESPACE;
import static org.jboss.mbui.model.structure.TemporalOperator.*;
import static org.jboss.mbui.model.structure.as7.StereoTypes.EditorPanel;
import static org.jboss.mbui.model.structure.as7.StereoTypes.Form;
import static org.junit.Assert.assertEquals;

import org.jboss.mbui.model.mapping.Mapping;
import org.jboss.mbui.model.mapping.as7.DMRMapping;
import org.jboss.mbui.model.structure.as7.StereoTypes;
import org.jboss.mbui.model.structure.impl.Builder;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Harald Pehl
 * @date 11/14/2012
 */
public class BuilderTest
{
    Builder cut;
    InteractionUnit iuFixture;
    Mapping mappingFixture;

    @Before
    public void setUp() throws Exception
    {
        cut = new Builder();
        iuFixture = new TestableInteractionUnit(NAMESPACE, "test", "Test");
        mappingFixture = new DMRMapping();
    }

    @Test(expected = IllegalStateException.class)
    public void addWithoutCurrent()
    {
        cut.add(iuFixture);
    }

    @Test(expected = IllegalStateException.class)
    public void addMappingWithoutCurrent()
    {
        cut.mappedBy(mappingFixture);
    }

    @Test(expected = IllegalStateException.class)
    public void buildWithoutCurrent()
    {
        cut.build();
    }

    @Test(expected = IllegalStateException.class)
    public void startEndMismatch()
    {
        cut.start(new Container(NAMESPACE, "root", "Root", Choice)).add(iuFixture).build();
    }

    @Test
    public void build()
    {
        InteractionUnit interactionUnit = cut
            .start(new Container<StereoTypes>(NAMESPACE, "root", "Root", OrderIndependance, EditorPanel))
                .add(new Select(NAMESPACE, "table", "Table"))
                .start(new Container(NAMESPACE, "forms", "Forms", Choice))
                    .add(new Container<StereoTypes>(NAMESPACE, "basicAttributes", "Basic Attributes", Concurrency,
                            Form))
                    .add(new Container<StereoTypes>(NAMESPACE, "extendedAttributes", "Extended Attributes", Concurrency,
                            Form))
                .end()
            .end().build();

        // root
        Container root = (Container) interactionUnit;
        assertEquals(new QName(NAMESPACE, "root"), root.getId());
        assertEquals(2, root.getChildren().size());

        // table
        InteractionUnit table = (InteractionUnit) root.getChildren().get(0);
        assertEquals(new QName(NAMESPACE, "table"), table.getId());

        // forms
        Container forms = (Container) root.getChildren().get(1);
        assertEquals(new QName(NAMESPACE, "forms"), forms.getId());
        assertEquals(2, forms.getChildren().size());

        // basicAttributes
        InteractionUnit ba = (InteractionUnit) forms.getChildren().get(0);
        assertEquals(new QName(NAMESPACE, "basicAttributes"), ba.getId());

        // extendedAttributes
        InteractionUnit ea = (InteractionUnit) forms.getChildren().get(1);
        assertEquals(new QName(NAMESPACE, "extendedAttributes"), ea.getId());
    }
}
