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

import java.util.Date;

/**
 * Abstract base class for MBUI classes. Subclasses must specify an unique ID accross the class hierarchy.
 * <p/>
 * The id is used for {@link #equals(Object)}, {@link #hashCode()} and {@link #toString()}
 *
 * @author Harald Pehl
 * @date 10/24/2012
 */
public abstract class Model
{
    private final String id;
    private String name;
    private Date creationDate;
    private Date modificationDate;
    private Author author;
    private String comment;


    protected Model(final String id)
    {
        assert id != null : "Id must not be null";
        this.id = id;
        this.creationDate = new Date();
        this.author = Author.EMPTY;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) { return true; }
        if (!(o instanceof Model)) { return false; }

        Model model = (Model) o;
        if (!id.equals(model.id)) { return false; }

        return true;
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public String toString()
    {
        return "Model{" + id + '}';
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(final Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public Date getModificationDate()
    {
        return modificationDate;
    }

    public void setModificationDate(final Date modificationDate)
    {
        this.modificationDate = modificationDate;
    }

    public Author getAuthor()
    {
        return author;
    }

    public void setAuthor(final Author author)
    {
        this.author = author;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(final String comment)
    {
        this.comment = comment;
    }
}
