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

package org.jboss.as.console.client.core.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyFailureHandler;
import org.jboss.as.console.client.auth.CurrentUser;
import org.jboss.as.console.client.auth.SignInPagePresenter;
import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.Footer;
import org.jboss.as.console.client.core.Header;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.message.MessageBar;
import org.jboss.as.console.client.core.message.MessageCenter;
import org.jboss.as.console.client.core.message.MessageCenterView;
import org.jboss.as.console.client.core.settings.SettingsPresenter;
import org.jboss.as.console.client.core.settings.SettingsPresenterWidget;
import org.jboss.as.console.client.debug.DebugToolsPresenter;
import org.jboss.as.console.client.debug.InvocationMetricsPresenter;
import org.jboss.as.console.client.debug.ModelBrowserPresenter;
import org.jboss.as.console.client.domain.general.DomainPropertiesPresenter;
import org.jboss.as.console.client.domain.general.InterfacePresenter;
import org.jboss.as.console.client.domain.groups.ServerGroupMgmtPresenter;
import org.jboss.as.console.client.domain.groups.ServerGroupPresenter;
import org.jboss.as.console.client.domain.groups.deployment.DeploymentsPresenter;
import org.jboss.as.console.client.domain.hosts.CurrentHostSelection;
import org.jboss.as.console.client.domain.hosts.HostMgmtPresenter;
import org.jboss.as.console.client.domain.hosts.ServerConfigPresenter;
import org.jboss.as.console.client.domain.hosts.ServerInstancesPresenter;
import org.jboss.as.console.client.domain.hosts.general.HostInterfacesPresenter;
import org.jboss.as.console.client.domain.hosts.general.HostJVMPresenter;
import org.jboss.as.console.client.domain.hosts.general.HostPropertiesPresenter;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.overview.DomainOverviewPresenter;
import org.jboss.as.console.client.domain.profiles.CurrentProfileSelection;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.HandlerMapping;
import org.jboss.as.console.client.shared.dispatch.InvocationMetrics;
import org.jboss.as.console.client.shared.dispatch.impl.DMRHandler;
import org.jboss.as.console.client.shared.model.DeploymentStore;
import org.jboss.as.console.client.shared.model.SubsystemStore;
import org.jboss.as.console.client.shared.sockets.SocketBindingPresenter;
import org.jboss.as.console.client.shared.subsys.jca.DataSourcePresenter;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSourceStore;
import org.jboss.as.console.client.shared.subsys.jca.model.DomainDriverStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.DriverRegistry;
import org.jboss.as.console.client.shared.subsys.jca.model.DriverStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.StandaloneDriverStrategy;
import org.jboss.as.console.client.shared.subsys.logging.LoggingPresenter;
import org.jboss.as.console.client.shared.subsys.messaging.JMSPresenter;
import org.jboss.as.console.client.shared.subsys.messaging.MessagingPresenter;
import org.jboss.as.console.client.shared.subsys.web.WebPresenter;
import org.jboss.as.console.client.standalone.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.standalone.deployment.DeploymentListPresenter;
import org.jboss.as.console.client.standalone.interfaces.InterfaceToolPresenter;
import org.jboss.as.console.client.standalone.path.PathToolPresenter;
import org.jboss.as.console.client.standalone.properties.PropertyToolPresenter;
import org.jboss.as.console.client.standalone.sockets.SocketToolPresenter;
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

    //@DefaultGatekeeper
    Gatekeeper getLoggedInGatekeeper();
    CurrentUser getCurrentUser();
    BootstrapContext getBootstrapContext();
    ApplicationProperties getAppProperties();

    // ----------------------------------------------------------------------

    Header getHeader();
    Footer getFooter();

    MessageBar getMessageBar();
    MessageCenter getMessageCenter();
    MessageCenterView getMessageCenterView();

    // ----------------------------------------------------------------------

    DispatchAsync getDispatchAsync();
    HandlerMapping getDispatcherHandlerRegistry();
    DMRHandler getDMRHandler();
    InvocationMetrics getInvocationMetrics();

    // ----------------------------------------------------------------------
    Provider<SignInPagePresenter> getSignInPagePresenter();
    AsyncProvider<MainLayoutPresenter> getMainLayoutPresenter();
    AsyncProvider<SettingsPresenter> getSettingsPresenter();
    AsyncProvider<SettingsPresenterWidget> getSettingsPresenterWidget();

    // ----------------------------------------------------------------------
    AsyncProvider<SystemApplicationPresenter> getSystemAppPresenter();

    // ----------------------------------------------------------------------
    AsyncProvider<ServerMgmtApplicationPresenter> getServerManagementAppPresenter();
    AsyncProvider<DeploymentListPresenter> getDeploymentListPresenter();

    DeploymentStore getDeploymentStore();

    AsyncProvider<InterfaceToolPresenter> getInterfaceToolPresenter();
    AsyncProvider<PathToolPresenter> getPathToolPresenter();
    AsyncProvider<PropertyToolPresenter> getPropertyToolPresenter();
    AsyncProvider<SocketToolPresenter> getSocketToolPresenter();

    // ----------------------------------------------------------------------
    // domain config below
    AsyncProvider<ProfileMgmtPresenter> getProfileMgmtPresenter();
    CurrentProfileSelection getCurrentSelectedProfile();
    CurrentHostSelection getCurrentSelectedHost();
    AsyncProvider<ServerGroupMgmtPresenter> getServerGroupMgmtPresenter();

    AsyncProvider<DomainOverviewPresenter> getProfileToolPresenter();
    AsyncProvider<ServerGroupPresenter> getServerGroupsPresenter();

    ProfileStore getProfileStore();
    SubsystemStore getSubsystemStore();
    ServerGroupStore getServerGroupStore();
    HostInformationStore getHostInfoStore();


    AsyncProvider<DeploymentsPresenter> getDeploymentsPresenter();

    AsyncProvider<HostMgmtPresenter> getHostMgmtPresenter();
    AsyncProvider<ServerConfigPresenter> getServerPresenter();
    AsyncProvider<ServerInstancesPresenter> getInstancesPresenter();

    // ----------------------------------------------------------------------
    // dev tools
    AsyncProvider<DebugToolsPresenter> getDebugTools();
    AsyncProvider<ModelBrowserPresenter> getModelBrowser();
    AsyncProvider<InvocationMetricsPresenter> getMetrics();


    // ----------------------------------------------------------------------
    // shared subsystems
    AsyncProvider<DataSourcePresenter> getDataSourcePresenter();
    DataSourceStore getDataSourceStore();
    DomainDriverStrategy getDomainDriverStrategy();
    StandaloneDriverStrategy getStandloneDriverStrategy();
    DriverRegistry getDriverRegistry();

    AsyncProvider<JMSPresenter> getJMSPresenter();
    AsyncProvider<MessagingPresenter> getMessagingPresenter();
    AsyncProvider<LoggingPresenter> getLoggingPresenter();
    AsyncProvider<SocketBindingPresenter> getSocketBindingPresenter();

    AsyncProvider<WebPresenter> getWebPresenter();

    AsyncProvider<InterfacePresenter> getInterfacePresenter();
    AsyncProvider<DomainPropertiesPresenter> getDomainPropertiesPresenter();

    AsyncProvider<HostPropertiesPresenter> getHostPropertiesPresenter();
    AsyncProvider<HostJVMPresenter> getHostJVMPresenter();
    AsyncProvider<HostInterfacesPresenter> getHostInterfacesPresenter();
}
