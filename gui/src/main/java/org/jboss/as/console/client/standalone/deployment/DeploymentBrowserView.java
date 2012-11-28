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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.deployment.DeploymentCommandDelegate;
import org.jboss.as.console.client.shared.deployment.DeploymentFilter;
import org.jboss.as.console.client.shared.deployment.DeploymentStore;
import org.jboss.as.console.client.shared.deployment.model.DeployedEjb;
import org.jboss.as.console.client.shared.deployment.model.DeployedEndpoint;
import org.jboss.as.console.client.shared.deployment.model.DeployedPersistenceUnit;
import org.jboss.as.console.client.shared.deployment.model.DeployedServlet;
import org.jboss.as.console.client.shared.deployment.model.DeploymentData;
import org.jboss.as.console.client.shared.deployment.model.DeploymentJpaSubsystem;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.deployment.model.DeploymentWebSubsystem;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.as.console.client.widgets.browser.DefaultCellBrowser;
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
    private DeploymentFilter filter;
    private DeploymentStore deploymentStore;
    private DeploymentBrowserPresenter presenter;
    private DeploymentTreeModel deploymentTreeModel;
    private DeckPanel contextPanel;
    private Map<String, Form<DeploymentData>> forms;
    private Map<String, Integer> indexes;


    @Inject
    public DeploymentBrowserView(final DeploymentStore deploymentStore)
    {
        this.deploymentStore = deploymentStore;
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

        ListDataProvider<DeploymentRecord> dataProvider = new ListDataProvider<DeploymentRecord>(keyProvider);
        filter = new DeploymentFilter(dataProvider);
        toolStrip.addToolWidget(filter.asWidget());

        int index = 1;
        contextPanel = new DeckPanel();
        contextPanel.add(new Label("No information available."));

        addContextForm(DeploymentRecord.class, index++,
                new TextAreaItem("name", "Name"),
                new TextAreaItem("path", "Path"),
                new TextAreaItem("runtimeName", "Runtime Name"),
                new TextBoxItem("relativeTo", "Relative To"));

        addContextForm(DeploymentJpaSubsystem.class, index++,
                new TextAreaItem("name", "Name"),
                new TextBoxItem("defaultDataSource", "Default Datasource"),
                new TextBoxItem("defaultInheritance", "Default Inheritance"),
                new CheckBoxItem("defaultVfs", "Default VFS"));

        // TODO Add link for context-root
        addContextForm(DeploymentWebSubsystem.class, index++,
                new TextAreaItem("name", "Name"),
                new TextBoxItem("contextRoot", "Context Root"),
                new NumberBoxItem("maxActiveSessions", "Max Active Sessions"),
                new TextBoxItem("virtualHost", "Virtual Host"));

        addContextForm(DeployedEjb.class, index++,
                new TextAreaItem("name", "Name"),
                new TextBoxItem("componentClassname", "Classname"),
                new ListItem("declaredRoles", "Declared Roles"),
                new TextBoxItem("runAsRole", "Run As Role"),
                new TextBoxItem("securityDomain", "Security Domain"));

        addContextForm(DeployedPersistenceUnit.class, index++,
                new TextAreaItem("name", "Name"));

        addContextForm(DeployedServlet.class, index++,
                new TextAreaItem("name", "Name"));

        addContextForm(DeployedEndpoint.class, index++,
                new TextAreaItem("name", "Name"));

        contextPanel.showWidget(0);

        deploymentTreeModel = new DeploymentTreeModel(presenter, deploymentStore);
        DefaultCellBrowser cellBrowser = new DefaultCellBrowser.Builder(deploymentTreeModel, null).build();

        OneToOneLayout layout = new OneToOneLayout()
                .setTitle(Console.CONSTANTS.common_label_deployments())
                .setHeadline(Console.CONSTANTS.common_label_deployments())
                .setDescription("Currently deployed application components.")
                .setMaster(Console.MESSAGES.available("Deployments"), cellBrowser)
                .setMasterTools(toolStrip)
                .setDetail("Properties", contextPanel);
        return layout.build();
    }

    private <T extends DeploymentData> void addContextForm(Class<T> clazz, int index, FormItem... formItems)
    {
        Form<T> form = new Form<T>(clazz);
        form.setNumColumns(2);
        form.setEnabled(false);
        form.setFields(formItems);

        forms.put(clazz.getName(), (Form<DeploymentData>) form);
        indexes.put(clazz.getName(), index);
        contextPanel.add(form.asWidget());
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
        AutoBean<T> autoBean = AutoBeanUtils.getAutoBean(selectedContext);
        String classname = autoBean.getType().getName();
        Form<DeploymentData> form = forms.get(classname);
        Integer index = indexes.get(classname);
        if (form != null && index != null && index > 0 && index < contextPanel.getWidgetCount())
        {
            form.edit(selectedContext);
            contextPanel.showWidget(index);
        }
        else
        {
            contextPanel.showWidget(0);
        }
    }
}
