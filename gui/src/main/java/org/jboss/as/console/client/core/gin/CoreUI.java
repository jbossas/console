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
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import org.jboss.as.console.client.auth.CurrentUser;
import org.jboss.as.console.client.auth.SignInPagePresenter;
import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.Footer;
import org.jboss.as.console.client.core.Header;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.StandaloneGateKeeper;
import org.jboss.as.console.client.core.message.MessageBar;
import org.jboss.as.console.client.core.message.MessageCenter;
import org.jboss.as.console.client.core.message.MessageCenterView;
import org.jboss.as.console.client.core.settings.SettingsPresenter;
import org.jboss.as.console.client.core.settings.SettingsPresenterWidget;
import org.jboss.as.console.client.domain.groups.ServerGroupMgmtPresenter;
import org.jboss.as.console.client.domain.groups.ServerGroupPresenter;
import org.jboss.as.console.client.domain.groups.deployment.DeploymentsPresenter;
import org.jboss.as.console.client.domain.hosts.HostMgmtPresenter;
import org.jboss.as.console.client.domain.hosts.HostVMMetricPresenter;
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
import org.jboss.as.console.client.domain.runtime.DomainRuntimePresenter;
import org.jboss.as.console.client.plugins.SubsystemRegistry;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.HandlerMapping;
import org.jboss.as.console.client.shared.dispatch.InvocationMetrics;
import org.jboss.as.console.client.shared.dispatch.impl.DMRHandler;
import org.jboss.as.console.client.shared.expr.ExpressionResolver;
import org.jboss.as.console.client.shared.general.InterfacePresenter;
import org.jboss.as.console.client.shared.general.PropertiesPresenter;
import org.jboss.as.console.client.shared.general.SocketBindingPresenter;
import org.jboss.as.console.client.shared.help.HelpSystem;
import org.jboss.as.console.client.shared.model.DeploymentStore;
import org.jboss.as.console.client.shared.model.SubsystemStore;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.runtime.ds.DataSourceMetricPresenter;
import org.jboss.as.console.client.shared.runtime.jms.JMSMetricPresenter;
import org.jboss.as.console.client.shared.runtime.jpa.JPAMetricPresenter;
import org.jboss.as.console.client.shared.runtime.tx.TXMetricPresenter;
import org.jboss.as.console.client.shared.runtime.web.WebMetricPresenter;
import org.jboss.as.console.client.shared.runtime.ws.WebServiceRuntimePresenter;
import org.jboss.as.console.client.shared.state.CurrentHostSelection;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ReloadState;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.configadmin.ConfigAdminPresenter;
import org.jboss.as.console.client.shared.subsys.deploymentscanner.ScannerPresenter;
import org.jboss.as.console.client.shared.subsys.ejb3.EEPresenter;
import org.jboss.as.console.client.shared.subsys.ejb3.EJB3Presenter;
import org.jboss.as.console.client.shared.subsys.infinispan.CacheContainerPresenter;
import org.jboss.as.console.client.shared.subsys.infinispan.DistributedCachePresenter;
import org.jboss.as.console.client.shared.subsys.infinispan.InvalidationCachePresenter;
import org.jboss.as.console.client.shared.subsys.infinispan.LocalCachePresenter;
import org.jboss.as.console.client.shared.subsys.infinispan.ReplicatedCachePresenter;
import org.jboss.as.console.client.shared.subsys.jacorb.JacOrbPresenter;
import org.jboss.as.console.client.shared.subsys.jca.DataSourcePresenter;
import org.jboss.as.console.client.shared.subsys.jca.JcaPresenter;
import org.jboss.as.console.client.shared.subsys.jca.ResourceAdapterPresenter;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSourceStore;
import org.jboss.as.console.client.shared.subsys.jca.model.DomainDriverStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.DriverRegistry;
import org.jboss.as.console.client.shared.subsys.jca.model.StandaloneDriverStrategy;
import org.jboss.as.console.client.shared.subsys.jgroups.JGroupsPresenter;
import org.jboss.as.console.client.shared.subsys.jmx.JMXPresenter;
import org.jboss.as.console.client.shared.subsys.jpa.JpaPresenter;
import org.jboss.as.console.client.shared.subsys.logging.HandlerListManager;
import org.jboss.as.console.client.shared.subsys.logging.LoggingPresenter;
import org.jboss.as.console.client.shared.subsys.mail.MailPresenter;
import org.jboss.as.console.client.shared.subsys.messaging.MessagingPresenter;
import org.jboss.as.console.client.shared.subsys.modcluster.ModclusterPresenter;
import org.jboss.as.console.client.shared.subsys.naming.JndiPresenter;
import org.jboss.as.console.client.shared.subsys.osgi.config.OSGiConfigurationPresenter;
import org.jboss.as.console.client.shared.subsys.osgi.runtime.OSGiRuntimePresenter;
import org.jboss.as.console.client.shared.subsys.security.SecurityDomainsPresenter;
import org.jboss.as.console.client.shared.subsys.security.SecuritySubsystemPresenter;
import org.jboss.as.console.client.shared.subsys.threads.ThreadsPresenter;
import org.jboss.as.console.client.shared.subsys.tx.TransactionPresenter;
import org.jboss.as.console.client.shared.subsys.web.WebPresenter;
import org.jboss.as.console.client.shared.subsys.ws.DomainEndpointStrategy;
import org.jboss.as.console.client.shared.subsys.ws.EndpointRegistry;
import org.jboss.as.console.client.shared.subsys.ws.StandaloneEndpointStrategy;
import org.jboss.as.console.client.shared.subsys.ws.WebServicePresenter;
import org.jboss.as.console.client.standalone.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.standalone.StandaloneServerPresenter;
import org.jboss.as.console.client.standalone.deployment.DeploymentListPresenter;
import org.jboss.as.console.client.standalone.runtime.StandaloneRuntimePresenter;
import org.jboss.as.console.client.standalone.runtime.VMMetricsPresenter;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;


