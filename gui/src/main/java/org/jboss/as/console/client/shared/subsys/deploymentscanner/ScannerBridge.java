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
package org.jboss.as.console.client.shared.subsys.deploymentscanner;

import com.google.gwt.autobean.shared.AutoBean;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.deploymentscanner.model.DeploymentScanner;
import org.jboss.as.console.client.shared.viewframework.EntityAttributes;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;

import org.jboss.as.console.client.shared.viewframework.AbstractEntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.SubsystemOpFactory;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

/**
 * Adapter for CRUD on Deployment Scanner
 * 
 * Implements the abstract methods that require a direct call to BeanFactory.deploymentScanner()
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class ScannerBridge extends AbstractEntityToDmrBridge<DeploymentScanner> {

    private BeanFactory beanFactory;
    
    public ScannerBridge(DispatchAsync dispatcher,
            FrameworkView view,
            EntityAttributes attributes,
            SubsystemOpFactory opFactory,
            BeanFactory beanFactory) {
        super(dispatcher, view, attributes, opFactory);
        
        this.beanFactory = beanFactory;
    }

    @Override
    public DeploymentScanner newEntity() {
        DeploymentScanner scanner = beanFactory.deploymentScanner().as();
        
        scanner.setName((String)attributes.findAttribute("name").getDefaultValue());
        scanner.setPath((String)attributes.findAttribute("path").getDefaultValue());
        scanner.setRelativeTo((String)attributes.findAttribute("relativeTo").getDefaultValue());
        scanner.setEnabled((Boolean)attributes.findAttribute("enabled").getDefaultValue());
        scanner.setScanInterval((Integer)attributes.findAttribute("scanInterval").getDefaultValue());
        scanner.setAutoDeployZipped((Boolean)(attributes.findAttribute("autoDeployZipped").getDefaultValue()));
        scanner.setAutoDeployExploded((Boolean)attributes.findAttribute("autoDeployExploded").getDefaultValue());
        scanner.setDeploymentTimeout((Long)attributes.findAttribute("deploymentTimeout").getDefaultValue());
        
        return scanner;
    }

    @Override
    public DeploymentScanner makeEntity(Property prop) {
        DeploymentScanner scanner = beanFactory.deploymentScanner().as();
        scanner.setName(prop.getName());
        ModelNode values = prop.getValue();

        scanner.setPath(values.get(attributes.findAttribute("path").getDmrName()).asString());
        scanner.setRelativeTo(values.get(attributes.findAttribute("relativeTo").getDmrName()).asString());
        scanner.setEnabled(values.get(attributes.findAttribute("enabled").getDmrName()).asBoolean());
        scanner.setScanInterval(values.get(attributes.findAttribute("scanInterval").getDmrName()).asInt());
        scanner.setAutoDeployZipped(values.get(attributes.findAttribute("autoDeployZipped").getDmrName()).asBoolean());
        scanner.setAutoDeployExploded(values.get(attributes.findAttribute("autoDeployExploded").getDmrName()).asBoolean());
        scanner.setDeploymentTimeout(values.get(attributes.findAttribute("deploymentTimeout").getDmrName()).asLong());

        return scanner;
    }
}
