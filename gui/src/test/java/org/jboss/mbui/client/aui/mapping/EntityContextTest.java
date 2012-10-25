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
package org.jboss.mbui.client.aui.mapping;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
public class EntityContextTest
{
    EntityContext cut;

    @Before
    public void setUp() throws Exception
    {
        cut = new EntityContext("test");
    }

    @Test
    public void addNullMapping() throws Exception
    {
        cut.addMapping(null);
        assertTrue(cut.getMappings().isEmpty());
    }

    @Test
    public void addMapping() throws Exception
    {
        TestableMapping mapping = new TestableMapping();
        cut.addMapping(mapping);
        assertEquals(1, cut.getMappings().size());
        assertEquals(mapping, cut.getMappings().iterator().next());
    }

    @Test
    public void getNoneExistingMapping() throws Exception
    {
        Mapping mapping = cut.getMapping("foo");
        assertNotNull(mapping);
        assertEquals(Mapping.EMPTY, mapping);
    }
}