/**
 * Overall module configuration.
 *
 * @see CoreUIModule
 *
 * @author Heiko Braun
 * @date 1/31/11
 */
@GinModules(PluginContainer.class)
public interface CoreUI extends Ginjector {


    SubsystemRegistry getSubsystemRegistry();

    PlaceManager getPlaceManager();
    EventBus getEventBus();
    //ProxyFailureHandler getProxyFailureHandler();
    TokenFormatter getTokenFormatter();

    //@DefaultGatekeeper
    //Gatekeeper getLoggedInGatekeeper();

    StandaloneGateKeeper getStandaloneGatekeeper();
    DomainGateKeeper getDomainGatekeeper();

    CurrentUser getCurrentUser();
    BootstrapContext getBootstrapContext();
    ApplicationProperties getAppProperties();

    // ----------------------------------------------------------------------

    Header getHeader();
    Footer getFooter();

    MessageBar getMessageBar();
    MessageCenter getMessageCenter();
    MessageCenterView getMessageCenterView();

    HelpSystem getHelpSystem();

    ExpressionResolver getExpressionManager();
    Baseadress getBaseadress();
    RuntimeBaseAddress getRuntimeBaseAddress();

    // ----------------------------------------------------------------------

    DispatchAsync getDispatchAsync();
    HandlerMapping getDispatcherHandlerRegistry();
    DMRHandler getDMRHandler();
    InvocationMetrics getInvocationMetrics();

    ApplicationMetaData getApplicationMetaData();

    // ----------------------------------------------------------------------
    Provider<SignInPagePresenter> getSignInPagePresenter();
    AsyncProvider<MainLayoutPresenter> getMainLayoutPresenter();
    AsyncProvider<SettingsPresenter> getSettingsPresenter();
    AsyncProvider<SettingsPresenterWidget> getSettingsPresenterWidget();


    // ----------------------------------------------------------------------
    AsyncProvider<ServerMgmtApplicationPresenter> getServerManagementAppPresenter();
    AsyncProvider<DeploymentListPresenter> getDeploymentListPresenter();

    DeploymentStore getDeploymentStore();


    // ----------------------------------------------------------------------
    // domain config below
    AsyncProvider<ProfileMgmtPresenter> getProfileMgmtPresenter();
    CurrentProfileSelection getCurrentSelectedProfile();
    CurrentHostSelection getCurrentSelectedHost();
    CurrentServerSelection getCurrentSelectedServer();
    ReloadState getReloadState();

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
    // shared subsystems
    AsyncProvider<DataSourcePresenter> getDataSourcePresenter();
    DataSourceStore getDataSourceStore();

