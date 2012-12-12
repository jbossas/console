package org.jboss.as.console.client.shared.runtime.ext;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.settings.ModelVersions;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
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
                    Extension extensionBean = factory.extension().as();
                    extensionBean.setName(property.getName());
                    extensionBean.setModule(model.get("module").asString());

                    Property subsystem = model.get("subsystem").asPropertyList().get(0);
                    extensionBean.setSubsystem(subsystem.getName());

                    String major = subsystem.getValue().get("management-major-version").asString();
                    String minor = subsystem.getValue().get("management-minor-version").asString();
                    String micro = subsystem.getValue().hasDefined("management-micro-version") ?
                            subsystem.getValue().get("management-micro-version").asString() : "0";

                    extensionBean.setVersion(major+"."+minor+"."+micro);

                    // compatibility checks
                    ModelVersions modelVersions = Console.MODULES.modelVersions();
                    String subsystemCompatVersion = modelVersions.get(subsystem.getName());
                    String compatVersion = subsystemCompatVersion !=null ? subsystemCompatVersion : "no-set";
                    extensionBean.setCompatibleVersion(compatVersion);
                    extensions.add(extensionBean);
                }

                callback.onSuccess(extensions);
            }
        });
    }
}
