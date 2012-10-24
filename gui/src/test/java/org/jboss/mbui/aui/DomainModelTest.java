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
package org.jboss.mbui.aui;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Harald Pehl
 * @date 10/24/2012
 */
public class DomainModelTest
{
    DomainModel cut;
    DomainModel.Group groupFixture = new DomainModel.Group("test");

    @Before
    public void setUp()
    {
        this.cut = new DomainModel("test", "/subsystem=test");
    }

    @Test
    public void addNullAttribute()
    {
        cut.addAttributes((String)null);
        assertTrue(cut.getAttributes().isEmpty());
    }

    @Test
    public void addEmptyAttribute()
    {
        cut.addAttributes("");
        assertTrue(cut.getAttributes().isEmpty());
    }

    @Test
    public void addNullAndEmptyAttributes()
    {
        cut.addAttributes("", null, "", "", null, "");
        assertTrue(cut.getAttributes().isEmpty());
    }

    @Test
    public void addDefaultAttribtes()
    {
        cut.addAttributes("foo", "bar");
        assertTrue(cut.getAttributes(groupFixture).isEmpty());
        assertEquals(2, cut.getAttributes().size());
    }

    @Test
    public void addGroupAttribtes()
    {
        cut.addAttributes(groupFixture, "foo", "bar");
        assertTrue(cut.getAttributes().isEmpty());
        assertEquals(2, cut.getAttributes(groupFixture).size());
    }

    @Test
    public void getUnknownGroupAttributes()
    {
        assertNotNull(cut.getAttributes(new DomainModel.Group("unknown")));
    }
}
