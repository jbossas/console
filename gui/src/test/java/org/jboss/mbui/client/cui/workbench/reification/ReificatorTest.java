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
package org.jboss.mbui.client.cui.workbench.reification;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.mbui.client.aui.aim.Container;
import org.jboss.mbui.client.aui.aim.Input;
import org.jboss.mbui.client.aui.aim.Select;
import org.jboss.mbui.client.cui.Context;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.jboss.mbui.client.aui.aim.TemporalOperator.Choice;
import static org.jboss.mbui.client.aui.aim.TemporalOperator.OrderIndependance;
import static org.junit.Assert.*;

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
        GWTMockUtilities.disarm();
        cut = new Reificator();
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

    @After
    public void tearDown()
    {
        GWTMockUtilities.restore();
    }

    @Test
    public void testReify() throws Exception
    {
        ContainerWidget cw = cut.reify(iuFixture, new Context());
        assertNotNull(cw);

        // root
        assertEquals("root", cw.asWidget().getLayoutData());
        assertTrue(cw.asWidget() instanceof HasWidgets);
        HasWidgets container = (HasWidgets) cw.asWidget();
        Iterator<Widget> iterator = container.iterator();

        // table
        Widget widget = iterator.next();
        assertEquals("table", widget.getLayoutData());

        // forms
        widget = iterator.next();
        assertEquals("forms", widget.getLayoutData());
        assertTrue(widget instanceof HasWidgets);
        container = (HasWidgets) widget;
        iterator = container.iterator();

        // basicAttributes
        widget = iterator.next();
        assertEquals("basicAttributes", widget.getLayoutData());

        // extendedAttributes
        widget = iterator.next();
        assertEquals("extendedAttributes", widget.getLayoutData());
    }
}
