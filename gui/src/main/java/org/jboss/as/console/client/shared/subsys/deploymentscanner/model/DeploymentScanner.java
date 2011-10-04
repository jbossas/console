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
package org.jboss.as.console.client.shared.subsys.deploymentscanner.model;

import org.jboss.as.console.client.shared.viewframework.EnabledEntity;
import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * Model for a Deployment Scanner
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
@Address("/subsystem=deployment-scanner/scanner={0}")
public interface DeploymentScanner extends NamedEntity, EnabledEntity {
    
    @Override
    @Binding(detypedName="name", key=true)
    @FormItem(defaultValue="",
              localLabel="common_label_name",
              required=true,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="TEXT_BOX")
    public String getName();
    @Override
    public void setName(String name);
    
    @Override
    @Binding(detypedName="scan-enabled")
    @FormItem(defaultValue="false",
            localLabel="common_label_enabled",
            required=true,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX")
    public boolean isEnabled();
    @Override
    public void setEnabled(boolean isEnabled);
    
   @Binding(detypedName="path")
   @FormItem(defaultValue="deployments",
            localLabel="common_label_path",
            required=true,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX")
   String getPath();
   void setPath(String path);
   
   @Binding(detypedName="relative-to")
   @FormItem(defaultValue="jboss.server.base.dir",
            localLabel="subsys_deploymentscanner_relativeTo",
            required=false,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX")
   String getRelativeTo();
   void setRelativeTo(String relativeTo);
   
   @Binding(detypedName="scan-interval")
   @FormItem(defaultValue="5000",
            localLabel="subsys_deploymentscanner_scanInterval",
            required=false,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX")
   int getScanInterval();
   void setScanInterval(int scanInterval);
   
   @Binding(detypedName="auto-deploy-zipped")
   @FormItem(defaultValue="true",
            localLabel="subsys_deploymentscanner_autoDeployZipped",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX")
   boolean isAutoDeployZipped();
   void setAutoDeployZipped(boolean autoDeployZipped);
   
   @Binding(detypedName="auto-deploy-exploded")
   @FormItem(defaultValue="false",
            localLabel="subsys_deploymentscanner_autoDeployExploded",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX")
   boolean isAutoDeployExploded();
   void setAutoDeployExploded(boolean autoDeployExploded);
   
   @Binding(detypedName="deployment-timeout")
   @FormItem(defaultValue="60",
            localLabel="subsys_deploymentscanner_deploymentTimeout",
            required=false,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX")
   long getDeploymentTimeout();
   void setDeploymentTimeout(long deploymentTimeout);
}
