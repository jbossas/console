package org.jboss.as.console.client.domain.deployment;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.groups.ServerGroupMgmtPresenter;
import org.jboss.as.console.client.domain.model.EntityFilter;
import org.jboss.as.console.client.domain.model.Predicate;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.shared.DeploymentRecord;
import org.jboss.as.console.client.shared.DeploymentStore;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/1/11
 */
public class DeploymentsPresenter extends Presenter<DeploymentsPresenter.MyView, DeploymentsPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DeploymentStore deploymentStore;
    private ServerGroupStore serverGroupStore;

    private String groupFilter = "";
    private String typeFilter= "";

    private EntityFilter<DeploymentRecord> filter = new EntityFilter<DeploymentRecord>();

    @ProxyCodeSplit
    @NameToken(NameTokens.DeploymentsPresenter)
    public interface MyProxy extends Proxy<DeploymentsPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(DeploymentsPresenter presenter);
        void updateDeployments(List<DeploymentRecord> deploymentRecords);
        void updateGroups(List<ServerGroupRecord> serverGroupRecords);
    }

    @Inject
    public DeploymentsPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            DeploymentStore deploymentStore,
            ServerGroupStore serverGroupStore) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.deploymentStore = deploymentStore;
        this.serverGroupStore = serverGroupStore;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        getView().updateDeployments(deploymentStore.loadDeployments());
        getView().updateGroups(serverGroupStore.loadServerGroups());
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ServerGroupMgmtPresenter.TYPE_MainContent, this);
    }

    public void deleteDeployment(DeploymentRecord deploymentRecord) {
        deploymentStore.deleteDeployment(deploymentRecord);
    }


    public void onFilterGroup(final String groupName) {
        this.groupFilter = groupName;
        getView().updateDeployments(
                filter.apply(new TypeAndGroupPredicate(), deploymentStore.loadDeployments())
        );
    }

    public void onFilterType(final String type) {

        this.typeFilter = type;
        getView().updateDeployments(
                filter.apply(new TypeAndGroupPredicate(), deploymentStore.loadDeployments())
        );
    }

    class TypeAndGroupPredicate implements Predicate<DeploymentRecord>
    {
        @Override
        public boolean appliesTo(DeploymentRecord candidate) {


            boolean groupMatch = groupFilter.equals("") ?
                    true : candidate.getServerGroup().equals(groupFilter);

            boolean typeMatch = typeFilter.equals("") ?
                    true : candidate.getName().endsWith(typeFilter);

            return groupMatch && typeMatch;
        }
    }

}
