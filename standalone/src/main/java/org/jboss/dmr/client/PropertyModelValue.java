/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.dmr.client;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class PropertyModelValue extends ModelValue {

    private final Property property;

    PropertyModelValue(final String name, final ModelNode value) {
        this(new Property(name, value));
    }

    PropertyModelValue(final Property property) {
        super(ModelType.PROPERTY);
        if (property == null) {
            throw new IllegalArgumentException("property is null");
        }
        this.property = property;
    }

    PropertyModelValue(final DataInput in) throws IOException {
        super(ModelType.PROPERTY);
        final ModelNode node = new ModelNode();
        final String name = in.readUTF();
        node.readExternal(in);
        property = new Property(name, node);
    }

    PropertyModelValue(final String key, final ModelNode node, final boolean copy) {
        this(new Property(key, node, copy));
    }

    @Override
    void writeExternal(final DataOutput out) throws IOException {
        out.writeUTF(property.getName());
        property.getValue().writeExternal(out);
    }

    @Override
    ModelValue protect() {
        property.getValue().protect();
        return this;
    }

    @Override
    String asString() {
        return "(" + quote(property.getName()) + " => " + property.getValue() + ")";
    }

    @Override
    Property asProperty() {
        return property;
    }

    @Override
    List<Property> asPropertyList() {
        return Collections.singletonList(property);
    }

    @Override
    ModelNode asObject() {
        final ModelNode node = new ModelNode();
        node.get(property.getName()).set(property.getValue());
        return node;
    }

    @Override
    Set<String> getKeys() {
        return Collections.singleton(property.getName());
    }

    @Override
    List<ModelNode> asList() {
        return Collections.singletonList(new ModelNode(this));
    }

    @Override
    ModelNode getChild(final String name) {
        return property.getName().equals(name) ? property.getValue() : super.getChild(name);
    }

    @Override
    ModelNode getChild(final int index) {
        return index == 0 ? property.getValue() : super.getChild(index);
    }

    @Override
    ModelValue copy() {
        return new PropertyModelValue(property.getName(), property.getValue());
    }

    @Override
    ModelValue resolve() {
        return new PropertyModelValue(property.getName(), property.getValue().resolve());
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof PropertyModelValue && equals((PropertyModelValue)other);
    }

    public boolean equals(final PropertyModelValue other) {
        return this == other || other != null && other.property.getName().equals(property.getName()) && other.property.getValue().equals(property.getValue());
    }

    @Override
    public int hashCode() {
        return property.getName().hashCode() * 31 + property.getValue().hashCode();
    }

    @Override
    boolean has(final String key) {
        return key.equals(property.getName());
    }

    @Override
    ModelNode requireChild(final String name) throws NoSuchElementException {
        return property.getName().equals(name) ? property.getValue() : super.requireChild(name);
    }

    @Override
    void formatAsJSON(final StringBuilder builder, final int indent, final boolean multiLineRequested) {
        builder.append('{');
        builder.append(quote(property.getName()));
        builder.append(" : ");
        property.getValue().formatAsJSON(builder, indent, multiLineRequested);
        builder.append('}');
    }
}
