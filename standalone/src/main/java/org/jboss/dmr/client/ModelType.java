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
public enum ModelType {
    BIG_DECIMAL('d'),
    BIG_INTEGER('i'),
    BOOLEAN('Z'),
    BYTES('b'),
    DOUBLE('D'),
    EXPRESSION('e'),
    INT('I'),
    LIST('l'),
    LONG('J'),
    OBJECT('o'),
    PROPERTY('p'),
    STRING('s'),
    TYPE('t'),
    UNDEFINED('u');

    final char typeChar;

    ModelType(final char typeChar) {
        this.typeChar = typeChar;
    }

    char getTypeChar() {
        return typeChar;
    }

    static ModelType forChar(char c) {
        switch (c) {
             case 'J': return LONG;
             case 'I': return INT;
             case 'Z': return BOOLEAN;
             case 's': return STRING;
             case 'D': return DOUBLE;
             case 'd': return BIG_DECIMAL;
             case 'i': return BIG_INTEGER;
             case 'b': return BYTES;
             case 'l': return LIST;
             case 't': return TYPE;
             case 'o': return OBJECT;
             case 'p': return PROPERTY;
             case 'e': return EXPRESSION;
             case 'u': return UNDEFINED;
             default: throw new IllegalArgumentException("Invalid type character '" + c + "'");
        }
    }
}
