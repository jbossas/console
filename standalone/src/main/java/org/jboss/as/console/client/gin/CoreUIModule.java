package org.jboss.as.console.client.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.DefaultProxyFailureHandler;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.*;
import org.jboss.as.console.client.auth.CurrentUser;
import org.jboss.as.console.client.auth.LoggedInGatekeeper;
import org.jboss.as.console.client.auth.SignInPagePresenter;
import org.jboss.as.console.client.auth.SignInPageView;
import org.jboss.as.console.client.domain.DomainMgmtApplicationPresenter;
import org.jboss.as.console.client.domain.DomainMgmtApplicationViewImpl;
import org.jboss.as.console.client.domain.groups.ServerGroupsPresenter;
import org.jboss.as.console.client.domain.groups.ServerGroupsView;
import org.jboss.as.console.client.domain.model.*;
import org.jboss.as.console.client.domain.profiles.ProfileOverview;
import org.jboss.as.console.client.domain.profiles.ProfileOverviewPresenter;
import org.jboss.as.console.client.server.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.server.ServerMgmtApplicationViewImpl;
import org.jboss.as.console.client.shared.DeploymentStore;
import org.jboss.as.console.client.server.deployments.DeploymentToolPresenter;
import org.jboss.as.console.client.server.deployments.DeploymentToolViewImpl;
import org.jboss.as.console.client.shared.MockDeploymentStoreImpl;
import org.jboss.as.console.client.server.interfaces.InterfaceToolPresenter;
import org.jboss.as.console.client.server.interfaces.InterfaceToolViewImpl;
import org.jboss.as.console.client.server.path.PathToolPresenter;
import org.jboss.as.console.client.server.path.PathToolViewImpl;
import org.jboss.as.console.client.server.properties.PropertyToolPresenter;
import org.jboss.as.console.client.server.properties.PropertyToolViewImpl;
import org.jboss.as.console.client.server.sockets.SocketToolPresenter;
import org.jboss.as.console.client.server.sockets.SocketToolViewImpl;
import org.jboss.as.console.client.server.subsys.threads.ThreadManagementPresenter;
import org.jboss.as.console.client.server.subsys.threads.ThreadManagementViewImpl;
import org.jboss.as.console.client.shared.MockSubsystemStore;
import org.jboss.as.console.client.shared.SubsystemStore;
import org.jboss.as.console.client.system.SystemApplicationPresenter;
import org.jboss.as.console.client.system.SystemApplicationViewImpl;
import org.jboss.as.console.client.util.message.MessageBar;
import org.jboss.as.console.client.util.message.MessageCenter;
import org.jboss.as.console.client.util.message.MessageCenterView;

/**
 * Provides the bindings for the core UI components.
 *
 * @author Heiko Braun
 * @date 1/31/11
 */
public class CoreUIModule extends AbstractPresenterModule {

    protected void configure() {

        // main layout
        bind(Header.class).in(Singleton.class);
        bind(Footer.class).in(Singleton.class);

        // supporting components
        bind(MessageBar.class).in(Singleton.class);
        bind(MessageCenter.class).in(Singleton.class);
        bind(MessageCenterView.class).in(Singleton.class);

        // ----------------------------------------------------------------------

        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(PlaceManager.class).to(DefaultPlaceManager.class).in(Singleton.class);
        bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(Singleton.class);
        bind(RootPresenter.class).asEagerSingleton();
        bind(ProxyFailureHandler.class).to(DefaultProxyFailureHandler.class).in(Singleton.class);
        bind(Gatekeeper.class).to(LoggedInGatekeeper.class);
        bind(CurrentUser.class).in(Singleton.class);
        bind(BootstrapContext.class).in(Singleton.class);

        // sign in
        bindPresenter(SignInPagePresenter.class, SignInPagePresenter.MyView.class,
                SignInPageView.class, SignInPagePresenter.MyProxy.class);

        // main layout
        bindPresenter(MainLayoutPresenter.class,
                MainLayoutPresenter.MainLayoutView.class,
                MainLayoutViewImpl.class,
                MainLayoutPresenter.MainLayoutProxy.class);

        // ----------------------------------------------------------------------

        // system application
        bindPresenter(SystemApplicationPresenter.class,
                SystemApplicationPresenter.SystemAppView.class,
                SystemApplicationViewImpl.class,
                SystemApplicationPresenter.SystemAppProxy.class);

        // ----------------------------------------------------------------------

        // server management application
        bindPresenter(ServerMgmtApplicationPresenter.class,
                ServerMgmtApplicationPresenter.ServerManagementView.class,
                ServerMgmtApplicationViewImpl.class,
                ServerMgmtApplicationPresenter.ServerManagementProxy.class);

        // server deployments
        bindPresenter(DeploymentToolPresenter.class,
                DeploymentToolPresenter.DeploymentToolView.class,
                DeploymentToolViewImpl.class,
                DeploymentToolPresenter.DeploymentToolProxy.class);
        bind(DeploymentStore.class).to(MockDeploymentStoreImpl.class).in(Singleton.class);

        // server/interfaces
        bindPresenter(InterfaceToolPresenter.class,
                InterfaceToolPresenter.MyView.class,
                InterfaceToolViewImpl.class,
                InterfaceToolPresenter.MyProxy.class);
        
        // server/path
        bindPresenter(PathToolPresenter.class,
                PathToolPresenter.MyView.class,
                PathToolViewImpl.class,
                PathToolPresenter.MyProxy.class);
        
        // server/properties
        bindPresenter(PropertyToolPresenter.class,
                PropertyToolPresenter.MyView.class,
                PropertyToolViewImpl.class,
                PropertyToolPresenter.MyProxy.class);
        
        // server/sockets
        bindPresenter(SocketToolPresenter.class,
                SocketToolPresenter.MyView.class,
                SocketToolViewImpl.class,
                SocketToolPresenter.MyProxy.class);
        
        // server/threads
        bindPresenter(ThreadManagementPresenter.class,
                ThreadManagementPresenter.MyView.class,
                ThreadManagementViewImpl.class,
                ThreadManagementPresenter.MyProxy.class);
                
        
        // ------------------------------------------------
        // domain below

        // domain management application
        bindPresenter(DomainMgmtApplicationPresenter.class,
                DomainMgmtApplicationPresenter.MyView.class,
                DomainMgmtApplicationViewImpl.class,
                DomainMgmtApplicationPresenter.MyProxy.class);
        
        // domain/profiles
        bindPresenter(ProfileOverviewPresenter.class,
                ProfileOverviewPresenter.MyView.class,
                ProfileOverview.class,
                ProfileOverviewPresenter.MyProxy.class);
        
        bindPresenter(ServerGroupsPresenter.class,
                ServerGroupsPresenter.MyView.class,
                ServerGroupsView.class,
                ServerGroupsPresenter.MyProxy.class);
        
        bind(ProfileStore.class).to(MockProfileStore.class).in(Singleton.class);
        bind(SubsystemStore.class).to(MockSubsystemStore.class).in(Singleton.class);
        bind(ServerGroupStore.class).to(MockServerGroupStore.class).in(Singleton.class);
                
        
    }

}
