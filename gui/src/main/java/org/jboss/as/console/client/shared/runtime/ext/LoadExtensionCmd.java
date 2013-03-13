package org.jboss.as.console.client.shared.runtime.ext;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.settings.ModelVersions;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.dmr.client.dispatch.AsyncCommand;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 12/7/12
 */
public class LoadExtensionCmd implements AsyncCommand<List<Extension>>{

    private DispatchAsync dispatcher;
    private BeanFactory factory;

    public LoadExtensionCmd(DispatchAsync dispatcher, BeanFactory factory) {
        this.dispatcher = dispatcher;
        this.factory = factory;
    }

    @Override
    public void execute(final AsyncCallback<List<Extension>> callback) {

        ModelNode fetchExtensions = new ModelNode();
        fetchExtensions.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        fetchExtensions.get(ADDRESS).setEmptyList();
        fetchExtensions.get(CHILD_TYPE).set("extension");
        fetchExtensions.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(fetchExtensions), new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result)
            {
                ModelNode response = result.get();

                List<Property> properties = response.get(RESULT).asPropertyList();
                List<Extension> extensions = new ArrayList<Extension>(properties.size());

                for (Property property : properties)
                {
                    ModelNode model = property.getValue().asObject();
                    List<Property> subsystems = model.get("subsystem").asPropertyList();

                    for(Property subsys : subsystems)
                    {
                        Extension extensionBean = factory.extension().as();
                        extensionBean.setName(property.getName()+"#"+subsys.getName());
                        extensionBean.setModule(model.get("module").asString());

                        extensionBean.setSubsystem(subsys.getName());

                        String major = subsys.getValue().get("management-major-version").asString();
                        String minor = subsys.getValue().get("management-minor-version").asString();
                        String micro = subsys.getValue().hasDefined("management-micro-version") ?
                                    subsys.getValue().get("management-micro-version").asString() : "0";

                        extensionBean.setVersion(major+"."+minor+"."+micro);

                        // compatibility checks
                        ModelVersions modelVersions = Console.MODULES.modelVersions();
                        String subsystemCompatVersion = modelVersions.get(subsys.getName());
                        String compatVersion = subsystemCompatVersion !=null ? subsystemCompatVersion : "no-set";
                        extensionBean.setCompatibleVersion(compatVersion);
                        extensions.add(extensionBean);
                    }
                }

                Collections.sort(extensions, new Comparator<Extension>() {
                    @Override
                    public int compare(Extension extension, Extension extension2) {
                        return extension.getSubsystem().compareTo(extension2.getSubsystem());
                    }
                });
                callback.onSuccess(extensions);
            }
        });
    }

    public void dumpVersions(final AsyncCallback<String> callback) {

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).setEmptyList();

        operation.get(OP).set(COMPOSITE);

        List<ModelNode> steps = new ArrayList<ModelNode>();

        ModelNode major = new ModelNode();
        major.get(OP).set(READ_ATTRIBUTE_OPERATION);
        major.get(ADDRESS).setEmptyList();
        major.get(NAME).set("management-major-version");

        ModelNode minor = new ModelNode();
        minor.get(OP).set(READ_ATTRIBUTE_OPERATION);
        minor.get(ADDRESS).setEmptyList();
        minor.get(NAME).set("management-minor-version");

        ModelNode micro = new ModelNode();
        micro.get(OP).set(READ_ATTRIBUTE_OPERATION);
        micro.get(ADDRESS).setEmptyList();
        micro.get(NAME).set("management-micro-version");

        steps.add(major);
        steps.add(minor);
        steps.add(micro);

        operation.get(STEPS).set(steps);


        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();

                ModelNode wrapper = response.get(RESULT);

                int majorVersion = wrapper.get("step-1").get(RESULT).asInt();
                int minorVersion = wrapper.get("step-2").get(RESULT).asInt();
                int microVersion = wrapper.get("step-3").get(RESULT).asInt();

                final String coreVersion = majorVersion+"."+minorVersion+"."+microVersion;

                System.out.println("Core Management version:"+coreVersion);

                LoadExtensionCmd.this.execute(new SimpleCallback<List<Extension>>() {
                    @Override
                    public void onSuccess(List<Extension> extensions) {

                        StringBuilder sb = new StringBuilder();
                        sb.append("{").append("\n");
                        sb.append("\t\"created\":\"").append(new Date(System.currentTimeMillis())).append("\",").append("\n");
                        sb.append("\t\"core-version\":\"").append(coreVersion).append("\",").append("\n");

                        int i = 0;
                        for (Extension ext : extensions) {
                            sb.append("\t\"").append(ext.getSubsystem()).append("\"").append(": ");
                            sb.append("\"").append(ext.getVersion()).append("\"");
                            if (i < extensions.size() - 1)
                                sb.append(",");
                            sb.append("\n");
                            i++;
                        }

                        sb.append("}").append("\n");

                        callback.onSuccess(sb.toString());
                    }
                });
            }
        });
    }
}
