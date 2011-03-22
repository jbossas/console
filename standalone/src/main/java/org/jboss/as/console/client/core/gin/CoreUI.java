package org.jboss.as.console.client.core.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.gwtplatform.mvp.client.annotations.DefaultGatekeeper;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyFailureHandler;
import org.jboss.as.console.client.auth.CurrentUser;
import org.jboss.as.console.client.auth.SignInPagePresenter;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.Footer;
import org.jboss.as.console.client.core.Header;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.message.MessageBar;
import org.jboss.as.console.client.core.message.MessageCenter;
import org.jboss.as.console.client.core.message.MessageCenterView;
import org.jboss.as.console.client.debug.DebugToolsPresenter;
import org.jboss.as.console.client.debug.InvocationMetricsPresenter;
import org.jboss.as.console.client.debug.ModelBrowserPresenter;
import org.jboss.as.console.client.domain.CurrentSelectedProfile;
import org.jboss.as.console.client.domain.DomainOverviewPresenter;
import org.jboss.as.console.client.domain.deployment.DeploymentsPresenter;
import org.jboss.as.console.client.domain.groups.ServerGroupMgmtPresenter;
import org.jboss.as.console.client.domain.groups.ServerGroupPresenter;
import org.jboss.as.console.client.domain.hosts.HostMgmtPresenter;
import org.jboss.as.console.client.domain.hosts.InstancesPresenter;
import org.jboss.as.console.client.domain.hosts.ServerPresenter;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.server.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.server.deployment.DeploymentListPresenter;
import org.jboss.as.console.client.server.deployment.DeploymentMgmtPresenter;
import org.jboss.as.console.client.server.interfaces.InterfaceToolPresenter;
import org.jboss.as.console.client.server.path.PathToolPresenter;
import org.jboss.as.console.client.server.properties.PropertyToolPresenter;
import org.jboss.as.console.client.server.sockets.SocketToolPresenter;
import org.jboss.as.console.client.server.subsys.threads.ThreadManagementPresenter;
import org.jboss.as.console.client.shared.dispatch.InvocationMetrics;
import org.jboss.as.console.client.shared.dispatch.impl.DMRHandler;
import org.jboss.as.console.client.shared.model.DeploymentStore;
import org.jboss.as.console.client.shared.model.SubsystemStore;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.HandlerRegistry;
import org.jboss.as.console.client.system.SystemApplicationPresenter;

/**
 * Overall module configuration.
 *
 * @see CoreUIModule
 *
 * @author Heiko Braun
 * @date 1/31/11
 */
@GinModules(CoreUIModule.class)
public interface CoreUI extends Ginjector {

    PlaceManager getPlaceManager();
    EventBus getEventBus();
    ProxyFailureHandler getProxyFailureHandler();

    @DefaultGatekeeper
    Gatekeeper getLoggedInGatekeeper();
    CurrentUser getCurrentUser();
    BootstrapContext getBootstrapContext();

    // ----------------------------------------------------------------------

    Header getHeader();
    Footer getFooter();

    MessageBar getMessageBar();
    MessageCenter getMessageCenter();
    MessageCenterView getMessageCenterView();

    // ----------------------------------------------------------------------

    DispatchAsync getDispatchAsync();
    HandlerRegistry getDispatcherHandlerRegistry();
    DMRHandler getDMRHandler();
    InvocationMetrics getInvocationMetrics();

    // ----------------------------------------------------------------------
    Provider<SignInPagePresenter> getSignInPagePresenter();
    AsyncProvider<MainLayoutPresenter> getMainLayoutPresenter();

    // ----------------------------------------------------------------------
    AsyncProvider<SystemApplicationPresenter> getSystemAppPresenter();

    // ----------------------------------------------------------------------
    AsyncProvider<ServerMgmtApplicationPresenter> getServerManagementAppPresenter();
    AsyncProvider<DeploymentMgmtPresenter> getDeploymentMgmtPresenter();
    AsyncProvider<DeploymentListPresenter> getDeploymentListPresenter();

    DeploymentStore getDeploymentStore();

    AsyncProvider<InterfaceToolPresenter> getInterfaceToolPresenter();
    AsyncProvider<PathToolPresenter> getPathToolPresenter();
    AsyncProvider<PropertyToolPresenter> getPropertyToolPresenter();
    AsyncProvider<SocketToolPresenter> getSocketToolPresenter();

    AsyncProvider<ThreadManagementPresenter> getThreadManagementPresenter();


    // ----------------------------------------------------------------------
    // domain config below
    AsyncProvider<ProfileMgmtPresenter> getProfileMgmtPresenter();
    CurrentSelectedProfile getCurrentSelectedProfile();
    AsyncProvider<ServerGroupMgmtPresenter> getServerGroupMgmtPresenter();

    AsyncProvider<DomainOverviewPresenter> getProfileToolPresenter();
    AsyncProvider<ServerGroupPresenter> getServerGroupsPresenter();

    ProfileStore getProfileStore();
    SubsystemStore getSubsystemStore();
    ServerGroupStore getServerGroupStore();
    HostInformationStore getHostInfoStore();


    AsyncProvider<DeploymentsPresenter> getDeploymentsPresenter();

    AsyncProvider<HostMgmtPresenter> getHostMgmtPresenter();
    AsyncProvider<ServerPresenter> getServerPresenter();
    AsyncProvider<InstancesPresenter> getInstancesPresenter();

    // ----------------------------------------------------------------------
    // dev tools
    AsyncProvider<DebugToolsPresenter> getDebugTools();
    AsyncProvider<ModelBrowserPresenter> getModelBrowser();
    AsyncProvider<InvocationMetricsPresenter> getMetrics();
}
