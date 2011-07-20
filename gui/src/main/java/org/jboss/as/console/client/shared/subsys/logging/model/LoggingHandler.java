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

package org.jboss.as.console.client.shared.subsys.logging.model;

import java.util.List;

/**
 * Model for a Logging Handler
 *
 * @author Stan Silvert
 */
public interface LoggingHandler {
  
   String getType();
   void setType(String type);
   
   String getName();
   void setName(String name);
   
   String getLevel();
   void setLevel(String level);
   
   String getEncoding();
   void setEncoding(String encoding);
   
   String getFilter();
   void setFilter(String filter);
   
   String getFormatter();
   void setFormatter(String formatter);
   
   boolean isAutoflush();
   void setAutoflush(boolean autoflush);
   
   boolean isAppend();
   void setAppend(boolean append);
   
   String getFileRelativeTo();
   void setFileRelativeTo(String fileRelativeTo);
   
   String getFilePath();
   void setFilePath(String filePath);
   
   String getRotateSize();
   void setRotateSize(String rotateSize);
   
   String getMaxBackupIndex();
   void setMaxBackupIndex(String maxBackupIndex);
   
   String getTarget();
   void setTarget(String target);
   
   String getOverflowAction();
   void setOverflowAction(String overflowAction);
   
   List<String> getSubhandlers();
   void setSubhandlers(List<String> list);
   
   String getQueueLength();
   void setQueueLength(String queueLength);
   
   String getSuffix();
   void setSuffix(String suffix);
   
   /**
    * Get the subhandler that needs to be assigned to an async handler.
    * @param handlerName 
    */
   String getHandlerToAssign();
   
   /**
    * Set a subhandler that the presenter will try to assign.  This is for async handlers only.
    * @param handlerName The handler name.
    */
   void setHandlerToAssign(String handlerName);
   
   /**
    * Get the subhandler that needs to be unassigned from an async handler.
    * @param handlerName 
    */
   String getHandlerToUnassign();
   
   /**
    * Set a subhandler that the presenter will try to unassign.  This is for async handlers only.
    * @param handlerName The handler name.
    */
   void setHandlerToUnassign(String handlerName);
}
