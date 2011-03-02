package org.jboss.as.console.client.shared;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import org.jboss.as.console.client.domain.model.MockServerGroupStore;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/31/11
 */
public class MockDeploymentStoreImpl implements DeploymentStore {

    BeanFactory factory = GWT.create(BeanFactory.class);

    @Override
    public List<DeploymentRecord> loadDeployments() {
                
        List<DeploymentRecord> records = new ArrayList<DeploymentRecord>();

        DeploymentRecord dpl1 = factory.deployment().as();
        dpl1.setName("ols.war");
        dpl1.setRuntimeName("onlineStore.war");
        dpl1.setSha("2fd4e1c6 ...");
        dpl1.setServerGroup(MockServerGroupStore.PRODUCTION_SERVERS);

        DeploymentRecord dpl2 = factory.deployment().as();
        dpl2.setName("backOfficeApp.war");
        dpl2.setRuntimeName("BackOffice.war");
        dpl2.setSha("ed849ee1 ...");
        dpl2.setServerGroup(MockServerGroupStore.PRODUCTION_SERVERS);

        DeploymentRecord dpl3 = factory.deployment().as();
        dpl3.setName("mon-1.0.war");
        dpl3.setRuntimeName("monitor.war");
        dpl3.setSha("7a2d28fc ...");
        dpl3.setServerGroup(MockServerGroupStore.B2B_SERVICES);
        
        DeploymentRecord dpl4 = factory.deployment().as();
        dpl4.setName("spectest.war");
        dpl4.setRuntimeName("spectest.war");
        dpl4.setSha("7a2d28fc ...");
        dpl4.setServerGroup(MockServerGroupStore.DEVELOPMENT_ENVIRONMENT);

        DeploymentRecord dpl45 = factory.deployment().as();
        dpl45.setName("postfix.rar");
        dpl45.setRuntimeName("postfix.rar");
        dpl45.setSha("562d28fc ...");
        dpl45.setServerGroup(MockServerGroupStore.PRODUCTION_SERVERS);

        DeploymentRecord dpl5 = factory.deployment().as();
        dpl5.setName("sys-1.0.war");
        dpl5.setRuntimeName("system.war");
        dpl5.setSha("782d28fc ...");
        dpl5.setServerGroup(MockServerGroupStore.DEVELOPMENT_ENVIRONMENT);
        
        
        DeploymentRecord dpl6 = factory.deployment().as();
        dpl6.setName("app-2.1.ear");
        dpl6.setRuntimeName("app.ear");
        dpl6.setSha("232sd28fc ...");
        dpl6.setServerGroup(MockServerGroupStore.B2B_SERVICES);
        
        DeploymentRecord dpl7 = factory.deployment().as();
        dpl7.setName("b2b.rar");
        dpl7.setRuntimeName("connector.rar");
        dpl7.setSha("5672dasasfc ...");
        dpl7.setServerGroup(MockServerGroupStore.B2B_SERVICES);
        
        
        DeploymentRecord dpl8 = factory.deployment().as();
        dpl8.setName("all-apps.ear");
        dpl8.setRuntimeName("everything-we-own.ear");
        dpl8.setSha("7a2d234fc ...");
        dpl8.setServerGroup(MockServerGroupStore.PRODUCTION_SERVERS);
        
        DeploymentRecord dpl9 = factory.deployment().as();
        dpl9.setName("3rd-party.rar");
        dpl9.setRuntimeName("3rd-party-connector.rar");
        dpl9.setSha("7a2d345fc ...");
        dpl9.setServerGroup(MockServerGroupStore.B2B_SERVICES);

        records.add(dpl1);
        records.add(dpl2);
        records.add(dpl3);
        records.add(dpl4);
        records.add(dpl45);
        records.add(dpl5);
        records.add(dpl6);
        records.add(dpl7);
        records.add(dpl8);
        records.add(dpl9);

        Log.debug("Loaded " + records.size() +" deployment records");
        return records;
    }
}
