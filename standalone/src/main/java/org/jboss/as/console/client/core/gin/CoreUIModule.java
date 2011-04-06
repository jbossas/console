package org.jboss.as.console.client.core.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.DefaultProxyFailureHandler;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyFailureHandler;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import org.jboss.as.console.client.auth.CurrentUser;
import org.jboss.as.console.client.auth.LoggedInGatekeeper;
import org.jboss.as.console.client.auth.SignInPagePresenter;
import org.jboss.as.console.client.auth.SignInPageView;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.DefaultPlaceManager;
import org.jboss.as.console.client.core.Footer;
import org.jboss.as.console.client.core.Header;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.MainLayoutViewImpl;
import org.jboss.as.console.client.core.message.MessageBar;
import org.jboss.as.console.client.core.message.MessageCenter;
import org.jboss.as.console.client.core.message.MessageCenterView;
import org.jboss.as.console.client.debug.DebugToolsPresenter;
import org.jboss.as.console.client.debug.DebugToolsView;
import org.jboss.as.console.client.debug.InvocationMetricsPresenter;
import org.jboss.as.console.client.debug.InvocationMetricsView;
import org.jboss.as.console.client.debug.ModelBrowserPresenter;
import org.jboss.as.console.client.debug.ModelBrowserView;
import org.jboss.as.console.client.domain.groups.ServerGroupMgmtPresenter;
import org.jboss.as.console.client.domain.groups.ServerGroupMgmtView;
import org.jboss.as.console.client.domain.groups.ServerGroupPresenter;
import org.jboss.as.console.client.domain.groups.ServerGroupView;
import org.jboss.as.console.client.domain.groups.deployment.DeploymentsOverview;
import org.jboss.as.console.client.domain.groups.deployment.DeploymentsPresenter;
import org.jboss.as.console.client.domain.hosts.HostMgmtPresenter;
import org.jboss.as.console.client.domain.hosts.HostMgmtView;
import org.jboss.as.console.client.domain.hosts.ServerConfigPresenter;
import org.jboss.as.console.client.domain.hosts.ServerConfigView;
import org.jboss.as.console.client.domain.hosts.ServerInstancesPresenter;
import org.jboss.as.console.client.domain.hosts.ServerInstancesView;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.impl.HostInfoStoreImpl;
import org.jboss.as.console.client.domain.model.impl.ProfileStoreImpl;
import org.jboss.as.console.client.domain.model.impl.ServerGroupStoreImpl;
import org.jboss.as.console.client.domain.overview.DomainOverview;
import org.jboss.as.console.client.domain.overview.DomainOverviewPresenter;
import org.jboss.as.console.client.domain.profiles.CurrentSelectedProfile;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtView;
import org.jboss.as.console.client.server.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.server.ServerMgmtApplicationView;
import org.jboss.as.console.client.server.deployment.DeploymentListPresenter;
import org.jboss.as.console.client.server.deployment.DeploymentListView;
import org.jboss.as.console.client.server.deployment.DeploymentMgmtPresenter;
import org.jboss.as.console.client.server.deployment.DeploymentMgmtView;
import org.jboss.as.console.client.server.interfaces.InterfaceToolPresenter;
import org.jboss.as.console.client.server.interfaces.InterfaceToolViewImpl;
import org.jboss.as.console.client.server.path.PathToolPresenter;
import org.jboss.as.console.client.server.path.PathToolViewImpl;
import org.jboss.as.console.client.server.properties.PropertyToolPresenter;
import org.jboss.as.console.client.server.properties.PropertyToolViewImpl;
import org.jboss.as.console.client.server.sockets.SocketToolPresenter;
import org.jboss.as.console.client.server.sockets.SocketToolViewImpl;
import org.jboss.as.console.client.server.subsys.threads.ThreadManagementPresenter;
import org.jboss.as.console.client.server.subsys.threads.ThreadManagementView;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.InvocationMetrics;
import org.jboss.as.console.client.shared.dispatch.impl.DMRHandler;
import org.jboss.as.console.client.shared.dispatch.impl.DispatchAsyncImpl;
import org.jboss.as.console.client.shared.dispatch.impl.HandlerRegistry;
import org.jboss.as.console.client.shared.model.DeploymentStore;
import org.jboss.as.console.client.shared.model.DeploymentStoreImpl;
import org.jboss.as.console.client.shared.model.SubsystemStore;
import org.jboss.as.console.client.shared.model.SubsystemStoreImpl;
import org.jboss.as.console.client.shared.sockets.SocketBindingPresenter;
import org.jboss.as.console.client.shared.sockets.SocketBindingView;
import org.jboss.as.console.client.shared.subsys.jca.DataSourcePresenter;
import org.jboss.as.console.client.shared.subsys.jca.DatasourceView;
import org.jboss.as.console.client.shared.subsys.jms.JMSPresenter;
import org.jboss.as.console.client.shared.subsys.jms.JMSView;
import org.jboss.as.console.client.shared.subsys.logging.LoggingPresenter;
import org.jboss.as.console.client.shared.subsys.logging.LoggingView;
import org.jboss.as.console.client.system.SystemApplicationPresenter;
import org.jboss.as.console.client.system.SystemApplicationViewImpl;

