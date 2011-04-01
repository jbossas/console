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
