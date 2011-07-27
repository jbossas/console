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
package org.jboss.as.console.client.shared.subsys.logging;

import static org.jboss.as.console.client.shared.subsys.logging.HandlerAttribute.*;

/**
 * Enum that describes the attributes that belong with each type of Handler.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public enum HandlerType {
    CONSOLE("console-handler", NAME, LEVEL, AUTOFLUSH, ENCODING, FILTER, FORMATTER, TARGET),
    
    ASYNC("async-handler", NAME, LEVEL, OVERFLOW_ACTION, QUEUE_LENGTH, SUBHANDLERS),
    
    FILE("file-handler", NAME, LEVEL, ENCODING, FILTER, FORMATTER, AUTOFLUSH, APPEND, FILE_RELATIVE_TO, FILE_PATH),
    
    SIZE_ROTATING_FILE("size-rotating-file-handler", NAME, LEVEL, ENCODING, FORMATTER, AUTOFLUSH, FILE_RELATIVE_TO, 
                                                     FILE_PATH, ROTATE_SIZE, MAX_BACKUP_INDEX),
    
    PERIODIC_ROTATING_FILE("periodic-rotating-file-handler", NAME, LEVEL, ENCODING, FILTER, FORMATTER, AUTOFLUSH, 
                                                             APPEND, FILE_RELATIVE_TO, FILE_PATH, SUFFIX);
    
    private final String displayName;
    
    private final HandlerAttribute[] attributes;
    
    /**
     * Create a new HandlerType.
     * 
     * @param displayName This is both the name displayed in the UI and used in DMR requests.
     * @param attributes The attributes associated with the HandlerType.
     */
    private HandlerType(String displayName, HandlerAttribute... attributes) {
        this.displayName = displayName;
        this.attributes = attributes;
    }
    
    /**
     * Get the name of each HandlerType.
     * 
     * @return The names.
     */
    public static String[] getAllDisplayNames() {
        HandlerType[] values = HandlerType.values();
        String[] array = new String[values.length];
        for (int i=0; i < values.length; i++) {
            array[i] = values[i].displayName;
        }
        
        return array;
    }
    
    /**
     * Find a HandlerType given its displayName.
     * @param displayName The displanName (DMR type name)
     * @return The HandlerType if found
     * @throws IllegalArgumentException if not found.
     */
    public static HandlerType findHandlerType(String displayName) {
        for (HandlerType handler : HandlerType.values()) {
            if (handler.displayName.equals(displayName)) return handler;
        }
        
        throw new IllegalArgumentException("Unknown HandlerType display name " + displayName);
    }

    public String getDisplayName() {
        return this.displayName;
    }
    
    /**
     * Does this HandlerType have a give attribute?
     * @param attribute The attribute to search for.
     * @return <code>true</code> if the HandlerType has the attribute, <code>false</code> otherwise.
     */
    public boolean hasAttribute(HandlerAttribute attribute) {
        for (HandlerAttribute attrib : attributes) {
            if (attrib == attribute) return true;
        }
        
        return false;
    }
    
    /**
     * Return all the attributes that a given HandlerType has.
     * @return The attributes.
     */
    public HandlerAttribute[] getAttributes() {
        return this.attributes;
    }
}
