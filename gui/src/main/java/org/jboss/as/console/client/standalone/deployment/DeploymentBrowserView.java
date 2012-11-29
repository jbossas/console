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

package org.jboss.as.console.client.standalone.deployment;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.EnumLabelLookup;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.deployment.DeploymentCommandDelegate;
import org.jboss.as.console.client.shared.deployment.DeploymentStore;
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
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.as.console.client.widgets.browser.DefaultCellBrowser;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.ListItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Heiko Braun
 * @author Stan Silvert
 * @date 3/14/11
 */
public class DeploymentBrowserView extends SuspendableViewImpl implements DeploymentBrowserPresenter.MyView
{
    private DeploymentStore deploymentStore;
    private DeploymentBrowserPresenter presenter;
    private DeploymentTreeModel deploymentTreeModel;
    private DeploymentBreadcrumb breadcrumb;
    private DeckPanel contextPanel;
    private Map<String, TabPanel> tabPanels;
    private Map<String, Form<DeploymentData>> forms;
    private Map<String, Integer> indexes;


    @Inject
    public DeploymentBrowserView(final DeploymentStore deploymentStore)
    {
        this.deploymentStore = deploymentStore;
        this.tabPanels = new HashMap<String, TabPanel>();
        this.forms = new HashMap<String, Form<DeploymentData>>();
        this.indexes = new HashMap<String, Integer>();
    }

    @Override
    public Widget createWidget()
    {
        DeploymentDataKeyProvider<DeploymentRecord> keyProvider = new DeploymentDataKeyProvider<DeploymentRecord>();
        final SingleSelectionModel<DeploymentRecord> selectionModel = new SingleSelectionModel<DeploymentRecord>(
                keyProvider);

        final ToolStrip toolStrip = new ToolStrip();
        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                presenter.launchNewDeploymentDialoge(null, false);
            }
        });
        addBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_deploymentListView());
        toolStrip.addToolButtonRight(addBtn);
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_remove(), new
                ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent clickEvent)
                    {
                        DeploymentRecord selection = selectionModel.getSelectedObject();
                        if (selection != null)
                        {
                            new DeploymentCommandDelegate(
                                    DeploymentBrowserView.this.presenter,
                                    DeploymentCommand.REMOVE_FROM_STANDALONE).execute(
                                    selection
                            );
                        }
                    }
                }));
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_enOrDisable(), new
                ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent clickEvent)
                    {
                        DeploymentRecord selection = selectionModel.getSelectedObject();
                        if (selection != null)
                        {
                            new DeploymentCommandDelegate(
                                    DeploymentBrowserView.this.presenter,
                                    DeploymentCommand.ENABLE_DISABLE).execute(
                                    selection
                            );
                        }
                    }
                }));
        toolStrip.addToolButtonRight(new ToolButton("Update", new
                ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent clickEvent)
                    {
                        DeploymentRecord selection = selectionModel.getSelectedObject();
                        if (selection != null)
                        {
                            new DeploymentCommandDelegate(
                                    DeploymentBrowserView.this.presenter,
                                    DeploymentCommand.UPDATE_CONTENT).execute(
                                    selection
                            );
                        }
                    }
                }));

        deploymentTreeModel = new DeploymentTreeModel(presenter, deploymentStore);
        DefaultCellBrowser cellBrowser = new DefaultCellBrowser.Builder(deploymentTreeModel, null).build();

        breadcrumb = new DeploymentBreadcrumb();
        breadcrumb.getElement().setAttribute("style", "margin-top:30px;");

        int index = 0;
        contextPanel = new DeckPanel();
        contextPanel.getElement().setAttribute("style", "margin-top:30px;");
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
                new NumberBoxItem("maxActiveSessions", "Max Active Sessions"),
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

        SimpleLayout layout = new SimpleLayout()
                .setTitle(Console.CONSTANTS.common_label_deployments())
                .setHeadline(Console.CONSTANTS.common_label_deployments())
                .setDescription("Currently deployed application components.")
                .addContent("title", new ContentGroupLabel(Console.MESSAGES.available("Deployments")))
                .addContent("tools", toolStrip)
                .addContent("browser", cellBrowser)
                .addContent("breadcrumb", breadcrumb)
                .addContent("context", contextPanel);
        return layout.build();
    }

    private <T extends DeploymentData> void addContext(Class<T> clazz, int index, FormItem... formItems)
    {
        String classname = clazz.getName();
        TabPanel tabPanel = new TabPanel();
        tabPanel.setStyleName("default-tabpanel");

        if (formItems != null && formItems.length != 0)
        {
            Form<T> form = new Form<T>(clazz);
            form.setNumColumns(1);
            form.setEnabled(false);
            form.setFields(formItems);
            tabPanel.add(form.asWidget(), classname);
            forms.put(classname, (Form<DeploymentData>) form);
        }
        else
        {
            tabPanel.add(new Label("No information available."), classname);
        }

        tabPanels.put(classname, tabPanel);
        indexes.put(classname, index);
        contextPanel.add(tabPanel);
    }

    @Override
    public void setPresenter(DeploymentBrowserPresenter presenter)
    {
        this.presenter = presenter;
    }

    @Override
    public void updateDeploymentInfo(List<DeploymentRecord> deployments)
    {
        deploymentTreeModel.updateDeployments(deployments);
    }

    @Override
    public <T extends DeploymentData> void updateContext(final T selectedContext)
    {
        breadcrumb.setDeploymentData(selectedContext);

        AutoBean<T> autoBean = AutoBeanUtils.getAutoBean(selectedContext);
        String classname = autoBean.getType().getName();
        TabPanel tabPanel = tabPanels.get(classname);
        Integer index = indexes.get(classname);
        if (tabPanel != null && index != null && index > -1 && index < contextPanel.getWidgetCount())
        {
            tabPanel.selectTab(0);
            String tabTitle = EnumLabelLookup.labelFor(selectedContext.getType());
            tabPanel.getTabBar().setTabText(0, tabTitle);
            Form<DeploymentData> form = forms.get(classname);
            if (form != null)
            {
                form.edit(selectedContext);
            }
            contextPanel.showWidget(index);
        }
    }
}
