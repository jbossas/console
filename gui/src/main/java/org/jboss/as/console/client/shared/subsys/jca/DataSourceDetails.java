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

package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.as.console.client.widgets.forms.items.JndiNameItem;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.StatusItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 5/4/11
 */
public class DataSourceDetails {

    private Form<DataSource> form;
    private DataSourcePresenter presenter;
    private ToolButton disableBtn;

    public DataSourceDetails(DataSourcePresenter presenter) {
        this.presenter = presenter;
        form = new Form(DataSource.class);
        form.setNumColumns(2);

        /*form.addEditListener(new EditListener<DataSource>() {
            @Override
            public void editingBean(DataSource bean) {
                String nextState = bean.isEnabled() ? Console.CONSTANTS.common_label_disable():Console.CONSTANTS.common_label_enable();
                disableBtn.setText(nextState);
            }
        });*/
    }

    public Widget asWidget() {
        VerticalPanel detailPanel = new VerticalPanel();
        detailPanel.setStyleName("fill-layout-width");


        /*ClickHandler disableHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final boolean nextState = !form.getEditedEntity().isEnabled();
                Feedback.confirm(Console.MESSAGES.modify("datasource"),
                        Console.MESSAGES.modifyConfirm("Datasource "+form.getEditedEntity().getName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onDisable(form.getEditedEntity(), nextState);
                                }
                            }
                        });
            }
        };

        disableBtn = new ToolButton(Console.CONSTANTS.common_label_enOrDisable(), disableHandler);
        disableBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_enOrDisable_dataSourceDetails());

        ToolButton verifyBtn = new ToolButton(Console.CONSTANTS.subsys_jca_dataSource_verify(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.verifyConnection(form.getEditedEntity().getName(), false);
            }
        });

        verifyBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_verify_dataSourceDetails());

        */

        FormToolStrip<DataSource> toolStrip = new FormToolStrip<DataSource>(
                form,
                new FormToolStrip.FormCallback<DataSource>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                         presenter.onSaveDSDetails(form.getEditedEntity().getName(), form.getChangedValues());
                    }

                    @Override
                    public void onDelete(DataSource entity) {

                    }
                });


        toolStrip.providesDeleteOp(false);

       /* toolStrip.addToolButtonRight(disableBtn);

        // not available in domain mode
        if(Console.getBootstrapContext().isStandalone())
            toolStrip.addToolButtonRight(verifyBtn);  */

        detailPanel.add(toolStrip.asWidget());

        final TextItem nameItem = new TextItem("name", "Name");
        TextBoxItem jndiItem = new JndiNameItem("jndiName", "JNDI");
        StatusItem enabledFlagItem = new StatusItem("enabled", "Is enabled?");
        TextItem driverItem = new TextItem("driverName", "Driver");

        CheckBoxItem shareStatements = new CheckBoxItem("sharePreparedStatements", "Share Prepared Statements");
        NumberBoxItem statementCacheSize = new NumberBoxItem("prepareStatementCacheSize", "Statement Cache Size");

        form.setFields(nameItem, jndiItem, enabledFlagItem, driverItem, shareStatements, statementCacheSize);

        form.setEnabled(false); // currently not editable

        Widget formWidget = form.asWidget();


        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "datasources");
                        address.add("data-source", "*");
                        return address;
                    }
                }, form
        );
        detailPanel.add(helpPanel.asWidget());


        detailPanel.add(formWidget);

        ScrollPanel scroll = new ScrollPanel(detailPanel);
        return scroll;
    }

    public void bind(CellTable<DataSource> dataSourceTable) {
        form.bind(dataSourceTable);
    }

    public void setEnabled(boolean b) {
        form.setEnabled(b);
    }

    public DataSource getCurrentSelection() {
        return form.getEditedEntity();
    }
}
