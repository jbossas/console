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

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class Validation {
    private Validation() {}

    public static final ModelNode SCHEMA_DEFINITION;

    public static void validate(ModelNode schema, String rootType) throws IllegalArgumentException {

    }

    static {
        ModelNode schema = new ModelNode();
        ModelNode schemaRoot = schema.get("schemaRoot");
        schemaRoot.get("description").set("The root of a schema.");
        schemaRoot.get("type").set(ModelType.OBJECT);
        schemaRoot.get("propertyType").set("map");
        schemaRoot.get("property", "type").set(ModelType.OBJECT);
        ModelNode typeSpec = schema.get("typeSpecification");
        typeSpec.get("description").set("A description of a specific node in a model graph.");
        typeSpec.get("type").set(ModelType.OBJECT);
        typeSpec.get("propertyType").set("constrained");
        typeSpec.get("property", "description", "type").set(ModelType.STRING);
        typeSpec.get("property", "description", "recommended").set(true);
        typeSpec.get("property", "description", "description").set("The description of the model element type.");
        typeSpec.get("property", "type", "type").set(ModelType.TYPE);
        typeSpec.get("property", "type", "required").set(true);
        typeSpec.get("property", "type", "description").set("The type of the model element type.");
        typeSpec.get("property", "property", "type").set("typeSpecification");
        typeSpec.get("property", "property", "required").set(true);
        typeSpec.get("property", "property", "description").set("The parameters allowed within this model element type, if it is an OBJECT.");
        typeSpec.get("property", "required", "type").set(ModelType.BOOLEAN);
        typeSpec.get("property", "required", "default").set(false);
        typeSpec.get("property", "required", "description").set("Specifies whether a parameter is required to be given.");
        typeSpec.get("property", "recommended", "type").set(ModelType.BOOLEAN);
        typeSpec.get("property", "recommended", "default").set(false);
        typeSpec.get("property", "recommended", "description").set("Specifies whether a parameter is recommended to be given.");
        typeSpec.get("property", "default", "description").set("The default value of this element type.");
        schema.protect();
        SCHEMA_DEFINITION = schema;
    }

}
