package org.jboss.as.console.client.shared;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;

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

        DeploymentRecord dpl2 = factory.deployment().as();
        dpl2.setName("backOfficeApp.war");
        dpl2.setRuntimeName("BackOffice.war");
        dpl2.setSha("ed849ee1 ...");
        
        DeploymentRecord dpl3 = factory.deployment().as();
        dpl3.setName("mon-1.0.war");
        dpl3.setRuntimeName("monitor.war");
        dpl3.setSha("7a2d28fc ...");

        records.add(dpl1);
        records.add(dpl2);
        records.add(dpl3);

        Log.debug("Loaded " + records.size() +" deployment records");
        return records;
    }
}
