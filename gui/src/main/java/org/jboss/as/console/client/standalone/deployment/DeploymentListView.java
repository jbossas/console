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

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.deployment.DeploymentCommandDelegate;
import org.jboss.as.console.client.shared.deployment.DeploymentFilter;
import org.jboss.as.console.client.shared.deployment.TitleColumn;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.List;


/**
 * @author Heiko Braun
 * @author Stan Silvert
 * @date 3/14/11
 */
public class DeploymentListView extends SuspendableViewImpl implements DeploymentListPresenter.MyView{


    private DeploymentListPresenter presenter;
    private DefaultCellTable<DeploymentRecord> deploymentTable;
    private ListDataProvider<DeploymentRecord> dataProvider;
    private DeploymentFilter filter;

    @Override
    public Widget createWidget() {



        ProvidesKey<DeploymentRecord> key = new ProvidesKey<DeploymentRecord>() {
            @Override
            public Object getKey(DeploymentRecord deploymentRecord) {
                return deploymentRecord.getName();
            }
        };
        deploymentTable = new DefaultCellTable<DeploymentRecord>(8, key);
        final SingleSelectionModel<DeploymentRecord> selectionModel = new SingleSelectionModel<DeploymentRecord>();
        deploymentTable.setSelectionModel(selectionModel);

        dataProvider = new ListDataProvider<DeploymentRecord>(key);
        dataProvider.addDataDisplay(deploymentTable);


        // ---

        final ToolStrip toolStrip = new ToolStrip();
        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewDeploymentDialoge(null, false);
            }
        });
        addBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_deploymentListView());
        toolStrip.addToolButtonRight(addBtn);

        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                DeploymentRecord selection = selectionModel.getSelectedObject();
                if(selection!=null)
                {
                    new DeploymentCommandDelegate(
                            DeploymentListView.this.presenter,
                            DeploymentCommand.REMOVE_FROM_STANDALONE).execute(
                            selection
                    );
                }
            }
        }));

        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_enOrDisable(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                DeploymentRecord selection = selectionModel.getSelectedObject();
                if(selection!=null)
                {
                    new DeploymentCommandDelegate(
                            DeploymentListView.this.presenter,
                            DeploymentCommand.ENABLE_DISABLE).execute(
                            selection
                    );
                }
            }
        }));

        toolStrip.addToolButtonRight(new ToolButton("Update", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                DeploymentRecord selection = selectionModel.getSelectedObject();
                if(selection!=null)
                {
                    new DeploymentCommandDelegate(
                            DeploymentListView.this.presenter,
                            DeploymentCommand.UPDATE_CONTENT).execute(
                            selection
                    );
                }
            }
        }));



        filter = new DeploymentFilter(dataProvider);
        toolStrip.addToolWidget(filter.asWidget());

        TitleColumn dplNameColumn = new TitleColumn() {};

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

        deploymentTable.addColumn(dplNameColumn, Console.CONSTANTS.common_label_name());
        deploymentTable.addColumn(dplRuntimeColumn, Console.CONSTANTS.common_label_runtimeName());
        deploymentTable.addColumn(makeEnabledColumn(), Console.CONSTANTS.common_label_enabled());

        Form<DeploymentRecord> form = new Form<DeploymentRecord>(DeploymentRecord.class);
        form.setNumColumns(2);
        form.setEnabled(true);
        TextAreaItem name = new TextAreaItem("name", "Name");
        TextAreaItem runtimeName = new TextAreaItem("runtimeName", "Runtime Name");
        form.setFields(name, runtimeName);

        Form<DeploymentRecord> form2 = new Form<DeploymentRecord>(DeploymentRecord.class);
        form2.setNumColumns(2);
        form2.setEnabled(true);
        TextAreaItem path = new TextAreaItem("path", "Path");
        TextBoxItem relative = new TextBoxItem("relativeTo", "Relative To");
        form2.setFields(path, relative);

        form.bind(deploymentTable);
        form2.bind(deploymentTable);

        SafeHtmlBuilder tableFooter = new SafeHtmlBuilder();
        tableFooter.appendHtmlConstant("<span>[1] File System Deployment</span>");

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setTitle(Console.CONSTANTS.common_label_deployments())
                .setHeadline(Console.CONSTANTS.common_label_deployments())
                .setDescription("Currently deployed application components.")
                .setMaster(Console.MESSAGES.available("Deployments"), deploymentTable)
                .setMasterTools(toolStrip)
                .setMasterFooter(new HTML(tableFooter.toSafeHtml()))
                .addDetail(Console.CONSTANTS.common_label_attributes(), form.asWidget())
                .addDetail("Path", form2.asWidget());


        return layout.build();
    }

    // Refactor Me!  Copied from org.jboss.as.console.client.domain.groups.deployment.DeploymentsOverview
    private Column makeEnabledColumn() {
        return new Column<DeploymentRecord, ImageResource>(new ImageResourceCell()) {

            @Override
            public ImageResource getValue(DeploymentRecord deployment) {

                ImageResource res = null;

                if("FAILED".equalsIgnoreCase(deployment.getStatus()))
                {
                    res = Icons.INSTANCE.status_warn();
                }
                else if (deployment.isEnabled()) {
                    res = Icons.INSTANCE.status_good();
                } else {
                    res = Icons.INSTANCE.status_bad();
                }

                return res;
            }
        };
    }

    @Override
    public void setPresenter(DeploymentListPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateDeploymentInfo(List<DeploymentRecord> deployments) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(deployments);
        dataProvider.flush();

        deploymentTable.selectDefaultEntity();

        filter.reset(true);

    }

}
