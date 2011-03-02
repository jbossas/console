package org.jboss.as.console.client.core;

/**
 * @author Heiko Braun
 * @date 2/4/11
 */
public class NameTokens {

    public static final String mainLayout = "main";

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

    public static final String deploymentTool = "server-deployments";
    public static String getDeploymentTool() {
        return deploymentTool;
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

    public final static String PropertyToolPresenter = "server-properties";
    public static String getPropertyToolPresenter() {
        return PropertyToolPresenter;
    }

    public final static String SocketToolPresenter = "server-sockets";
    public static String getSocketToolPresenter() {
        return SocketToolPresenter;
    }

    public final static String SubsystemToolPresenter = "subsys";
    public static String getSubsystemToolPresenter() {
        return SubsystemToolPresenter;
    }

    public static final String ThreadManagementPresenter = "threads";
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
}
