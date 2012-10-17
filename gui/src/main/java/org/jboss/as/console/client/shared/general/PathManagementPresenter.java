package org.jboss.as.console.client.shared.general;

import com.google.web.bindery.event.shared.EventBus;
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
import org.jboss.as.console.client.shared.general.model.Path;
import org.jboss.as.console.client.shared.general.wizard.NewPathWizard;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 10/15/12
 */
public class PathManagementPresenter extends Presenter<PathManagementPresenter.MyView, PathManagementPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private RevealStrategy revealStrategy;
    private DefaultWindow window;
    private ApplicationMetaData metaData;
    private EntityAdapter<Path> entityAdapter;

    @ProxyCodeSplit
    @NameToken(NameTokens.PathManagementPresenter)
    public interface MyProxy extends Proxy<PathManagementPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(PathManagementPresenter presenter);

        void setPaths(List<Path> paths);
    }

    @Inject
    public PathManagementPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory, RevealStrategy revealStrategy,
            ApplicationMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.revealStrategy = revealStrategy;
        this.metaData = propertyMetaData;

        this.entityAdapter = new EntityAdapter<Path>(Path.class, metaData);

    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();

        loadPathInformation();
    }

    private void loadPathInformation() {
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).setEmptyList();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("path");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.failed("Paths"), response.getFailureDescription());
                }
                else
                {
                    List<ModelNode> payload = response.get("result").asList();

                    List<Path> paths = new ArrayList<Path>();
                    for (ModelNode item : payload) {
                        paths.add(entityAdapter.fromDMR(item));
                    }

                    getView().setPaths(paths);
                }
            }
        });
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void launchNewPathDialogue() {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Path"));
        window.setWidth(480);
        window.setHeight(360);

        window.trapWidget(
                new NewPathWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void onDeletePath(final Path path) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).add("path", path.getName());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(ModelAdapter.wasSuccess(response))
                    Console.info(Console.MESSAGES.deleted("Path " + path.getName()));
                else
                    Console.error(Console.MESSAGES.deletionFailed("Path " + path.getName()), response.getFailureDescription());

                loadPathInformation();
            }
        });
    }

    public void onSavePath(final String name, Map<String, Object> changedValues) {
        ModelNode address = new ModelNode();
        address.add("path", name);

        ModelNode addressNode = new ModelNode();
        addressNode.get(ADDRESS).set(address);

        ModelNode operation = entityAdapter.fromChangeset(changedValues, addressNode);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                    Console.error(Console.MESSAGES.modificationFailed("Path " + name), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.modified("Path " + name));

                loadPathInformation();
            }
        });
    }

    public void onCloseDialoge() {
        window.hide();
    }

    public void onCreatePath(final Path path) {
        onCloseDialoge();

        ModelNode operation = entityAdapter.fromEntity(path);
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).add("path", path.getName());

        // TODO: workaround ....
        if(null==path.getRelativeTo() || path.getRelativeTo().equals(""))
            operation.remove("relative-to");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(ModelAdapter.wasSuccess(response))
                    Console.info(Console.MESSAGES.added("Path "+path.getName()));
                else
                    Console.error(Console.MESSAGES.addingFailed("Path " + path.getName()), response.getFailureDescription());

                loadPathInformation();
            }
        });
    }
}
