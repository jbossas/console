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

package org.jboss.as.console.client.core;

/**
 * @author Heiko Braun
 * @date 2/4/11
 */
public class NameTokens {

    public static final String mainLayout = "main";

    public static final String DataSourcePresenter = "datasources";
    public static final String DebugToolsPresenter = "debug-tools";
    public static final String HostInterfacesPresenter = "host-interfaces";
    public static final String HostJVMPresenter = "host-jvms";
    public static final String HostPropertiesPresenter = "host-properties";
    public static final String InterfacePresenter = "interfaces";
    public static final String JMSPresenter = "jms";
    public static final String JndiPresenter = "naming";
    public static final String LoggingPresenter = "logging";
    public static final String MessageDrivenBeanPresenter = "mdb";
    public static final String MessagingPresenter = "messaging";
    public static final String MetricsPresenter = "invocation-metrics";
    public static final String ModelBrowserPresenter = "model-browser";
    public static final String OSGiPresenter = "osgi";
    public static final String PropertiesPresenter = "properties";
    public static final String ResourceAdapterPresenter ="resource-adapters";
    public static final String SettingsPresenter = "settings";
    public static final String SessionBeanPresenter = "session-bean";
    public static final String SocketBindingPresenter = "socket-bindings";
    public static final String StandaloneServerPresenter = "standalone-server";
    public static final String WebPresenter = "web";
    public static final String WebServicePresenter = "webservices";


    public static String getMainLayout() {
        return mainLayout;
    }

    public static final String signInPage = "login";
    public static String getSignInPage() {
        return signInPage;
    }

    public static final String errorPage = "err";
    public static String getErrorPage() {
        return errorPage;
    }

    public static final String serverConfig = "server";
    public static String getServerConfig() {
        return serverConfig;
    }

    public static final String StandloneDeployments = "server-deployments";
    public static String getStandloneDeployments() {
        return StandloneDeployments;
    }

    public static final String systemApp = "system";
    public static String getSystemApp() {
        return systemApp;
    }

    public final static String InterfaceToolPresenter = "server-interfaces";
    public static String getInterfaceToolPresenter() {
        return InterfaceToolPresenter;
    }

    public final static String PathToolPresenter = "server-paths";
    public static String getPathToolPresenter() {
        return PathToolPresenter;
    }

    public final static String SubsystemToolPresenter = "subsys";
    public static String getSubsystemToolPresenter() {
        return SubsystemToolPresenter;
    }

    public static final String ThreadManagementPresenter = "threading";
    public static String getThreadManagementPresenter() {
        return ThreadManagementPresenter;
    }


    // ------------------------------------------------------
    // domain tokens below

    public static final String ProfileMgmtPresenter = "profiles";
    public static String getProfileMgmtPresenter() {
        return ProfileMgmtPresenter;
    }

    public static final String ProfileOverviewPresenter = "profile-overview";
    public static String getProfileOverviewPresenter() {
        return ProfileOverviewPresenter;
    }

    public static final String ServerGroupPresenter = "server-group";
    public static String getServerGroupPresenter() {
        return ServerGroupPresenter;
    }

    public static final String ServerGroupMgmtPresenter = "server-groups";
    public static String getServerGroupMgmtPresenter() {
        return ServerGroupMgmtPresenter;
    }

    public static final String DeploymentsPresenter  = "domain-deployments";
    public static String getDeploymentsPresenter() {
        return DeploymentsPresenter;
    }

    public static final String HostMgmtPresenter = "hosts";
    public static String getHostMgmtPresenter() {
        return HostMgmtPresenter;
    }

    public final static String ServerPresenter = "server-config";
    public static String getServerPresenter() {
        return ServerPresenter;
    }

    public static final String InstancesPresenter = "server-instances";
    public static String getInstancesPresenter() {
        return InstancesPresenter;
    }

    public static final String DeploymentListPresenter = "deployment-list";
    public static String getDeploymentListPresenter() {
        return DeploymentListPresenter;
    }
}
