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

package org.jboss.as.console.client.domain.model.impl;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerFlag;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 3/18/11
 */
public class HostInfoStoreImpl implements HostInformationStore {

    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private ApplicationMetaData propertyMetaData;
    private EntityAdapter<Server> serverAdapter;
    private EntityAdapter<Jvm> jvmAdapter;
    private EntityAdapter<PropertyRecord> propertyAdapter;

    @Inject
    public HostInfoStoreImpl(DispatchAsync dispatcher, BeanFactory factory, ApplicationMetaData propertyMeta) {
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.propertyMetaData = propertyMeta;
        serverAdapter = new EntityAdapter<Server>(Server.class, propertyMeta);
        jvmAdapter = new EntityAdapter<Jvm>(Jvm.class, propertyMeta);
        propertyAdapter = new EntityAdapter<PropertyRecord>(PropertyRecord.class, propertyMeta);
    }

    @Override
    public void getHosts(final AsyncCallback<List<Host>> callback) {
        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("host");
        operation.get(ADDRESS).setEmptyList();

        dispatcher.execute(new DMRAction(operation, false), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                List<Property> hostModels = response.get("result").asPropertyList();

                List<Host> records = new LinkedList<Host>();
                for(Property hostModel : hostModels)
                {
                    Host record = factory.host().as();
                    record.setName(hostModel.getName());

                    // controller
                    ModelNode hostValues = hostModel.getValue();
                    boolean isController = hostValues.get("domain-controller").hasDefined("local");
                    record.setController(isController);
                    records.add(record);
                }

                callback.onSuccess(records);
            }

        });
    }

    @Override
    public void getServerConfigurations(String host, final AsyncCallback<List<Server>> callback) {

        if(host==null) throw new RuntimeException("Host parameter is null!");

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        ModelNode coreModel = new ModelNode();
        coreModel.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        coreModel.get(INCLUDE_RUNTIME).set(true);
        coreModel.get(ADDRESS).add("host", host);
        coreModel.get(CHILD_TYPE).set("server-config");
        steps.add(coreModel);

        ModelNode groupModel = new ModelNode();
        groupModel.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        groupModel.get(ADDRESS).setEmptyList();
        groupModel.get(CHILD_TYPE).set("server-group");
        steps.add(groupModel);

        operation.get(STEPS).set(steps);

        dispatcher.execute(new DMRAction(operation, false), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();
                ModelNode overalResult = response.get(RESULT);

                if(overalResult.isFailure())
                {
                    callback.onFailure(new RuntimeException("Failed to load sever configurations: "+response.getFailureDescription()));
                }
                else
                {

                    List<Property> serverGroupsModel = overalResult.get("step-2").get(RESULT).asPropertyList();

                    Map<String,String> group2profile = new HashMap<String,String>();

                    for(Property group : serverGroupsModel)
                    {
                        group2profile.put(group.getName(), group.getValue().get("profile").asString());
                    }


                    List<ModelNode> serverConfigModel = overalResult.get("step-1").get(RESULT).asList();

                    List<Server> records = new LinkedList<Server>();
                    for(ModelNode item : serverConfigModel)
                    {
                        ModelNode model = item.asProperty().getValue();
                        Server server = serverAdapter.fromDMR(model);
                        server.setStarted(model.get("status").asString().equals("STARTED"));
                        server.setProfile(group2profile.get(server.getGroup()));
                        records.add(server);
                    }

                    // group profiles

                    callback.onSuccess(records);

                }


            }

        });
    }

    @Override
    public void getServerConfiguration(String host, String server, final AsyncCallback<Server> callback) {

        if(host==null) throw new RuntimeException("Host parameter is null!");

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(ADDRESS).setEmptyList();
        operation.get(ADDRESS).add("host", host);
        operation.get(ADDRESS).add("server-config", server);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation, false), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();
                ModelNode model = response.get("result").asObject();

                Server server = serverAdapter.fromDMR(model);
                server.setStarted(model.get("status").asString().equals("STARTED"));

                callback.onSuccess(server);
            }

        });
    }

    public void getVirtualMachines(String host, final AsyncCallback<List<String>> callback) {
        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        operation.get(CHILD_TYPE).set("jvm");
        operation.get(ADDRESS).setEmptyList();
        operation.get(ADDRESS).add("host", host);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();
                List<ModelNode> payload = response.get("result").asList();

                List<String> records = new ArrayList<String>(payload.size());

                for(ModelNode jvm : payload)
                    records.add(jvm.asString());

                callback.onSuccess(records);
            }

        });
    }



    @Override
    public void getServerInstances(final String host, final AsyncCallback<List<ServerInstance>> callbackReference) {


        final Command cmd = new Command() {

            private int numRequests = 0;
            private int numResponses = 0;
            private final AsyncCallback<List<ServerInstance>> cb = callbackReference;
            private final String id = HTMLPanel.createUniqueId();

            @Override
            public void execute() {
                final List<ServerInstance> instanceList = new LinkedList<ServerInstance>();

                //System.out.println("*** ["+id+"] attempt to fetch server instances *** ");

                getServerConfigurations(host, new SimpleCallback<List<Server>>() {
                    @Override
                    public void onSuccess(final List<Server> serverConfigs) {


                        if(serverConfigs.isEmpty())
                        {
                            callbackReference.onSuccess(Collections.EMPTY_LIST);
                            return;
                        }

                        for(final Server handle : serverConfigs)
                        {

                            ModelNode operation = new ModelNode();
                            operation.get(OP).set(COMPOSITE);
                            operation.get(ADDRESS).setEmptyList();

                            List<ModelNode> steps = new ArrayList<ModelNode>();

                            final ModelNode coreData = new ModelNode();
                            coreData.get(OP).set(READ_RESOURCE_OPERATION);
                            coreData.get(INCLUDE_RUNTIME).set(true);
                            coreData.get(ADDRESS).setEmptyList();
                            coreData.get(ADDRESS).add("host", host);
                            coreData.get(ADDRESS).add("server", handle.getName());
                            steps.add(coreData);

                            final ModelNode interfaces = new ModelNode();
                            interfaces.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
                            interfaces.get(INCLUDE_RUNTIME).set(true);
                            interfaces.get(ADDRESS).add("host", host);
                            interfaces.get(ADDRESS).add("server", handle.getName());
                            interfaces.get(CHILD_TYPE).set("interface");
                            steps.add(interfaces);


                            final ModelNode socketBinding = new ModelNode();
                            socketBinding.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
                            socketBinding.get(INCLUDE_RUNTIME).set(true);
                            socketBinding.get(ADDRESS).add("host", host);
                            socketBinding.get(ADDRESS).add("server", handle.getName());
                            socketBinding.get(CHILD_TYPE).set("socket-binding-group");
                            steps.add(socketBinding);

                           /* final ModelNode groupProfile = new ModelNode();
                            groupProfile.get(OP).set(READ_RESOURCE_OPERATION);
                            groupProfile.get(ADDRESS).add("server-group", handle.getGroup());
                            steps.add(groupProfile);*/

                            operation.get(STEPS).set(steps);

                            numRequests++;

                            dispatcher.execute(new DMRAction(operation, false), new SimpleCallback<DMRResponse>() {


                                @Override
                                public void onFailure(Throwable caught) {
                                    numResponses++;

                                    ServerInstance instance = createInstanceModel(handle);
                                    instance.setHost(host);
                                    instance.setRunning(false);
                                    instance.setInterfaces(new HashMap<String, String>());
                                    instance.setSocketBindings(new HashMap<String, String>());
                                    instanceList.add(instance);

                                    checkComplete(instanceList, cb);
                                }

                                @Override
                                public void onSuccess(DMRResponse result) {

                                    numResponses++;

                                    ModelNode response = result.get();
                                    ModelNode compositeResponse = response.get(RESULT);

                                    ServerInstance instance = createInstanceModel(handle);
                                    instance.setInterfaces(new HashMap<String, String>());
                                    instance.setSocketBindings(new HashMap<String, String>());
                                    instanceList.add(instance);

                                    if(response.isFailure())
                                    {
                                        instance.setRunning(false);
                                    }
                                    else
                                    {

                                        ModelNode instanceModel = compositeResponse.get("step-1").get(RESULT);
                                        instance.setRunning(handle.isStarted());

                                        //instance.setProfile(instanceModel.get("profile-name").asString());

                                        if(instanceModel.hasDefined("server-state"))
                                        {
                                            String state = instanceModel.get("server-state").asString();
                                            if(state.equals("reload-required"))
                                            {
                                                instance.setFlag(ServerFlag.RELOAD_REQUIRED);
                                            }
                                            else if (state.equals("restart-required"))
                                            {
                                                instance.setFlag(ServerFlag.RESTART_REQUIRED);
                                            }
                                        }

                                        // ---- interfaces
                                        List<Property> interfaces = compositeResponse.get("step-2").get(RESULT).asPropertyList();

                                        for(Property intf : interfaces)
                                        {
                                            if(intf.getValue().hasDefined("resolved-address"))
                                            {
                                                instance.getInterfaces().put(
                                                        intf.getName(),
                                                        intf.getValue().get("resolved-address").asString()
                                                );
                                            }
                                        }


                                        // ---- socket binding
                                        List<Property> sockets = compositeResponse.get("step-3").get(RESULT).asPropertyList();

                                        for(Property socket : sockets)
                                        {
                                            instance.getSocketBindings().put(
                                                    socket.getName(),
                                                    socket.getValue().get("port-offset").asString()
                                            );

                                        }
                                    }

                                    checkComplete(instanceList, cb);
                                }
                            });

                        }
                    }
                });
            }

            private void checkComplete(List<ServerInstance> instanceList, AsyncCallback<List<ServerInstance>> callback) {
                if(numRequests==numResponses)
                {

                    Collections.sort(instanceList, new Comparator<ServerInstance>() {
                        @Override
                        public int compare(ServerInstance a, ServerInstance b) {
                            return a.getName().compareTo(b.getName());
                        }
                    });


                    //System.out.println("*** ["+id+"] complete roundtrips  *** ");

                    callback.onSuccess(instanceList);
                }
            }
        };


        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                cmd.execute();
            }
        });



        /* ModelNode operation = new ModelNode();
     operation.get(ADDRESS).setEmptyList();
     operation.get(OP).set(COMPOSITE);

     List<ModelNode> steps = new ArrayList<ModelNode>();

     // ---  fetch configs

     final ModelNode configOp = new ModelNode();
     configOp.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
     configOp.get(CHILD_TYPE).set("server-config");
     configOp.get(ADDRESS).setEmptyList();
     configOp.get(ADDRESS).add("host", host);
     configOp.get(INCLUDE_RUNTIME).set(true);


     steps.add(configOp);

     // ---  fetch instance state

     final ModelNode instanceOp = new ModelNode();
     instanceOp.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
     instanceOp.get(CHILD_TYPE).set("server");
     instanceOp.get(ADDRESS).setEmptyList();
     instanceOp.get(ADDRESS).add("host", host);
     instanceOp.get(INCLUDE_RUNTIME).set(true);

     steps.add(instanceOp);

     operation.get(STEPS).set(steps);

     System.out.println(operation);

     dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

         @Override
         public void onSuccess(DMRResponse result) {

             ModelNode response = result.get();

             System.out.println(response);
         }

     });   */

    }


    public void updateServerInstance(String host, final Server handle, final AsyncCallback<ServerInstance> callback) {


        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);
        operation.get(ADDRESS).setEmptyList();
        operation.get(ADDRESS).add("host", host);
        operation.get(ADDRESS).add("server", handle.getName());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {


            @Override
            public void onFailure(Throwable caught) {
                ServerInstance instance = createInstanceModel(handle);
                instance.setRunning(false);
                callback.onSuccess(instance);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode statusResponse = result.get();
                ModelNode payload = statusResponse.get(RESULT);

                ServerInstance instance = createInstanceModel(handle);

                if(statusResponse.isFailure())
                {
                    instance.setRunning(false);
                }
                else
                {
                    instance.setRunning(handle.isStarted());

                    if(payload.hasDefined("server-state"))
                    {
                        String state = payload.get("server-state").asString();
                        if(state.equals("reload-required"))
                        {
                            instance.setFlag(ServerFlag.RELOAD_REQUIRED);
                        }
                        else if (state.equals("restart-required"))
                        {
                            instance.setFlag(ServerFlag.RESTART_REQUIRED);
                        }
                    }

                }

                callback.onSuccess(instance);
            }
        });
    }

    private ServerInstance createInstanceModel(Server handle) {
        ServerInstance instance = factory.serverInstance().as();
        instance.setName(handle.getName());
        instance.setServer(handle.getName());
        instance.setGroup(handle.getGroup());
        instance.setProfile(handle.getProfile());
        return instance;
    }

    @Override
    public void startServer(final String host, final String configName, boolean startIt, final AsyncCallback<Boolean> callback) {
        final String actualOp = startIt ? "start" : "stop";

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(actualOp);
        operation.get(ADDRESS).add("host", host);
        operation.get(ADDRESS).add("server-config", configName);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(response.get("outcome").asString().equals("success"))
                {
                    callback.onSuccess(Boolean.TRUE);
                }
                else
                {
                    callback.onSuccess(Boolean.FALSE);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });

    }

    @Override
    public void reloadServer(String host, final String configName, final AsyncCallback<Boolean> callback) {
        final ModelNode operation = new ModelNode();
        operation.get(OP).set("reload");
        operation.get(ADDRESS).add("host", host);
        operation.get(ADDRESS).add("server", configName);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(response.get("outcome").asString().equals("success"))
                {
                    callback.onSuccess(Boolean.TRUE);
                }
                else
                {
                    callback.onSuccess(Boolean.FALSE);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onSuccess(Boolean.FALSE);
            }
        });

    }

    @Override
    public void createServerConfig(String host, Server record, final AsyncCallback<Boolean> callback) {
        final ModelNode serverConfig = new ModelNode();
        serverConfig.get(OP).set(ModelDescriptionConstants.ADD);
        serverConfig.get(ADDRESS).add("host", host);
        serverConfig.get(ADDRESS).add(ModelDescriptionConstants.SERVER_CONFIG, record.getName());

        serverConfig.get("name").set(record.getName());
        serverConfig.get("group").set(record.getGroup());
        serverConfig.get("auto-start").set(record.isAutoStart());

        // TODO: can be null?
        if(record.getJvm()!=null)
            serverConfig.get("jvm").set(record.getJvm().getName());

        serverConfig.get("socket-binding-group").set(record.getSocketBinding());
        serverConfig.get("socket-binding-port-offset").set(record.getPortOffset());


        //System.out.println(serverConfig.toJSONString());

        dispatcher.execute(new DMRAction(serverConfig), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {

                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                String outcome = response.get("outcome").asString();

                Boolean wasSuccessful = outcome.equals("success") ? Boolean.TRUE : Boolean.FALSE;
                callback.onSuccess(wasSuccessful);
            }
        });
    }

    @Override
    @Deprecated
    public void saveServerConfig(String host, String name, Map<String, Object> changedValues, final AsyncCallback<Boolean> callback) {
        ModelNode proto = new ModelNode();
        proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        proto.get(ADDRESS).add("host", host);
        proto.get(ADDRESS).add(ModelDescriptionConstants.SERVER_CONFIG, name);

        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(Server.class);
        ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                callback.onSuccess(response.get(OUTCOME).asString().equals(SUCCESS));
            }
        });
    }

    @Override
    public void deleteServerConfig(String host, Server record, final AsyncCallback<Boolean> callback) {
        final ModelNode serverConfig = new ModelNode();
        serverConfig.get(OP).set(ModelDescriptionConstants.REMOVE);
        serverConfig.get(ADDRESS).add("host", host);
        serverConfig.get(ADDRESS).add(ModelDescriptionConstants.SERVER_CONFIG, record.getName());


        dispatcher.execute(new DMRAction(serverConfig), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                Boolean wasSuccessful = !response.isFailure();
                callback.onSuccess(wasSuccessful);
            }
        });
    }

    @Override
    public void loadJVMConfiguration(String host, Server server, final AsyncCallback<Jvm> callback) {

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(ModelDescriptionConstants.READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).add("host", host);
        operation.get(ADDRESS).add(ModelDescriptionConstants.SERVER_CONFIG, server.getName());
        operation.get(CHILD_TYPE).set("jvm");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();

                if(result.isFailure())
                {
                    callback.onFailure(new Throwable("Failed to load jvms: "+result.getFailureDescription()));
                }
                else
                {


                    List<Property> jvms = result.get(RESULT).asPropertyList();
                    if(!jvms.isEmpty())
                    {
                        // select first entry
                        Property property = jvms.get(0);
                        Jvm jvm = jvmAdapter.fromDMR(property.getValue().asObject());
                        jvm.setName(property.getName());

                        callback.onSuccess(jvm);
                    }
                    else
                    {
                        callback.onSuccess(null);
                    }
                }
            }
        });

    }

    @Override
    public void loadProperties(String host, Server server, final AsyncCallback<List<PropertyRecord>> callback) {

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(ModelDescriptionConstants.READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).add("host", host);
        operation.get(ADDRESS).add(ModelDescriptionConstants.SERVER_CONFIG, server.getName());
        operation.get(CHILD_TYPE).set("system-property");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();


                if(result.isFailure())
                {
                    callback.onFailure(new Throwable("Failed to load server:"+result.getFailureDescription()));
                }
                else
                {
                    List<Property> properties = result.get(RESULT).asPropertyList();
                    List<PropertyRecord> records = new ArrayList<PropertyRecord>(properties.size());

                    for(Property prop : properties)
                    {
                        PropertyRecord record = factory.property().as();
                        record.setKey(prop.getName());
                        ModelNode payload = prop.getValue().asObject();
                        record.setValue(payload.get("value").asString());
                        record.setBootTime(payload.get("boot-time").asBoolean());

                        records.add(record);
                    }

                    callback.onSuccess(records);
                }
            }
        });
    }
}
