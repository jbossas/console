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
package org.jboss.as.console.client.shared.subsys.security;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.security.model.GenericSecurityDomainData;
import org.jboss.as.console.client.widgets.tables.ButtonCell;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.Feedback;

/**
 * @author David Bosschaert
 */
public abstract class AbstractDomainDetailEditor <T extends GenericSecurityDomainData> {
    final Class<T> entityClass;
    final SecurityDomainsPresenter presenter;

    DefaultCellTable<T> attributesTable;
    ListDataProvider<T> attributesProvider;
    String domainName;
    boolean resourceExists;
    ToolButton addModule;
    List<T> backup;
    DefaultWindow window;

    AbstractDomainDetailEditor(SecurityDomainsPresenter presenter, Class<T> entityClass) {
        this.presenter = presenter;
        this.entityClass = entityClass;
    }

    abstract String getEntityName();
    abstract String getStackElementName();
    abstract String getStackName();
    abstract Wizard<T> getWizard();
    abstract void saveData();

    Widget asWidget() {
        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("fill-layout-width");

        attributesTable = new DefaultCellTable<T>(4);
        attributesTable.getElement().setAttribute("style", "margin-top:5px;");
        attributesProvider = new ListDataProvider<T>();
        attributesProvider.addDataDisplay(attributesTable);

        ToolStrip tableTools = new ToolStrip();
        tableTools.addToolWidget(new HTML("&nbsp;&nbsp;" + getStackName()));
        addModule = new ToolButton(Console.CONSTANTS.common_label_add());
        addModule.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openWizard(null);
            }
        });
        tableTools.addToolButtonRight(addModule);
        vpanel.add(tableTools);

        Column<T, String> codeColumn = new Column<T, String>(new TextCell()) {
            @Override
            public String getValue(T record) {
                return record.getCode();
            }
        };
        attributesTable.addColumn(codeColumn, "Code");

        addCustomColumns(attributesTable);

        ButtonCell<T> editCell = new ButtonCell<T>(Console.CONSTANTS.common_label_edit(), new ActionCell.Delegate<T>() {
            @Override
            public void execute(T object) {
                openWizard(object);
            }
        });
        ButtonCell<T> removeCell = new ButtonCell<T>(Console.CONSTANTS.common_label_delete(), new ActionCell.Delegate<T>() {
            @Override
            public void execute(final T object) {
                Feedback.confirm(getEntityName(), "Remove this entry: " + object.getCode() + "?",
                    new Feedback.ConfirmationHandler() {
                        @Override
                        public void onConfirmation(boolean isConfirmed) {
                            if (isConfirmed) {
                                attributesProvider.getList().remove(object);
                                saveData();
                            }
                        }
                    });
            }
        });

        List<HasCell<T, T>> actionCells = new ArrayList<HasCell<T,T>>();
        actionCells.add(new IdentityColumn<T>(editCell));
        actionCells.add(new IdentityColumn<T>(removeCell));
        IdentityColumn<T> actionColumn = new IdentityColumn<T>(new CompositeCell(actionCells));
        attributesTable.addColumn(actionColumn, "");

        vpanel.add(attributesTable);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(attributesTable);
        vpanel.add(pager);

        final PropertyEditor propertyEditor = new PropertyEditor();

        final SingleSelectionModel<T> ssm = new SingleSelectionModel<T>();
        ssm.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                T policy = ssm.getSelectedObject();
                List<PropertyRecord> props = policy.getProperties();
                if (props == null)
                    props = new ArrayList<PropertyRecord>();
                propertyEditor.setProperties("", props);
            }
        });
        attributesTable.setSelectionModel(ssm);

        vpanel.add(new ContentGroupLabel("Properties"));
        vpanel.add(propertyEditor.asWidget());
        propertyEditor.setAllowEditProps(false);

        return vpanel;
    }

    void addCustomColumns(DefaultCellTable<T> attributesTable) {
        // by default no custom columns are needed
    }

    void setData(String domainName, List<T> newList, boolean resourceExists) {
        this.domainName = domainName;
        this.resourceExists = resourceExists;

        List<T> list = attributesProvider.getList();
        list.clear();
        list.addAll(newList);
    }

    void openWizard(T editedObject) {
        Wizard<T> wizard = getWizard();
        if (wizard == null)
            return;

        window = new DefaultWindow(
            (editedObject == null ? Console.CONSTANTS.common_label_add() : Console.CONSTANTS.common_label_edit()) + " " +
            getStackElementName());
        window.setWidth(480);
        window.setHeight(360);
        window.setWidget(wizard.asWidget());
        if (editedObject != null)
            wizard.edit(editedObject);

        window.setGlassEnabled(true);
        window.center();
    }

    public void closeWizard() {
        if (window != null)
            window.hide();
    }

    public void addAttribute(T policy) {
        attributesProvider.getList().add(policy);
        save(policy);
    }

    public void save(T policy) {
        saveData();

        // This sometimes selects the right row but not always - is there a more consistent way?
        attributesTable.getSelectionModel().setSelected(policy, true);
    }

    public interface Wizard<T> {
        void edit(T object);
        Widget asWidget();
    }
}