/**
 * Provides the bindings for the core UI widgets.
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

        bind(DispatchAsync.class).to(DispatchAsyncImpl.class).in(Singleton.class);
        bind(HandlerRegistry.class).in(Singleton.class);
        bind(DMRHandler.class).in(Singleton.class);
        bind(InvocationMetrics.class).in(Singleton.class);

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
                ServerMgmtApplicationView.class,
                ServerMgmtApplicationPresenter.ServerManagementProxy.class);

        // server deployments
        bindPresenter(DeploymentMgmtPresenter.class,
                DeploymentMgmtPresenter.DeploymentToolView.class,
                DeploymentMgmtView.class,
                DeploymentMgmtPresenter.DeploymentToolProxy.class);

        bindPresenter(DeploymentListPresenter.class,
                DeploymentListPresenter.MyView.class,
                DeploymentListView.class,
                DeploymentListPresenter.MyProxy.class);

        bind(DeploymentStore.class).to(DeploymentStoreImpl.class).in(Singleton.class);

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
                ThreadManagementView.class,
                ThreadManagementPresenter.MyProxy.class);


        // ------------------------------------------------
        // domain management application

        // profile management application
        bindPresenter(ProfileMgmtPresenter.class,
                ProfileMgmtPresenter.MyView.class,
                ProfileMgmtView.class,
                ProfileMgmtPresenter.MyProxy.class);

        // domain/profiles
        bindPresenter(DomainOverviewPresenter.class,
                DomainOverviewPresenter.MyView.class,
                DomainOverview.class,
                DomainOverviewPresenter.MyProxy.class);
        bind(CurrentSelectedProfile.class).in(Singleton.class);

        // domain/server-group
        bindPresenter(ServerGroupMgmtPresenter.class,
                ServerGroupMgmtPresenter.MyView.class,
                ServerGroupMgmtView.class,
                ServerGroupMgmtPresenter.MyProxy.class);

        // domain/server-group
        bindPresenter(ServerGroupPresenter.class,
                ServerGroupPresenter.MyView.class,
                ServerGroupView.class,
                ServerGroupPresenter.MyProxy.class);

        bind(ProfileStore.class).to(ProfileStoreImpl.class).in(Singleton.class);
        bind(SubsystemStore.class).to(SubsystemStoreImpl.class).in(Singleton.class);
        bind(ServerGroupStore.class).to(ServerGroupStoreImpl.class).in(Singleton.class);
        bind(HostInformationStore.class).to(HostInfoStoreImpl.class).in(Singleton.class);

        // domain/domain-deployments
        bindPresenter(DeploymentsPresenter.class,
                DeploymentsPresenter.MyView.class,
                DeploymentsOverview.class,
                DeploymentsPresenter.MyProxy.class);


        bindPresenter(HostMgmtPresenter.class,
                HostMgmtPresenter.MyView.class,
                HostMgmtView.class,
                HostMgmtPresenter.MyProxy.class);

        bindPresenter(ServerConfigPresenter.class,
                ServerConfigPresenter.MyView.class,
                ServerConfigView.class,
                ServerConfigPresenter.MyProxy.class);

        bindPresenter(ServerInstancesPresenter.class,
                ServerInstancesPresenter.MyView.class,
                ServerInstancesView.class,
                ServerInstancesPresenter.MyProxy.class);


        // -------


        bindPresenter(DebugToolsPresenter.class,
                DebugToolsPresenter.MyView.class,
                DebugToolsView.class,
                DebugToolsPresenter.MyProxy.class);

        bindPresenter(ModelBrowserPresenter.class,
                ModelBrowserPresenter.MyView.class,
                ModelBrowserView.class,
                ModelBrowserPresenter.MyProxy.class);

        bindPresenter(InvocationMetricsPresenter.class,
                InvocationMetricsPresenter.MyView.class,
                InvocationMetricsView.class,
                InvocationMetricsPresenter.MyProxy.class);

        // -------

        bindPresenter(DataSourcePresenter.class,
                DataSourcePresenter.MyView.class,
                DatasourceView.class,
                DataSourcePresenter.MyProxy.class);

        bindPresenter(JMSPresenter.class,
                JMSPresenter.MyView.class,
                JMSView.class,
                JMSPresenter.MyProxy.class);

        bindPresenter(LoggingPresenter.class,
                LoggingPresenter.MyView.class,
                LoggingView.class,
                LoggingPresenter.MyProxy.class);

        bindPresenter(SocketBindingPresenter.class,
                SocketBindingPresenter.MyView.class,
                SocketBindingView.class,
                SocketBindingPresenter.MyProxy.class);
    }

}
