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

/**
 * Value object for an author.
 *
 * @author Harald Pehl
 * @date 10/24/2012
 */
public class Author
{
    public final static Author EMPTY = new Author("n/a", "n/a");
    public final static Author JBOSS = new Author("Jboss", "jboss-as7-dev@lists.jboss.org");

    private final String name;
    private final String email;

    public Author(final String name, final String email)
    {
        this.name = name;
        this.email = email;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) { return true; }
        if (!(o instanceof Author)) { return false; }

        Author author = (Author) o;
        if (!email.equals(author.email)) { return false; }
        if (!name.equals(author.name)) { return false; }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = name.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "Author{" + name + ", " + email + '}';
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }
}
