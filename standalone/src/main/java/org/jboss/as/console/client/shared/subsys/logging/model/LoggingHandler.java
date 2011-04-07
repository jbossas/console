/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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

package org.jboss.as.console.client.shared.subsys.logging.model;

/**
 * Model for a Logging Handler
 *
 * @author Stan Silvert
 */
public interface LoggingHandler {
  
   String getName();
   void setName(String name);
   
   boolean isAutoflush();
   void setAutoflush(boolean autoflush);
   
   String getEncoding();
   void setEncoding(String encoding);
   
   String getFormatter();
   void setFormatter(String formatter);
   
   String getType();
   void setType(String type);
   
   String getLevel();
   void setLevel(String level);
   
   String getQueueLength();
   void setQueueLength(String queueLength);
}
