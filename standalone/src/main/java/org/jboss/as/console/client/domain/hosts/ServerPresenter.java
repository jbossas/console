package org.jboss.as.console.client.domain.hosts;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public class ServerPresenter extends Presenter<ServerPresenter.MyView, ServerPresenter.MyProxy>
    implements HostSelectionEvent.HostSelectionListener {

    private HostInformationStore hostInfoStore;

    private Server selectedRecord = null;
    private ServerGroupStore serverGroupStore;

    private boolean hasBeenRevealed = false;

    @ProxyCodeSplit
    @NameToken(NameTokens.ServerPresenter)
    public interface MyProxy extends Proxy<ServerPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(ServerPresenter presenter);
        void setSelectedRecord(Server selectedRecord);
        void updateServerGroups(List<ServerGroupRecord> serverGroupRecords);
    }

    @Inject
    public ServerPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            HostInformationStore hostInfoStore,
            ServerGroupStore serverGroupStore) {
        super(eventBus, view, proxy);

        this.hostInfoStore = hostInfoStore;
        this.serverGroupStore = serverGroupStore;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(HostSelectionEvent.TYPE, this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        String hostName = request.getParameter("host", null);
        String serverName = request.getParameter("server", null);
        String action= request.getParameter("action", null);

        if(hostName!=null && serverName!=null)
        {
            for(Server server : hostInfoStore.getServerConfigurations(hostName))
            {
                if(server.getName().equals(serverName))
                {
                    selectedRecord = server;
                    break;
                }
            }
        }
        else if("new".equals(action))
        {
            Window.alert("Not implemented yet.");
        }
        else
        {
            Log.warn("Parameters missing. Fallback to default Server");
            hostName = hostInfoStore.getHosts().get(0).getName();
            selectedRecord = hostInfoStore.getServerConfigurations(hostName).get(0);
        }

    }

    @Override
    protected void onReset() {
        super.onReset();
        getView().updateServerGroups(serverGroupStore.loadServerGroups());
        if(selectedRecord!=null)
            getView().setSelectedRecord(selectedRecord);

        hasBeenRevealed = true;
    }

    @Override
    protected void revealInParent() {
         RevealContentEvent.fire(getEventBus(), HostMgmtPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onHostSelection(String hostName) {

        // display first server config by default
        List<Server> serverConfigurations = hostInfoStore.getServerConfigurations(hostName);
        if(!serverConfigurations.isEmpty() && hasBeenRevealed) {
            selectedRecord = serverConfigurations.get(0);
            getView().setSelectedRecord(selectedRecord);
        }
    }


}
