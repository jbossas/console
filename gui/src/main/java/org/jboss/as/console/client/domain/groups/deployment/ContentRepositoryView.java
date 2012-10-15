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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.deployment.DeploymentCommandDelegate;
import org.jboss.as.console.client.shared.deployment.DeploymentFilter;
import org.jboss.as.console.client.shared.deployment.TitleColumn;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;
import org.jboss.ballroom.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.ListItem;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 * @date 3/1/11
 */
public class ContentRepositoryView extends SuspendableViewImpl implements DeploymentsPresenter.MyView {

    private DeploymentsPresenter presenter;

    private ListDataProvider<DeploymentRecord> domainDeploymentProvider = new ListDataProvider<DeploymentRecord>();

    private GroupDeploymentsOverview groupOverview;
    private Map<String,List<String>> currentAssignments = new HashMap<String, List<String>>();
    private DefaultCellTable<DeploymentRecord> contentTable;
    private DeploymentFilter filter;

    @Override
    public void setPresenter(DeploymentsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        DefaultTabLayoutPanel tabLayoutPanel = new DefaultTabLayoutPanel(40, Style.Unit.PX);
        tabLayoutPanel.addStyleName("default-tabpanel");

        groupOverview = new GroupDeploymentsOverview(presenter);

        tabLayoutPanel.add(makeDeploymentsPanel(), "Content Repository", true);
        tabLayoutPanel.add(groupOverview.asWidget(), "Server Groups", true);

        tabLayoutPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                if(event.getItem()==1)
                {
                    groupOverview.resetPages();
                }
            }
        });
        return tabLayoutPanel;
    }

    private Widget makeDeploymentsPanel() {

        String[] columnHeaders = new String[]{Console.CONSTANTS.common_label_name(),
                Console.CONSTANTS.common_label_runtimeName()};
        List<Column> columns = makeNameAndRuntimeColumns();

        contentTable = new DefaultCellTable<DeploymentRecord>(8, new ProvidesKey<DeploymentRecord>() {
            @Override
            public Object getKey(DeploymentRecord deploymentRecord) {
                return deploymentRecord.getName();
            }
        });
        final SingleSelectionModel<DeploymentRecord> selectionModel = new SingleSelectionModel<DeploymentRecord>();
        contentTable.setSelectionModel(selectionModel);

        domainDeploymentProvider.addDataDisplay(contentTable);

        for (int i = 0; i < columnHeaders.length; i++) {
            contentTable.addColumn(columns.get(i), columnHeaders[i]);
        }

        contentTable.addColumn(new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord deployment) {
                return String.valueOf(currentAssignments.get(deployment.getName()).size());
            }
        }, "Assignments");

        Form<DeploymentRecord> form = new Form<DeploymentRecord>(DeploymentRecord.class);
        form.setNumColumns(2);
        form.setEnabled(true);
        TextAreaItem name = new TextAreaItem("name", "Name");
        TextAreaItem runtimeName = new TextAreaItem("runtimeName", "Runtime Name");
        final ListItem groups = new ListItem("assignments", "Assigned Groups");

        form.setFields(name, runtimeName, groups);

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                DeploymentRecord selection = selectionModel.getSelectedObject();
                if(selection!=null)
                {
                    groups.setValue(currentAssignments.get(selection.getName()));
                }
            }
        });
        form.bind(contentTable);

        // ---

        final ToolStrip toolStrip = new ToolStrip();

        filter = new DeploymentFilter(domainDeploymentProvider);
        toolStrip.addToolWidget(filter.asWidget());

        // ---

        ToolButton addContentBtn = new ToolButton("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewDeploymentDialoge(null, false);
            }
        });
        addContentBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_addContent_deploymentsOverview());
        toolStrip.addToolButtonRight(addContentBtn);

        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_remove()
                , new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                final DeploymentRecord selection = selectionModel.getSelectedObject();
                if(selection!=null) {
                    new DeploymentCommandDelegate(
                            ContentRepositoryView.this.presenter,
                            DeploymentCommand.REMOVE_FROM_DOMAIN).execute(
                            selection
                    );
                }
            }
        }));

        // --

        toolStrip.addToolButtonRight(new ToolButton("Assign"
                , new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                final DeploymentRecord selection = selectionModel.getSelectedObject();
                if(selection!=null)
                {
                    new DeploymentCommandDelegate(
                            ContentRepositoryView.this.presenter,
                            DeploymentCommand.ADD_TO_GROUP).execute(
                            selection
                    );
                }
            }
        }));


        toolStrip.addToolButtonRight(new ToolButton("Update"
                , new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {

                final DeploymentRecord selection = selectionModel.getSelectedObject();
                if(selection!=null)
                {
                    new DeploymentCommandDelegate(
                            ContentRepositoryView.this.presenter,
                            DeploymentCommand.UPDATE_CONTENT).execute(
                            selection
                    );
                }
            }
        }));

        SafeHtmlBuilder tableFooter = new SafeHtmlBuilder();
        tableFooter.appendHtmlConstant("<span style='font-size:10px;color:#A7ABB4;'>[1] File System Deployment</span>");


        Form<DeploymentRecord> form2 = new Form<DeploymentRecord>(DeploymentRecord.class);
        form2.setNumColumns(2);
        form2.setEnabled(true);
        TextAreaItem path = new TextAreaItem("path", "Path");
        TextAreaItem relative = new TextAreaItem("relativeTo", "Relative To");
        form2.setFields(path, relative);

        form2.bind(contentTable);

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadline(Console.CONSTANTS.common_label_contentRepository())
                .setMaster(Console.MESSAGES.available("Deployment Content"), contentTable)
                .setMasterTools(toolStrip)
                .setMasterFooter(new HTML(tableFooter.toSafeHtml()))
                .setDescription("The content repository contains all deployed content. Contents need to be assigned to sever groups in order to become effective.")
                .addDetail(Console.CONSTANTS.common_label_attributes(), form.asWidget())
                .addDetail("Path", form2.asWidget());


        return layout.build();
    }

    @Override
    public void updateDeploymentInfo(DomainDeploymentInfo domainDeploymentInfo, DeploymentRecord... targets) {

        List<ServerGroupRecord> serverGroups = this.presenter.getServerGroups();

        this.groupOverview.setGroups(serverGroups);
        this.groupOverview.setGroupDeployments(domainDeploymentInfo.getServerGroupDeployments());

        domainDeploymentProvider.setList(domainDeploymentInfo.getDomainDeployments());
        contentTable.selectDefaultEntity();

        currentAssignments = matchAssignments(domainDeploymentInfo);

        filter.reset(true);
    }

    private Map<String,List<String>>  matchAssignments(DomainDeploymentInfo domainDeploymentInfo) {
        Map<String,List<String>> assignments = new HashMap<String, List<String>>();
        for(String deployment : domainDeploymentInfo.getAllDeploymentNames())
        {

            if(null==assignments.get(deployment))
                assignments.put(deployment, new ArrayList<String>());

            final Map<String, List<DeploymentRecord>> groupDeployments = domainDeploymentInfo.getServerGroupDeployments();
            for(String group : groupDeployments.keySet())
            {
                for(DeploymentRecord item : groupDeployments.get(group))
                {
                    if(item.getName().equals(deployment))
                    {
                        assignments.get(deployment).add(group);
                        break;
                    }
                }
            }



        }

        return assignments;
    }

    private List<Column> makeNameAndRuntimeColumns() {
        List<Column> columns = new ArrayList<Column>(2);

        columns.add(new TitleColumn());

        TextColumn<DeploymentRecord> dplRuntimeColumn = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                String title = null;
                if(record.getRuntimeName().length()>27)
                    title = record.getRuntimeName().substring(0,26)+"...";
                else
                    title = record.getRuntimeName();
                return title;
            }
        };

        columns.add(dplRuntimeColumn);

        return columns;
    }

}