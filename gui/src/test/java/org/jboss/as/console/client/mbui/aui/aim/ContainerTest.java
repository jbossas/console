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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Harald Pehl
 * @date 10/26/2012
 */
public class ContainerTest
{
    Container cut;

    @Before
    public void setUp() throws Exception
    {
        this.cut = new Container("test", TemporalOperator.Choice);
    }

    @Test
    public void parentChild()
    {
        InteractionUnit foo = new TestableInteractionUnit("foo");
        InteractionUnit bar = new TestableInteractionUnit("bar");

        cut.add(foo);
        assertEquals(foo, cut.getChildren().get(0));

        cut.add(bar);
        assertEquals(bar, cut.getChildren().get(1));

        cut.remove(bar);
        assertFalse(cut.getChildren().contains(bar));

        cut.remove(foo);
        assertFalse(cut.getChildren().contains(foo));
    }
}
