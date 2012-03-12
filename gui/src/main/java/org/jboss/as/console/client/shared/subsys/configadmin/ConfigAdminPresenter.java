package org.jboss.as.console.client.shared.subsys.configadmin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.dispatch.impl.SimpleDMRResponseHandler;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.configadmin.model.ConfigAdminData;
import org.jboss.as.console.client.shared.subsys.configadmin.wizard.NewConfigAdminDataWizard;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

public class ConfigAdminPresenter extends Presenter<ConfigAdminPresenter.MyView, ConfigAdminPresenter.MyProxy> {
    public static final String CONFIG_ADMIN_SUBSYSTEM = "configadmin";

    private final DispatchAsync dispatcher;
    private final BeanFactory factory;
    private final RevealStrategy revealStrategy;
    private DefaultWindow window;

    @ProxyCodeSplit
    @NameToken(NameTokens.ConfigAdminPresenter)
    public interface MyProxy extends Proxy<ConfigAdminPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(ConfigAdminPresenter configAdminPresenter);
        void updateConfigurationAdmin(List<ConfigAdminData> casDataList, String selectPid);
    }

    @Inject
    public ConfigAdminPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory, RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.factory = factory;
        this.revealStrategy = revealStrategy;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        loadConfigAdminDetails(null);
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    private void loadConfigAdminDetails(final String selectPid) {
        ModelNode operation = createOperation(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("configuration");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                ModelNode model = response.get(RESULT);

                List<ConfigAdminData> casDataList = new ArrayList<ConfigAdminData>();
                for (String pid : model.keys()) {
                    ConfigAdminData data = factory.configAdminData().as();
                    data.setPid(pid);

                    List<PropertyRecord> properties = new ArrayList<PropertyRecord>();
                    for(Property property : model.get(pid).get("entries").asPropertyList()) {
                        PropertyRecord record = factory.property().as();
                        record.setKey(property.getName());
                        record.setValue(property.getValue().asString());
                        properties.add(record);
                    }
                    data.setProperties(properties);
                    casDataList.add(data);
                }
                getView().updateConfigurationAdmin(casDataList, selectPid);
            }
        });
    }

    public void launchNewCASPropertyWizard() {
        window = new DefaultWindow(Console.CONSTANTS.subsys_configadmin_add());
        window.setWidth(480);
        window.setHeight(360);
        window.trapWidget(new NewConfigAdminDataWizard(this).asWidget());
        window.setGlassEnabled(true);
        window.center();
    }

    public void closeDialogue() {
        if (window != null)
            window.hide();
    }

    public void onAddConfigurationAdminData(final ConfigAdminData data) {
        closeDialogue();

        ModelNode operation = createOperation(ADD);
        operation.get(ADDRESS).add("configuration", data.getPid());
        ModelNode modelData = operation.get("entries");
        for (PropertyRecord record : data.getProperties()) {
            modelData.get(record.getKey()).set(record.getValue());
        }

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(ADD, Console.CONSTANTS.subsys_configadmin_PID(), data.getPid(),
                new Command() {
                    @Override
                    public void execute() {
                        loadConfigAdminDetails(data.getPid());
                    }
                }));
    }

    public void onDeleteConfigurationAdminData(final String pid) {
        ModelNode operation = createOperation(REMOVE);
        operation.get(ADDRESS).add("configuration", pid);

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(REMOVE, Console.CONSTANTS.subsys_configadmin_PID(), pid,
                new Command() {
                    @Override
                    public void execute() {
                        loadConfigAdminDetails(null);
                    }
                }));
    }

    public void onUpdateConfigurationAdminData(final ConfigAdminData data) {
        ModelNode operation = createOperation(REMOVE);
        operation.get(ADDRESS).add("configuration", data.getPid());

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(REMOVE, Console.CONSTANTS.subsys_configadmin_PID(), data.getPid(),
                new Command() {
                    @Override
                    public void execute() {
                        onAddConfigurationAdminData(data);
                    }
                }));
    }

    private ModelNode createOperation(String operator) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(operator);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add(SUBSYSTEM, CONFIG_ADMIN_SUBSYSTEM);
        return operation;
    }
}
