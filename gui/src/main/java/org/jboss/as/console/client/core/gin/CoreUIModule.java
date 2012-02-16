
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
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import org.jboss.as.console.client.auth.CurrentUser;
import org.jboss.as.console.client.auth.SignInPagePresenter;
import org.jboss.as.console.client.auth.SignInPageView;
import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.DefaultPlaceManager;
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.DomainUse;
import org.jboss.as.console.client.core.Footer;
import org.jboss.as.console.client.core.Header;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.MainLayoutViewImpl;
import org.jboss.as.console.client.core.NewTokenFormatter;
import org.jboss.as.console.client.core.StandaloneGateKeeper;
import org.jboss.as.console.client.core.StandaloneUse;
import org.jboss.as.console.client.core.message.MessageBar;
import org.jboss.as.console.client.core.message.MessageCenter;
import org.jboss.as.console.client.core.message.MessageCenterView;
import org.jboss.as.console.client.core.settings.SettingsPresenter;
import org.jboss.as.console.client.core.settings.SettingsPresenterViewImpl;
import org.jboss.as.console.client.core.settings.SettingsPresenterWidget;
import org.jboss.as.console.client.core.settings.SettingsView;
import org.jboss.as.console.client.domain.groups.ServerGroupMgmtPresenter;
import org.jboss.as.console.client.domain.groups.ServerGroupMgmtView;
import org.jboss.as.console.client.domain.groups.ServerGroupPresenter;
import org.jboss.as.console.client.domain.groups.ServerGroupView;
import org.jboss.as.console.client.domain.groups.deployment.DeploymentsOverview;
import org.jboss.as.console.client.domain.groups.deployment.DeploymentsPresenter;
import org.jboss.as.console.client.domain.hosts.HostMgmtPresenter;
import org.jboss.as.console.client.domain.hosts.HostMgmtView;
import org.jboss.as.console.client.domain.hosts.HostVMMetricPresenter;
import org.jboss.as.console.client.domain.hosts.HostVMMetricView;
import org.jboss.as.console.client.domain.hosts.ServerConfigPresenter;
import org.jboss.as.console.client.domain.hosts.ServerConfigView;
import org.jboss.as.console.client.domain.hosts.ServerInstancesPresenter;
import org.jboss.as.console.client.domain.hosts.ServerInstancesView;
import org.jboss.as.console.client.domain.hosts.general.HostInterfacesPresenter;
import org.jboss.as.console.client.domain.hosts.general.HostInterfacesView;
import org.jboss.as.console.client.domain.hosts.general.HostJVMPresenter;
import org.jboss.as.console.client.domain.hosts.general.HostJVMView;
import org.jboss.as.console.client.domain.hosts.general.HostPropertiesPresenter;
import org.jboss.as.console.client.domain.hosts.general.HostPropertiesView;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.impl.HostInfoStoreImpl;
import org.jboss.as.console.client.domain.model.impl.ProfileStoreImpl;
import org.jboss.as.console.client.domain.model.impl.ServerGroupStoreImpl;
import org.jboss.as.console.client.domain.overview.DomainOverview;
import org.jboss.as.console.client.domain.overview.DomainOverviewPresenter;
import org.jboss.as.console.client.domain.profiles.CurrentProfileSelection;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtView;
import org.jboss.as.console.client.domain.runtime.DomainRuntimePresenter;
import org.jboss.as.console.client.domain.runtime.DomainRuntimeView;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.HandlerMapping;
import org.jboss.as.console.client.shared.dispatch.InvocationMetrics;
import org.jboss.as.console.client.shared.dispatch.impl.DMRHandler;
import org.jboss.as.console.client.shared.dispatch.impl.DispatchAsyncImpl;
import org.jboss.as.console.client.shared.dispatch.impl.HandlerRegistry;
import org.jboss.as.console.client.shared.expr.DefaultExpressionResolver;
import org.jboss.as.console.client.shared.expr.ExpressionResolver;
import org.jboss.as.console.client.shared.general.InterfacePresenter;
import org.jboss.as.console.client.shared.general.InterfaceView;
import org.jboss.as.console.client.shared.general.PropertiesPresenter;
import org.jboss.as.console.client.shared.general.PropertiesView;
import org.jboss.as.console.client.shared.general.SocketBindingPresenter;
import org.jboss.as.console.client.shared.general.SocketBindingView;
import org.jboss.as.console.client.shared.help.HelpSystem;
import org.jboss.as.console.client.shared.model.DeploymentStore;
import org.jboss.as.console.client.shared.model.DeploymentStoreImpl;
import org.jboss.as.console.client.shared.model.SubsystemStore;
import org.jboss.as.console.client.shared.model.SubsystemStoreImpl;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.runtime.ds.DataSourceMetricPresenter;
import org.jboss.as.console.client.shared.runtime.ds.DataSourceMetricView;
import org.jboss.as.console.client.shared.runtime.jms.JMSMetricPresenter;
import org.jboss.as.console.client.shared.runtime.jms.JMSMetricView;
import org.jboss.as.console.client.shared.runtime.jpa.JPAMetricPresenter;
import org.jboss.as.console.client.shared.runtime.jpa.JPAMetricsView;
import org.jboss.as.console.client.shared.runtime.tx.TXMetricPresenter;
import org.jboss.as.console.client.shared.runtime.tx.TXMetricViewImpl;
import org.jboss.as.console.client.shared.runtime.web.WebMetricPresenter;
import org.jboss.as.console.client.shared.runtime.web.WebMetricView;
import org.jboss.as.console.client.shared.runtime.ws.WebServiceRuntimePresenter;
import org.jboss.as.console.client.shared.runtime.ws.WebServiceRuntimeView;
import org.jboss.as.console.client.shared.state.CurrentHostSelection;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ReloadState;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.configadmin.ConfigAdminPresenter;
import org.jboss.as.console.client.shared.subsys.configadmin.ConfigAdminView;
import org.jboss.as.console.client.shared.subsys.deploymentscanner.ScannerPresenter;
import org.jboss.as.console.client.shared.subsys.deploymentscanner.ScannerView;
import org.jboss.as.console.client.shared.subsys.ejb3.EEPresenter;
import org.jboss.as.console.client.shared.subsys.ejb3.EESubsystemView;
import org.jboss.as.console.client.shared.subsys.ejb3.EJB3Presenter;
import org.jboss.as.console.client.shared.subsys.ejb3.EJB3View;
import org.jboss.as.console.client.shared.subsys.infinispan.CacheContainerPresenter;
import org.jboss.as.console.client.shared.subsys.infinispan.CacheContainerView;
import org.jboss.as.console.client.shared.subsys.infinispan.DistributedCachePresenter;
import org.jboss.as.console.client.shared.subsys.infinispan.DistributedCacheView;
import org.jboss.as.console.client.shared.subsys.infinispan.InvalidationCachePresenter;
import org.jboss.as.console.client.shared.subsys.infinispan.InvalidationCacheView;
import org.jboss.as.console.client.shared.subsys.infinispan.LocalCachePresenter;
import org.jboss.as.console.client.shared.subsys.infinispan.LocalCacheView;
import org.jboss.as.console.client.shared.subsys.infinispan.ReplicatedCachePresenter;
import org.jboss.as.console.client.shared.subsys.infinispan.ReplicatedCacheView;
import org.jboss.as.console.client.shared.subsys.jacorb.JacOrbPresenter;
import org.jboss.as.console.client.shared.subsys.jacorb.JacOrbView;
import org.jboss.as.console.client.shared.subsys.jca.DataSourcePresenter;
import org.jboss.as.console.client.shared.subsys.jca.DatasourceView;
import org.jboss.as.console.client.shared.subsys.jca.JcaPresenter;
import org.jboss.as.console.client.shared.subsys.jca.JcaSubsystemView;
import org.jboss.as.console.client.shared.subsys.jca.ResourceAdapterPresenter;
import org.jboss.as.console.client.shared.subsys.jca.ResourceAdapterView;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSourceStore;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSourceStoreImpl;
import org.jboss.as.console.client.shared.subsys.jca.model.DomainDriverStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.StandaloneDriverStrategy;
import org.jboss.as.console.client.shared.subsys.jgroups.JGroupsPresenter;
import org.jboss.as.console.client.shared.subsys.jgroups.JGroupsSubsystemView;
import org.jboss.as.console.client.shared.subsys.jmx.JMXPresenter;
import org.jboss.as.console.client.shared.subsys.jmx.JMXSubsystemView;
import org.jboss.as.console.client.shared.subsys.jpa.JpaPresenter;
import org.jboss.as.console.client.shared.subsys.jpa.JpaView;
import org.jboss.as.console.client.shared.subsys.logging.HandlerListManager;
import org.jboss.as.console.client.shared.subsys.logging.LoggingPresenter;
import org.jboss.as.console.client.shared.subsys.logging.LoggingView;
import org.jboss.as.console.client.shared.subsys.mail.MailPresenter;
import org.jboss.as.console.client.shared.subsys.mail.MailSubsystemView;
import org.jboss.as.console.client.shared.subsys.messaging.MessagingPresenter;
import org.jboss.as.console.client.shared.subsys.messaging.MessagingView;
import org.jboss.as.console.client.shared.subsys.naming.JndiPresenter;
import org.jboss.as.console.client.shared.subsys.naming.JndiView;
import org.jboss.as.console.client.shared.subsys.osgi.config.OSGiConfigurationPresenter;
import org.jboss.as.console.client.shared.subsys.osgi.config.OSGiSubsystemView;
import org.jboss.as.console.client.shared.subsys.osgi.runtime.OSGiRuntimePresenter;
import org.jboss.as.console.client.shared.subsys.osgi.runtime.OSGiRuntimeView;
import org.jboss.as.console.client.shared.subsys.security.SecurityDomainsPresenter;
import org.jboss.as.console.client.shared.subsys.security.SecurityDomainsView;
import org.jboss.as.console.client.shared.subsys.security.SecuritySubsystemPresenter;
import org.jboss.as.console.client.shared.subsys.security.SecuritySubsystemView;
import org.jboss.as.console.client.shared.subsys.threads.ThreadsPresenter;
import org.jboss.as.console.client.shared.subsys.threads.ThreadsView;
import org.jboss.as.console.client.shared.subsys.tx.TransactionPresenter;
import org.jboss.as.console.client.shared.subsys.tx.TransactionView;
import org.jboss.as.console.client.shared.subsys.web.WebPresenter;
import org.jboss.as.console.client.shared.subsys.web.WebSubsystemView;
import org.jboss.as.console.client.shared.subsys.ws.DomainEndpointStrategy;
import org.jboss.as.console.client.shared.subsys.ws.EndpointRegistry;
import org.jboss.as.console.client.shared.subsys.ws.StandaloneEndpointStrategy;
import org.jboss.as.console.client.shared.subsys.ws.WebServicePresenter;
import org.jboss.as.console.client.shared.subsys.ws.WebServiceView;
import org.jboss.as.console.client.standalone.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.standalone.ServerMgmtApplicationView;
import org.jboss.as.console.client.standalone.StandaloneServerPresenter;
import org.jboss.as.console.client.standalone.StandaloneServerView;
import org.jboss.as.console.client.standalone.deployment.DeploymentListPresenter;
import org.jboss.as.console.client.standalone.deployment.DeploymentListView;
import org.jboss.as.console.client.standalone.runtime.StandaloneRuntimePresenter;
import org.jboss.as.console.client.standalone.runtime.StandaloneRuntimeView;
import org.jboss.as.console.client.standalone.runtime.VMMetricsPresenter;
import org.jboss.as.console.client.standalone.runtime.VMMetricsView;

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

        bind(HelpSystem.class).in(Singleton.class);

        bind(ExpressionResolver.class).to(DefaultExpressionResolver.class).in(Singleton.class);
        bind(Baseadress.class).in(Singleton.class);
        bind(RuntimeBaseAddress.class).in(Singleton.class);

        // ----------------------------------------------------------------------

        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(PlaceManager.class).to(DefaultPlaceManager.class).in(Singleton.class);

        // see http://code.google.com/p/gwt-platform/issues/detail?id=381
        //bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(Singleton.class);
        bind(TokenFormatter.class).to(NewTokenFormatter.class).in(Singleton.class);

        bind(RootPresenter.class).asEagerSingleton();
        //bind(ProxyFailureHandler.class).to(DefaultProxyFailureHandler.class).in(Singleton.class);
        //bind(Gatekeeper.class).to(LoggedInGatekeeper.class);

        bind(Gatekeeper.class).annotatedWith(DomainUse.class).to(DomainGateKeeper.class).in(Singleton.class);
        bind(Gatekeeper.class).annotatedWith(StandaloneUse.class).to(StandaloneGateKeeper.class).in(Singleton.class);

        bind(CurrentUser.class).in(Singleton.class);
        bind(BootstrapContext.class).in(Singleton.class);
        bind(ApplicationProperties.class).to(BootstrapContext.class).in(Singleton.class);

        // sign in
        bindPresenter(SignInPagePresenter.class, SignInPagePresenter.MyView.class,
                SignInPageView.class, SignInPagePresenter.MyProxy.class);

        // main layout
        bindPresenter(MainLayoutPresenter.class,
                MainLayoutPresenter.MainLayoutView.class,
                MainLayoutViewImpl.class,
                MainLayoutPresenter.MainLayoutProxy.class);

        bindPresenter(SettingsPresenter.class,
                SettingsPresenter.MyView.class,
                SettingsPresenterViewImpl.class,
                SettingsPresenter.MyProxy.class);

        bindPresenterWidget(SettingsPresenterWidget.class,
                SettingsPresenterWidget.MyView.class,
                SettingsView.class
        );

        // ----------------------------------------------------------------------

        bind(DispatchAsync.class).to(DispatchAsyncImpl.class).in(Singleton.class);
        bind(HandlerMapping.class).to(HandlerRegistry.class).in(Singleton.class);
        bind(DMRHandler.class).in(Singleton.class);
        bind(InvocationMetrics.class).in(Singleton.class);

        // ----------------------------------------------------------------------

        // server management application

        bindPresenter(ServerMgmtApplicationPresenter.class,
                ServerMgmtApplicationPresenter.ServerManagementView.class,
                ServerMgmtApplicationView.class,
                ServerMgmtApplicationPresenter.ServerManagementProxy.class);

        bindPresenter(DeploymentListPresenter.class,
                DeploymentListPresenter.MyView.class,
                DeploymentListView.class,
                DeploymentListPresenter.MyProxy.class);

        bind(DeploymentStore.class).to(DeploymentStoreImpl.class).in(Singleton.class);

        // ------------------------------------------------
        // domain management application

        bindPresenter(InterfacePresenter.class,
                InterfacePresenter.MyView.class,
                InterfaceView.class,
                InterfacePresenter.MyProxy.class);

        bindPresenter(PropertiesPresenter.class,
                PropertiesPresenter.MyView.class,
                PropertiesView.class,
                PropertiesPresenter.MyProxy.class);

        bindPresenter(HostPropertiesPresenter.class,
                HostPropertiesPresenter.MyView.class,
                HostPropertiesView.class,
                HostPropertiesPresenter.MyProxy.class);

        bindPresenter(HostInterfacesPresenter.class,
                HostInterfacesPresenter.MyView.class,
                HostInterfacesView.class,
                HostInterfacesPresenter.MyProxy.class);

        bindPresenter(HostJVMPresenter.class,
                HostJVMPresenter.MyView.class,
                HostJVMView.class,
                HostJVMPresenter.MyProxy.class);

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

        bind(CurrentProfileSelection.class).in(Singleton.class);
        bind(CurrentHostSelection.class).in(Singleton.class);
        bind(CurrentServerSelection.class).in(Singleton.class);
        bind(ReloadState.class).in(Singleton.class);

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

        bindPresenter(DataSourcePresenter.class,
                DataSourcePresenter.MyView.class,
                DatasourceView.class,
                DataSourcePresenter.MyProxy.class);

        bind(DataSourceStore.class).to(DataSourceStoreImpl.class).in(Singleton.class);
        bind(DomainDriverStrategy.class).in(Singleton.class);
        bind(StandaloneDriverStrategy.class).in(Singleton.class);

        bindPresenter(EJB3Presenter.class,
                EJB3Presenter.MyView.class,
                EJB3View.class,
                EJB3Presenter.MyProxy.class);

        bindPresenter(MessagingPresenter.class,
                MessagingPresenter.MyView.class,
                MessagingView.class,
                MessagingPresenter.MyProxy.class);

        bindPresenter(LoggingPresenter.class,
                LoggingPresenter.MyView.class,
                LoggingView.class,
                LoggingPresenter.MyProxy.class);

        bind(HandlerListManager.class).in(Singleton.class);

        bindPresenter(ScannerPresenter.class,
                ScannerPresenter.MyView.class,
                ScannerView.class,
                ScannerPresenter.MyProxy.class);

        bindPresenter(ThreadsPresenter.class,
                ThreadsPresenter.MyView.class,
                ThreadsView.class,
                ThreadsPresenter.MyProxy.class);

        bindPresenter(ConfigAdminPresenter.class,
                ConfigAdminPresenter.MyView.class,
                ConfigAdminView.class,
                ConfigAdminPresenter.MyProxy.class);

        bindPresenter(OSGiConfigurationPresenter.class,
                OSGiConfigurationPresenter.MyView.class,
                OSGiSubsystemView.class,
                OSGiConfigurationPresenter.MyProxy.class);

        bindPresenter(OSGiRuntimePresenter.class,
                OSGiRuntimePresenter.MyView.class,
                OSGiRuntimeView.class,
                OSGiRuntimePresenter.MyProxy.class);

        // Infinispan
        bindPresenter(CacheContainerPresenter.class,
                CacheContainerPresenter.MyView.class,
                CacheContainerView.class,
                CacheContainerPresenter.MyProxy.class);
        bindPresenter(LocalCachePresenter.class,
                LocalCachePresenter.MyView.class,
                LocalCacheView.class,
                LocalCachePresenter.MyProxy.class);
        bindPresenter(InvalidationCachePresenter.class,
                InvalidationCachePresenter.MyView.class,
                InvalidationCacheView.class,
                InvalidationCachePresenter.MyProxy.class);
        bindPresenter(ReplicatedCachePresenter.class,
                ReplicatedCachePresenter.MyView.class,
                ReplicatedCacheView.class,
                ReplicatedCachePresenter.MyProxy.class);
        bindPresenter(DistributedCachePresenter.class,
                DistributedCachePresenter.MyView.class,
                DistributedCacheView.class,
                DistributedCachePresenter.MyProxy.class);


        bindPresenter(SocketBindingPresenter.class,
                SocketBindingPresenter.MyView.class,
                SocketBindingView.class,
                SocketBindingPresenter.MyProxy.class);

        bindPresenter(WebPresenter.class,
                WebPresenter.MyView.class,
                WebSubsystemView.class,
                WebPresenter.MyProxy.class);

        bindPresenter(StandaloneServerPresenter.class,
                StandaloneServerPresenter.MyView.class,
                StandaloneServerView.class,
                StandaloneServerPresenter.MyProxy.class);

        bindPresenter(WebServicePresenter.class,
                WebServicePresenter.MyView.class,
                WebServiceView.class,
                WebServicePresenter.MyProxy.class);

         bindPresenter(WebServiceRuntimePresenter.class,
                WebServiceRuntimePresenter.MyView.class,
                WebServiceRuntimeView.class,
                WebServiceRuntimePresenter.MyProxy.class);

        bind(EndpointRegistry.class).in(Singleton.class);
        bind(DomainEndpointStrategy.class).in(Singleton.class);
        bind(StandaloneEndpointStrategy.class).in(Singleton.class);

        bindPresenter(ResourceAdapterPresenter.class,
                ResourceAdapterPresenter.MyView.class,
                ResourceAdapterView.class,
                ResourceAdapterPresenter.MyProxy.class);

        bindPresenter(JndiPresenter.class,
                JndiPresenter.MyView.class,
                JndiView.class,
                JndiPresenter.MyProxy.class);

        bindPresenter(VMMetricsPresenter.class,
                VMMetricsPresenter.MyView.class,
                VMMetricsView.class,
                VMMetricsPresenter.MyProxy.class);

        bindPresenter(HostVMMetricPresenter.class,
                HostVMMetricPresenter.MyView.class,
                HostVMMetricView.class,
                HostVMMetricPresenter.MyProxy.class);

        bindPresenter(TransactionPresenter.class,
                TransactionPresenter.MyView.class,
                TransactionView.class,
                TransactionPresenter.MyProxy.class);

        bindPresenter(SecuritySubsystemPresenter.class,
                SecuritySubsystemPresenter.MyView.class,
                SecuritySubsystemView.class,
                SecuritySubsystemPresenter.MyProxy.class);

        bindPresenter(SecurityDomainsPresenter.class,
                SecurityDomainsPresenter.MyView.class,
                SecurityDomainsView.class,
                SecurityDomainsPresenter.MyProxy.class);

        bindPresenter(StandaloneRuntimePresenter.class,
                StandaloneRuntimePresenter.MyView.class,
                StandaloneRuntimeView.class,
                StandaloneRuntimePresenter.MyProxy.class);

        bindPresenter(DomainRuntimePresenter.class,
                DomainRuntimePresenter.MyView.class,
                DomainRuntimeView.class,
                DomainRuntimePresenter.MyProxy.class);

        bindPresenter(TXMetricPresenter.class,
                TXMetricPresenter.MyView.class,
                TXMetricViewImpl.class,
                TXMetricPresenter.MyProxy.class);

        bindPresenter(JpaPresenter.class,
                JpaPresenter.MyView.class,
                JpaView.class,
                JpaPresenter.MyProxy.class);

        bindPresenter(JacOrbPresenter.class,
                JacOrbPresenter.MyView.class,
                JacOrbView.class,
                JacOrbPresenter.MyProxy.class);

        bindPresenter(MailPresenter.class,
                MailPresenter.MyView.class,
                MailSubsystemView.class,
                MailPresenter.MyProxy.class);

        bindPresenter(JMXPresenter.class,
                JMXPresenter.MyView.class,
                JMXSubsystemView.class,
                JMXPresenter.MyProxy.class);

        bindPresenter(EEPresenter.class,
                EEPresenter.MyView.class,
                EESubsystemView.class,
                EEPresenter.MyProxy.class);

        bindPresenter(JcaPresenter.class,
                JcaPresenter.MyView.class,
                JcaSubsystemView.class,
                JcaPresenter.MyProxy.class);

        bindPresenter(WebMetricPresenter.class,
                WebMetricPresenter.MyView.class,
                WebMetricView.class,
                WebMetricPresenter.MyProxy.class);

        bindPresenter(JMSMetricPresenter.class,
                JMSMetricPresenter.MyView.class,
                JMSMetricView.class,
                JMSMetricPresenter.MyProxy.class);

         bindPresenter(DataSourceMetricPresenter.class,
                DataSourceMetricPresenter.MyView.class,
                DataSourceMetricView.class,
                DataSourceMetricPresenter.MyProxy.class);

        bindPresenter(JPAMetricPresenter.class,
                JPAMetricPresenter.MyView.class,
                JPAMetricsView.class,
                JPAMetricPresenter.MyProxy.class);

        bindPresenter(JGroupsPresenter.class,
                JGroupsPresenter.MyView.class,
                JGroupsSubsystemView.class,
                JGroupsPresenter.MyProxy.class);


    }

}

