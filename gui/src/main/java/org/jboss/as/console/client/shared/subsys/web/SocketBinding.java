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

package org.jboss.as.console.client.shared.subsys.web;

import static org.jboss.dmr.client.ModelDescriptionConstants.INCLUDE_RUNTIME;
import static org.jboss.dmr.client.ModelDescriptionConstants.RECURSIVE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Pavel Slegr
 * @date 3/19/12
 */

public class SocketBinding {

    private DispatchAsync dispatcher;
    private int syncCounter;
    private Map<String,String> sgbBind;
    private String finalSGB = "standard-sockets";
	
	public SocketBinding(DispatchAsync dispatcher) {
		this.dispatcher = dispatcher;
	}
	

   public void loadSocketBindingGroupForName(String groupName, final AsyncCallback<List<String>> callback)
   {
       ModelNode op = new ModelNode();
       op.get(ModelDescriptionConstants.OP).set(ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION);
       op.get(ModelDescriptionConstants.ADDRESS).add("socket-binding-group", groupName);
       op.get(ModelDescriptionConstants.CHILD_TYPE).set("socket-binding");
       
       dispatcher.execute(new DMRAction(op), new AsyncCallback<DMRResponse>() {
           @Override
           public void onFailure(Throwable caught) {
               callback.onFailure(caught);
           }

           @Override
           public void onSuccess(DMRResponse result) {
               ModelNode response = result.get();
               if(ModelAdapter.wasSuccess(response))
               {
                   List<ModelNode> payload = response.get("result").asList();

                   List<String> records = new ArrayList<String>(payload.size());
                   for(ModelNode binding : payload)
                   {
                	   if(binding.asString().contains("socket-binding")){}
                       records.add(binding.asString());
                   }
                   callback.onSuccess(records);
               }
               else
               {
                   callback.onFailure(new RuntimeException("Failed to load socket binding groups"));
               }
           }
       });
   }
   
   
   private void loadServerGroupNames(final AsyncCallback<List<String>> callback)
   {
       ModelNode op = new ModelNode();
       op.get(ModelDescriptionConstants.OP).set(ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION);
       op.get(ModelDescriptionConstants.OP_ADDR).setEmptyList();
       op.get(ModelDescriptionConstants.CHILD_TYPE).set("server-group");
       
       dispatcher.execute(new DMRAction(op), new AsyncCallback<DMRResponse>() {
           @Override
           public void onFailure(Throwable caught) {
               callback.onFailure(caught);
           }

           @Override
           public void onSuccess(DMRResponse result) {
               ModelNode response = result.get();
               if(ModelAdapter.wasSuccess(response))
               {
                   List<ModelNode> payload = response.get("result").asList();

                   List<String> records = new ArrayList<String>(payload.size());
                   for(ModelNode binding : payload)
                   {
                       records.add(binding.asString());
                   }

                   callback.onSuccess(records);
               }
               else
               {
                   callback.onFailure(new RuntimeException("Failed to load socket binding groups"));
               }
           }
       });
   }
   
   private void getSocketBindingGroupForServerGroup(final String groupName, final AsyncCallback<String[]> callback) {
           ModelNode op = new ModelNode();
           op.get(ModelDescriptionConstants.OP).set(ModelDescriptionConstants.READ_RESOURCE_OPERATION);
           op.get(ModelDescriptionConstants.ADDRESS).add("server-group", groupName);
           op.get(RECURSIVE).set(true);
           op.get(INCLUDE_RUNTIME).set(true);

           dispatcher.execute(new DMRAction(op), new AsyncCallback<DMRResponse>() {
               @Override
               public void onFailure(Throwable caught) {
                   callback.onFailure(caught);
               }

               @Override
               public void onSuccess(DMRResponse result) {
                   ModelNode response = result.get();
                   if(ModelAdapter.wasSuccess(response))
                   {
                       List<ModelNode> payload = response.get("result").asList();

                       List<String> records = new ArrayList<String>(payload.size());
                       String profile = null;
                       String sbgRef = null;
                       for(ModelNode binding : payload)
                       {
                           if(binding.asString().contains("\"profile\"")){
                        	   profile = sanitizeProperty(binding.asString());
                           }
                           if(binding.asString().contains("\"socket-binding-group\"")){
                        	   sbgRef = sanitizeProperty(binding.asString());
                           }
                           records.add(binding.asString());
                       }
                       
                       callback.onSuccess(new String[]{profile,sbgRef});
                   }
                   else
                   {
                       callback.onFailure(new RuntimeException("Failed load server config " + groupName));
                   }

               }
           });
   }
   
   public void loadSocketBindingGroupForSelectedProfile(final AsyncCallback<List<String>> callback) {
	   final String selectedProfile = Console.MODULES.getCurrentSelectedProfile().getName();
	   if(selectedProfile != null){
	       this.loadServerGroupNames(new SimpleCallback<List<String>>() {
	           @Override
	           public void onSuccess(final List<String> groupNames) {
	        	   sgbBind = new HashMap<String,String>(); 
	               for(final String groupName : groupNames){
	        	       getSocketBindingGroupForServerGroup(groupName, new SimpleCallback<String[]>() {
	        	           @Override
	        	           public void onSuccess(String[] result) {
	        	        	   sgbBind.put(result[0], result[1]);
	        	        	   if(sync(groupNames.size())){
	        	        		   loadSocketBindingGroupForName(finalSGB,new SimpleCallback<List<String>>() {

									@Override
									public void onSuccess(List<String> result) {
										callback.onSuccess(result);
									}
	        	        			   
	        	        		   });
	        	        	   }
	        	           }
	        	           @Override
	        		        public void onFailure(Throwable caught) {
	        		        	super.onFailure(caught);
	        		        	// increase syncCounter in case of Failure to prevent to show empty list of sockets, but rather show standard-sockets one
	        		        	syncCounter++;
	        		        }
	        	       });
	               }
	           }
	       });
	   }
	   else{
		   loadSocketBindingGroupForName(finalSGB,new SimpleCallback<List<String>>() {

				@Override
				public void onSuccess(List<String> result) {
					callback.onSuccess(result);
				}
   			   
   		   });
	   }
   }
   
   private String sanitizeProperty(String value){
	   String prop = value;
	   String propSubstr = prop.substring(prop.indexOf("=>"),prop.length());
	   int beginIndex = propSubstr.indexOf("\"");
	   int endIndex = propSubstr.lastIndexOf("\"");
	   return propSubstr.substring(beginIndex + 1, endIndex);
	   
   }
   
   private boolean sync(int maxCounts){
	   this.syncCounter++;
	   if(this.syncCounter == maxCounts){
    	   final String selectedProfile = Console.MODULES.getCurrentSelectedProfile().getName();
    	   if(this.sgbBind.containsKey(selectedProfile)) {
    		   this.finalSGB = this.sgbBind.get(selectedProfile);
    	   }
    	   else{
    		   this.finalSGB = "standard-sockets";
    	   }
    	   this.syncCounter = 0;
    	   this.sgbBind = new HashMap<String,String>();
    	   return true;
	   }
	   return false;
   }
}