    DomainDriverStrategy getDomainDriverStrategy();
    StandaloneDriverStrategy getStandloneDriverStrategy();
    DriverRegistry getDriverRegistry();

    AsyncProvider<EJB3Presenter> getEJB3Presenter();
    AsyncProvider<MessagingPresenter> getMessagingPresenter();

    AsyncProvider<LoggingPresenter> getLoggingPresenter();
    HandlerListManager getHandlerListManager();

    AsyncProvider<ScannerPresenter> getScannerPresenter();
    AsyncProvider<ConfigAdminPresenter> getConfigAdminPresenter();
    AsyncProvider<OSGiConfigurationPresenter> getOSGiConfigurationPresenter();
    AsyncProvider<OSGiRuntimePresenter> getOSGiRuntimePresenter();
    AsyncProvider<SocketBindingPresenter> getSocketBindingPresenter();

    // Infinispan
    AsyncProvider<CacheContainerPresenter> getCacheContainerPresenter();
    AsyncProvider<LocalCachePresenter> getLocalCachePresenter();
    AsyncProvider<InvalidationCachePresenter> getInvalidationCachePresenter();
    AsyncProvider<DistributedCachePresenter> getDistributedCachePresenter();
    AsyncProvider<ReplicatedCachePresenter> getReplicatedCachePresenter();

    AsyncProvider<ThreadsPresenter> getBoundedQueueThreadPoolPresenter();

    AsyncProvider<WebPresenter> getWebPresenter();

    AsyncProvider<InterfacePresenter> getInterfacePresenter();
    AsyncProvider<PropertiesPresenter> getDomainPropertiesPresenter();

    AsyncProvider<HostPropertiesPresenter> getHostPropertiesPresenter();
    AsyncProvider<HostJVMPresenter> getHostJVMPresenter();
    AsyncProvider<HostInterfacesPresenter> getHostInterfacesPresenter();

    AsyncProvider<StandaloneServerPresenter> getStandaloneServerPresenter();

    AsyncProvider<WebServicePresenter> getWebServicePresenter();
    AsyncProvider<WebServiceRuntimePresenter> getWebServiceRuntimePresenter();

    EndpointRegistry getEndpointRegistry();
    DomainEndpointStrategy getDomainEndpointStrategy();
    StandaloneEndpointStrategy getStandaloneEndpointStrategy();

    AsyncProvider<ResourceAdapterPresenter> getResourceAdapterPresenter();
    AsyncProvider<JndiPresenter> getJndiPresenter();

    AsyncProvider<VMMetricsPresenter> getVMMetricsPresenter();
    AsyncProvider<HostVMMetricPresenter> getServerVMMetricPresenter();

    AsyncProvider<TransactionPresenter> getTransactionPresenter();
    AsyncProvider<SecuritySubsystemPresenter> getSecuritySubsystemPresenter();
    AsyncProvider<SecurityDomainsPresenter> getSecurityDomainsPresenter();

    AsyncProvider<StandaloneRuntimePresenter> getRuntimePresenter();
    AsyncProvider<DomainRuntimePresenter> getDomainRuntimePresenter();
    AsyncProvider<TXMetricPresenter> getTXMetricPresenter();

    AsyncProvider<JacOrbPresenter> getJacOrbPresenter();
    AsyncProvider<JpaPresenter> getJpaPresenter();
    AsyncProvider<MailPresenter> getMailPresenter();
    AsyncProvider<ModclusterPresenter> getModclusterPresenter();
    AsyncProvider<JMXPresenter> getJMXPresenter();
    AsyncProvider<EEPresenter> getEEPresenter();

    AsyncProvider<JcaPresenter> getJcaPresenter();

    AsyncProvider<WebMetricPresenter> WebMetricPresenter();

    AsyncProvider<JMSMetricPresenter> JMSMetricPresenter();

    AsyncProvider<DataSourceMetricPresenter> DataSourceMetricPresenter();

    AsyncProvider<JPAMetricPresenter> JPAMetricPresenter();

    AsyncProvider<JGroupsPresenter> JGroupsPresenter();

}
