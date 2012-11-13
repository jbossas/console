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
package org.jboss.as.console.client.mbui.cui;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
public class Context
{
    private Stack<Map<String, Object>> subcontexts = new Stack<Map<String, Object>>();


    public Context()
    {
        push();
    }

    public void push()
    {
        subcontexts.push(new HashMap<String, Object>());
    }

    public void pop()
    {
        subcontexts.pop();
    }

    public <T> Context set(final String name, final T value)
    {
        subcontexts.peek().put(name, value);
        return this;
    }

    public <T> T get(final String name)
    {
        Object value = subcontexts.peek().get(name);
        if (value != null)
        {
            return (T) value;
        }
        return null;
    }

    public boolean has(final String name)
    {
        Object value = subcontexts.peek().get(name);
        return value != null;
    }

    @Override
    public String toString()
    {
        return "Context {sub=" + subcontexts.size() + "}";
    }
}
