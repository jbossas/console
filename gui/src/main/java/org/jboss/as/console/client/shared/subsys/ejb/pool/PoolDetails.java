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
package org.jboss.as.console.client.shared.subsys.ejb.pool;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.ejb.pool.model.EJBPool;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.forms.UnitBoxItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

/**
 * @author David Bosschaert
 */
public class PoolDetails {
    private Form<EJBPool> form;
    private ToolButton editBtn;
    private final BeanPoolsPresenter presenter;

    public PoolDetails(BeanPoolsPresenter presenter) {
        this.presenter = presenter;
        form = new Form(EJBPool.class);
        form.setNumColumns(2);
    }

    public Widget asWidget() {
        VerticalPanel detailPanel = new VerticalPanel();
        detailPanel.setStyleName("fill-layout-width");

        ToolStrip detailToolStrip = new ToolStrip();
        editBtn = new ToolButton(Console.CONSTANTS.common_label_edit());
        ClickHandler editHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (form.getEditedEntity() == null)
                    return;

                if (Console.CONSTANTS.common_label_edit().equals(editBtn.getText()))
                    presenter.onEditPool(form.getEditedEntity());
                else
                    presenter.onSavePool(form.getEditedEntity().getName(), form.getChangedValues());
            }
        };
        editBtn.addClickHandler(editHandler);
        editBtn.setEnabled(false); // Attributes are not yet editable...
        detailToolStrip.addToolButton(editBtn);

        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        ClickHandler deleteHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Feedback.confirm("Delete DataSource",
                    "Really delete this DataSource '" + form.getEditedEntity().getName() + "' ?",
                    new Feedback.ConfirmationHandler() {
                        @Override
                        public void onConfirmation(boolean isConfirmed) {
                            presenter.onDeletePool(form.getEditedEntity().getName());
                        }
                    });
            }
        };
        deleteBtn.addClickHandler(deleteHandler);
        detailToolStrip.addToolButton(deleteBtn);

        detailPanel.add(detailToolStrip);

        TextItem nameItem = new TextItem("name", "Name");
        NumberBoxItem maxPoolSizeItem = new NumberBoxItem("maxPoolSize", "Maximum Pool Size");
        UnitBoxItem<Long> timeout = new UnitBoxItem<Long>("timeout", "timeoutUnit", "Timeout", Long.class);
        presenter.populateTimeoutUnits(timeout);

        form.setFields(nameItem, maxPoolSizeItem, timeout, timeout.getUnitItem());

        form.setEnabled(false);

        Widget formWidget = form.asWidget();

        FormHelpPanel helpPanel = new FormHelpPanel(
            new FormHelpPanel.AddressCallback() {
                @Override
                public ModelNode getAddress() {
                    ModelNode address = Baseadress.get();
                    address.add(ModelDescriptionConstants.SUBSYSTEM, BeanPoolsPresenter.SUBSYSTEM_NAME);
                    address.add(BeanPoolsPresenter.POOL_NAME, "*");
                    return address;
                }
            }, form);
        detailPanel.add(helpPanel.asWidget());
        detailPanel.add(formWidget);

        ScrollPanel scroll = new ScrollPanel(detailPanel);
        return scroll;
    }

    public void bind(CellTable<EJBPool> poolTable) {
        form.bind(poolTable);
    }

    public void setEnabled(boolean b) {
        form.setEnabled(b);

        if (b)
            editBtn.setText(Console.CONSTANTS.common_label_save());
        else
            editBtn.setText(Console.CONSTANTS.common_label_edit());
    }
}
