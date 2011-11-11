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
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.security.model.AbstractAuthData;
import org.jboss.as.console.client.shared.subsys.security.wizard.NewAuthorizationPolicyModuleWizard;
import org.jboss.as.console.client.widgets.tables.ButtonCell;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;

/**
 * @author David Bosschaert
 */
public abstract class AuthEditor <T extends AbstractAuthData> implements PropertyManagement {
    final SecurityDomainsPresenter presenter;

    DefaultCellTable<T> attributesTable;
    ListDataProvider<T> attributesProvider;
    String domainName;
    boolean resourceExists;
    ToolButton addModule;
    List<T> backup;
    DefaultWindow window;

    AuthEditor(SecurityDomainsPresenter presenter) {
        this.presenter = presenter;
    }

    abstract String getStackName();
    abstract void onSaveData();

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
                openWizard();
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

        Column<T, String> flagColumn = new Column<T, String>(new TextCell()) {
            @Override
            public String getValue(T record) {
                return record.getFlag();
            }
        };
        attributesTable.addColumn(flagColumn, "Flag");

        Column<T, T> removeColumn = new Column<T, T>(
                new ButtonCell<T>(Console.CONSTANTS.common_label_delete(), new ActionCell.Delegate<T>() {
                    @Override
                    public void execute(T object) {
                        // The remove button has the same functional status as the add button so
                        // it should only operate if the addbutton is enabled.
                        // (I couldn't figure out a way to actually disable this button yet).
                        if (addModule.isEnabled())
                            attributesProvider.getList().remove(object);
                    }
                })
            ) {
                @Override
                public T getValue(T record) {
                    return record;
                }
            };
        attributesTable.addColumn(removeColumn, "");
        vpanel.add(attributesTable);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(attributesTable);
        vpanel.add(pager);

        final PropertyEditor propertyEditor = new PropertyEditor(this, true, 5);

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
        setEditingEnabled(false);

        return vpanel;
    }

    void setData(String domainName, List<T> newList, boolean resourceExists) {
        this.domainName = domainName;
        this.resourceExists = resourceExists;

        List<T> list = attributesProvider.getList();
        list.clear();
        list.addAll(newList);
    }

    private void openWizard() {
        window = new DefaultWindow("New Authorization Policy");
        window.setWidth(480);
        window.setHeight(360);
        window.setWidget(new NewAuthorizationPolicyModuleWizard(this).asWidget());
        window.setGlassEnabled(true);
        window.center();
    }

    public void closeWizard() {
        if (window != null)
            window.hide();
    }

    public void addPolicy(T policy) {
        attributesProvider.getList().add(policy);
    }

    // These are for the property editor
    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
    }

    @Override
    public void closePropertyDialoge() {
    }

    public void setEditingEnabled(boolean isEnabled) {
        addModule.setVisible(isEnabled);
    }

    public void onCancel() {
        setEditingEnabled(false);

        List<T> list = attributesProvider.getList();
        list.clear();
        list.addAll(backup);
        backup = null;
    }

    public void onEdit() {
        backup = new ArrayList<T>(attributesProvider.getList());
        setEditingEnabled(true);
    }

    public void onSave() {
        setEditingEnabled(false);
        onSaveData();
    }
}
