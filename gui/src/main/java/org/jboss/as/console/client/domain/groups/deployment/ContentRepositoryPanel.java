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
package org.jboss.as.console.client.domain.groups.deployment;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.deployment.DeploymentCommandDelegate;
import org.jboss.as.console.client.shared.deployment.DeploymentDataKeyProvider;
import org.jboss.as.console.client.shared.deployment.DeploymentFilter;
import org.jboss.as.console.client.shared.deployment.DeploymentNameColumn;
import org.jboss.as.console.client.shared.deployment.model.ContentRepository;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.ListItem;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Harald Pehl
 * @date 12/12/2012
 */
public class ContentRepositoryPanel implements IsWidget
{
    private final Widget widget;
    private final DomainDeploymentPresenter presenter;
    private DeploymentFilter filter;
    private DefaultCellTable<DeploymentRecord> deploymentsTable;
    private ListDataProvider<DeploymentRecord> deploymentData;
    private SingleSelectionModel<DeploymentRecord> deploymentSelection;
    private ContentRepository contentRepository;

    public ContentRepositoryPanel(DomainDeploymentPresenter presenter)
    {
        this.presenter = presenter;
        this.widget = initUI();
    }

    @SuppressWarnings("unchecked")
    private Widget initUI()
    {
        String[] columnHeaders = new String[]{Console.CONSTANTS.common_label_name(),
                Console.CONSTANTS.common_label_runtimeName()};
        List<Column> columns = makeNameAndRuntimeColumns();

        DeploymentDataKeyProvider<DeploymentRecord> keyProvider = new DeploymentDataKeyProvider<DeploymentRecord>();
        deploymentsTable = new DefaultCellTable<DeploymentRecord>(8, keyProvider);
        for (int i = 0; i < columnHeaders.length; i++)
        {
            deploymentsTable.addColumn(columns.get(i), columnHeaders[i]);
        }
        deploymentsTable.addColumn(new TextColumn<DeploymentRecord>()
        {
            @Override
            public String getValue(DeploymentRecord deployment)
            {
                return String.valueOf(contentRepository.getAssignments(deployment));
            }
        }, "Assignments");

        deploymentSelection = new SingleSelectionModel<DeploymentRecord>(keyProvider);
        deploymentsTable.setSelectionModel(deploymentSelection);

        deploymentData = new ListDataProvider<DeploymentRecord>();
        deploymentData.addDataDisplay(deploymentsTable);

        SafeHtmlBuilder tableFooter = new SafeHtmlBuilder();
        tableFooter.appendHtmlConstant("<span style='font-size:10px;color:#A7ABB4;'>[1] File System Deployment</span>");

        Form<DeploymentRecord> form = new Form<DeploymentRecord>(DeploymentRecord.class);
        form.setNumColumns(2);
        form.setEnabled(true);
        TextAreaItem name = new TextAreaItem("name", "Name");
        TextAreaItem runtimeName = new TextAreaItem("runtimeName", "Runtime Name");
        final ListItem groups = new ListItem("assignments", "Assigned Groups");
        form.setFields(name, runtimeName, groups);
        form.bind(deploymentsTable);

        deploymentSelection.addSelectionChangeHandler(
                new SelectionChangeEvent.Handler()
                {
                    @Override
                    public void onSelectionChange(SelectionChangeEvent event)
                    {
                        DeploymentRecord selection = deploymentSelection.getSelectedObject();
                        if (selection != null)
                        {
                            List<String> serverGroups = contentRepository.getServerGroups(selection);
                            groups.setValue(serverGroups);
                        }
                    }
                });

        final ToolStrip toolStrip = new ToolStrip();
        filter = new DeploymentFilter(deploymentData);
        toolStrip.addToolWidget(filter.asWidget());

        ToolButton addContentBtn = new ToolButton("Add", new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                presenter.launchNewDeploymentDialoge(null, false);
            }
        });
        addContentBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_addContent_deploymentsOverview());
        toolStrip.addToolButtonRight(addContentBtn);

        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_remove(),
                new ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent clickEvent)
                    {
                        final DeploymentRecord selection = deploymentSelection.getSelectedObject();
                        if (selection != null)
                        {
                            new DeploymentCommandDelegate(ContentRepositoryPanel.this.presenter,
                                    DeploymentCommand.REMOVE_FROM_DOMAIN).execute(selection);
                        }
                    }
                }));

        toolStrip.addToolButtonRight(new ToolButton("Assign",
                new ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent clickEvent)
                    {
                        final DeploymentRecord selection = deploymentSelection.getSelectedObject();
                        if (selection != null)
                        {
                            new DeploymentCommandDelegate(ContentRepositoryPanel.this.presenter,
                                    DeploymentCommand.ADD_TO_GROUP).execute(selection);
                        }
                    }
                }));

        toolStrip.addToolButtonRight(new ToolButton("Replace",
                new ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent clickEvent)
                    {

                        final DeploymentRecord selection = deploymentSelection.getSelectedObject();
                        if (selection != null)
                        {
                            new DeploymentCommandDelegate(ContentRepositoryPanel.this.presenter,
                                    DeploymentCommand.UPDATE_CONTENT).execute(selection);
                        }
                    }
                }));

        Form<DeploymentRecord> form2 = new Form<DeploymentRecord>(DeploymentRecord.class);
        form2.setNumColumns(2);
        form2.setEnabled(true);
        TextAreaItem path = new TextAreaItem("path", "Path");
        TextBoxItem relative = new TextBoxItem("relativeTo", "Relative To");
        form2.setFields(path, relative);
        form2.bind(deploymentsTable);

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadline(Console.CONSTANTS.common_label_contentRepository())
                .setMaster(Console.MESSAGES.available("Deployment Content"), deploymentsTable)
                .setMasterTools(toolStrip)
                .setMasterFooter(new HTML(tableFooter.toSafeHtml()))
                .setDescription(
                        "The content repository contains all deployed content. Contents need to be assigned to sever groups in order to become effective.")
                .addDetail(Console.CONSTANTS.common_label_attributes(), form.asWidget())
                .addDetail("Path", form2.asWidget());
        return layout.build();
    }

    @Override
    public void updateDeploymentInfo(DomainDeploymentInfo domainDeploymentInfo, DeploymentRecord... targets) {

        List<ServerGroupRecord> serverGroups = this.presenter.getServerGroups();

        this.groupOverview.setGroups(serverGroups);
        this.groupOverview.setGroupDeployments(domainDeploymentInfo.getServerGroupDeployments());

        currentAssignments = matchAssignments(domainDeploymentInfo);

        domainDeploymentProvider.setList(domainDeploymentInfo.getDomainDeployments());
        contentTable.selectDefaultEntity();

        filter.reset(true);
    }

    private List<Column> makeNameAndRuntimeColumns()
    {
        List<Column> columns = new ArrayList<Column>(2);
        columns.add(new DeploymentNameColumn());

        TextColumn<DeploymentRecord> dplRuntimeColumn = new TextColumn<DeploymentRecord>()
        {
            @Override
            public String getValue(DeploymentRecord record)
            {
                String title;
                if (record.getRuntimeName().length() > 27)
                {
                    title = record.getRuntimeName().substring(0, 26) + "...";
                }
                else
                {
                    title = record.getRuntimeName();
                }
                return title;
            }
        };
        columns.add(dplRuntimeColumn);
        return columns;
    }

    @Override
    public Widget asWidget()
    {
        return widget;
    }

    void updateContentRepository(final ContentRepository contentRepository)
    {
        this.contentRepository = contentRepository;
        this.deploymentData.setList(contentRepository.getDeployments());
        this.deploymentsTable.selectDefaultEntity();
        this.filter.reset(true);
    }
}

