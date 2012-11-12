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
package org.jboss.as.console.client.mbui.cui.reification;

import org.jboss.as.console.client.mbui.aui.aim.Container;
import org.jboss.as.console.client.mbui.aui.aim.Input;
import org.jboss.as.console.client.mbui.aui.aim.Select;
import org.jboss.as.console.client.mbui.cui.Context;
import org.junit.Before;
import org.junit.Test;

import static org.jboss.as.console.client.mbui.aui.aim.TemporalOperator.Choice;
import static org.jboss.as.console.client.mbui.aui.aim.TemporalOperator.OrderIndependance;
import static org.junit.Assert.assertEquals;

/**
 * @author Harald Pehl
 * @date 10/30/2012
 */
public class ReificatorTest
{
    Reificator cut;
    Container iuFixture;

    @Before
    public void setUp() throws Exception
    {
        cut = new Reificator(null);
        cut.strategies.clear();
        cut.strategies.add(new TestableReificationStrategy());

        iuFixture = new Container("root", OrderIndependance);
        Select table = new Select("table");
        iuFixture.add(table);
        Container forms = new Container("forms", Choice);
        iuFixture.add(forms);
        Input basicAttributes = new Input("basicAttributes");
        forms.add(basicAttributes);
        Input extendedAttributes = new Input("extendedAttributes");
        forms.add(extendedAttributes);
    }

    @Test
    public void testReify() throws Exception
    {
        TestableReificationWidget root = (TestableReificationWidget) cut.reify(iuFixture, new Context());

        // root
        assertEquals("root", root.interactionUnit.getId());
        assertEquals(2, root.children.size());

        // table
        TestableReificationWidget table = (TestableReificationWidget) root.children.get(0);
        assertEquals("table", table.interactionUnit.getId());

        // forms
        TestableReificationWidget forms = (TestableReificationWidget) root.children.get(1);
        assertEquals("forms", forms.interactionUnit.getId());
        assertEquals(2, forms.children.size());

        // basicAttributes
        TestableReificationWidget ba = (TestableReificationWidget) forms.children.get(0);
        assertEquals("basicAttributes", ba.interactionUnit.getId());

        // extendedAttributes
        TestableReificationWidget ea = (TestableReificationWidget) forms.children.get(1);
        assertEquals("extendedAttributes", ea.interactionUnit.getId());
    }
}
