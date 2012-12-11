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
package org.jboss.as.console.client.shared.deployment;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import org.jboss.as.console.client.shared.deployment.model.DeployedEjb;
import org.jboss.as.console.client.shared.deployment.model.DeployedEndpoint;
import org.jboss.as.console.client.shared.deployment.model.DeployedPersistenceUnit;
import org.jboss.as.console.client.shared.deployment.model.DeployedServlet;
import org.jboss.as.console.client.shared.deployment.model.DeploymentData;
import org.jboss.as.console.client.shared.deployment.model.DeploymentEjbSubsystem;
import org.jboss.as.console.client.shared.deployment.model.DeploymentJpaSubsystem;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.deployment.model.DeploymentWebSubsystem;
import org.jboss.as.console.client.shared.deployment.model.DeploymentWebserviceSubsystem;
import org.jboss.as.console.client.widgets.browser.DefaultCellBrowser;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.ListItem;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Harald Pehl
 * @date 12/04/2012
 */
public class DeploymentBrowser
{
    private final DeploymentStore deploymentStore;
    private final SingleSelectionModel<DeploymentRecord> selectionModel;
    private final DeploymentTreeModel deploymentTreeModel;
    private final DefaultCellBrowser cellBrowser;
    private final DeploymentBreadcrumb breadcrumb;
    private final DeckPanel contextPanel;
    private final Map<String, Form<DeploymentData>> forms;
    private final Map<String, Integer> indexes;


    public DeploymentBrowser(final DeploymentStore deploymentStore, final SingleSelectionModel<DeploymentRecord> selectionModel)
    {
        this.deploymentStore = deploymentStore;
        this.selectionModel = selectionModel;

        forms = new HashMap<String, Form<DeploymentData>>();
        indexes = new HashMap<String, Integer>();

        deploymentTreeModel = new DeploymentTreeModel(this, deploymentStore, selectionModel);
        cellBrowser = new DefaultCellBrowser.Builder(deploymentTreeModel, null).build();

        breadcrumb = new DeploymentBreadcrumb();
        breadcrumb.getElement().setAttribute("style", "margin-top:30px;");

        int index = 0;
        this.contextPanel = new DeckPanel();
        addContext(DeploymentRecord.class, index++,
                new TextAreaItem("name", "Name"),
                new TextAreaItem("path", "Path"),
                new TextAreaItem("runtimeName", "Runtime Name"),
                new TextBoxItem("relativeTo", "Relative To"));

        addContext(DeploymentEjbSubsystem.class, index++);

        addContext(DeploymentJpaSubsystem.class, index++,
                new TextAreaItem("name", "Name"),
                new TextBoxItem("defaultDataSource", "Default Datasource"),
                new TextBoxItem("defaultInheritance", "Default Inheritance"),
                new CheckBoxItem("defaultVfs", "Default VFS"));

        addContext(DeploymentWebSubsystem.class, index++,
                new TextAreaItem("name", "Name"),
                new TextBoxItem("contextRoot", "Context Root"),
                new TextBoxItem("virtualHost", "Virtual Host"));

        addContext(DeploymentWebserviceSubsystem.class, index++);

        addContext(DeployedEjb.class, index++,
                new TextAreaItem("name", "Name"),
                new TextBoxItem("componentClassname", "Component Classname"),
                new ListItem("declaredRoles", "Declared Roles"),
                new TextBoxItem("runAsRole", "Run As Role"),
                new TextBoxItem("securityDomain", "Security Domain"));

        addContext(DeployedPersistenceUnit.class, index++,
                new TextAreaItem("name", "Name"),
                new CheckBoxItem("enabled", "Enabled"),
                new ListItem("entities", "Entities"));

        addContext(DeployedServlet.class, index++,
                new TextAreaItem("name", "Name"),
                new TextBoxItem("servletClass", "Servlet Class"));

        addContext(DeployedEndpoint.class, index++,
                new TextAreaItem("name", "Name"),
                new TextBoxItem("classname", "Classname"),
                new TextBoxItem("context", "Context"),
                new TextBoxItem("endpointType", "Type"),
                new TextBoxItem("wsdl", "WSDL"));
    }

    private <T extends DeploymentData> void addContext(Class<T> clazz, int index, FormItem... formItems)
    {
        Widget widget;
        String classname = clazz.getName();
        if (formItems != null && formItems.length != 0)
        {
            Form<T> form = new Form<T>(clazz);
            form.setNumColumns(1);
            form.setEnabled(false);
            form.setFields(formItems);
            forms.put(classname, (Form<DeploymentData>) form);
            widget = form.asWidget();
        }
        else
        {
            widget = new Label("No information available.");
            widget.getElement().addClassName("console-DeploymentBreadcrumb-noinfo");
        }
        widget.getElement().addClassName("console-DeploymentBreadcrumb-context");
        indexes.put(classname, index);
        contextPanel.add(widget);
    }

    public void updateDeployments(List<DeploymentRecord> deployments)
    {
        deploymentTreeModel.updateDeployments(deployments);
    }

    public <T extends DeploymentData> void updateContext(final T selectedContext)
    {
        breadcrumb.setDeploymentData(selectedContext);
        AutoBean<T> autoBean = AutoBeanUtils.getAutoBean(selectedContext);
        String classname = autoBean.getType().getName();
        Integer index = indexes.get(classname);
        if (index != null && index > -1 && index < contextPanel.getWidgetCount())
        {
            Form<DeploymentData> form = forms.get(classname);
            if (form != null)
            {
                form.edit(selectedContext);
            }
            contextPanel.showWidget(index);
        }
    }

    public DefaultCellBrowser getCellBrowser()
    {
        return cellBrowser;
    }

    public DeploymentBreadcrumb getBreadcrumb()
    {
        return breadcrumb;
    }

    public DeckPanel getContextPanel()
    {
        return contextPanel;
    }
}
