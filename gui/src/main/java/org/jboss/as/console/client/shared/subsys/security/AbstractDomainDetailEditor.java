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

import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.spi.ProviderKeyBinding;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.security.model.GenericSecurityDomainData;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David Bosschaert
 * @author Heiko Braun
 */
public abstract class AbstractDomainDetailEditor <T extends GenericSecurityDomainData>
        implements PropertyManagement {
    final Class<T> entityClass;
    final SecurityDomainsPresenter presenter;

    DefaultCellTable<T> attributesTable;
    ListDataProvider<T> attributesProvider;
    String domainName;
    boolean resourceExists;
    ToolButton addModule;

    DefaultWindow window;
    ContentHeaderLabel headerLabel;

    Wizard<T> wizard;
    PropertyEditor propertyEditor;
    DefaultWindow propertyWindow;
    private String description;

    AbstractDomainDetailEditor(SecurityDomainsPresenter presenter, Class<T> entityClass) {
        this.presenter = presenter;
        this.entityClass = entityClass;
    }

    abstract String getEntityName();
    abstract String getStackElementName();
    abstract String getStackName();
    abstract Wizard<T> getWizard();
    abstract ProvidesKey<T> getKeyProvider();

    protected void setDescription(String description) {
        this.description = description;
    }

    abstract void saveData();
    
    abstract void removeData();

    Widget asWidget() {

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");

        // TODO: in order for the selection to retain we need a distinct key per module.

        // attributesTable = new DefaultCellTable<T>(4, getKeyProvider());
        attributesTable = new DefaultCellTable<T>(4);

        attributesTable.getElement().setAttribute("style", "margin-top:5px;");
        attributesProvider = new ListDataProvider<T>();
        attributesProvider.addDataDisplay(attributesTable);

        headerLabel = new ContentHeaderLabel("TITLE HERE");
        vpanel.add(headerLabel);
        vpanel.add(new ContentDescription(description));

        vpanel.add(new ContentGroupLabel(getStackName()));

        ToolStrip tableTools = new ToolStrip();

        addModule = new ToolButton(Console.CONSTANTS.common_label_add());
        addModule.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openWizard(null);
            }
        });
        addModule.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_abstractDomainDetailEditor());
        tableTools.addToolButtonRight(addModule);
        tableTools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {

                        final T policy = getCurrentSelection();
                        Feedback.confirm(
                                Console.MESSAGES.deleteTitle(getEntityName()),
                                Console.MESSAGES.deleteConfirm(policy.getCode()),
                                new Feedback.ConfirmationHandler() {
                                    @Override
                                    public void onConfirmation(boolean isConfirmed) {
                                        if (isConfirmed) {
                                            attributesProvider.getList().remove(policy);
                                            if(attributesProvider.getList().size() > 0){
                                                saveData();
                                            }
                                            // call remove() on last provider-module instead of save()
                                            else{
                                            	removeData();
                                            }
                                        }
                                    }
                                });
                    }
                })
        );
        vpanel.add(tableTools);

        // -------

        Column<T, String> codeColumn = new Column<T, String>(new TextCell()) {
            @Override
            public String getValue(T record) {
                return record.getCode();
            }
        };
        attributesTable.addColumn(codeColumn, Console.CONSTANTS.subsys_security_codeField());

        addCustomColumns(attributesTable);

        List<HasCell<T, T>> actionCells = new ArrayList<HasCell<T,T>>();
        IdentityColumn<T> actionColumn = new IdentityColumn<T>(new CompositeCell(actionCells));
        attributesTable.addColumn(actionColumn, "");

        vpanel.add(attributesTable);

        // -------

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(attributesTable);
        vpanel.add(pager);

        // -------


        propertyEditor = new PropertyEditor(this, true);
        propertyEditor.setHideButtons(false);

        final SingleSelectionModel<T> ssm = new SingleSelectionModel<T>();
        ssm.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                T policy = ssm.getSelectedObject();
                if (policy == null) // Can this actually happen?
                {
                    return;
                }

                List<PropertyRecord> props = policy.getProperties();
                if (props == null)  {
                    props = new ArrayList<PropertyRecord>();
                    policy.setProperties(props);
                }

                propertyEditor.setProperties("", policy.getProperties());

                wizard.edit(policy);

            }
        });
        attributesTable.setSelectionModel(ssm);


        wizard = getWizard();

        TabPanel bottomTabs = new TabPanel();
        bottomTabs.setStyleName("default-tabpanel");
        bottomTabs.add(wizard.asWidget(), "Attributes");
        bottomTabs.add(propertyEditor.asWidget(), "Module Options");

        propertyEditor.setAllowEditProps(false);

        vpanel.add(new ContentGroupLabel("Details"));

        vpanel.add(bottomTabs);
        bottomTabs.selectTab(0);

        // -------

        ScrollPanel scroll = new ScrollPanel(vpanel);

        LayoutPanel layout = new LayoutPanel();
        layout.add(scroll);
        layout.setWidgetTopHeight(scroll, 0, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    private T getCurrentSelection() {
        return ((SingleSelectionModel<T>) attributesTable.getSelectionModel()).getSelectedObject();
    }


    void addCustomColumns(DefaultCellTable<T> attributesTable) {
        // by default no custom columns are needed
    }

    void setData(String domainName, List<T> newList, boolean resourceExists) {
        this.domainName = domainName;
        this.resourceExists = resourceExists;

        this.headerLabel.setText("Security Domain: "+ domainName);

        List<T> list = attributesProvider.getList();
        list.clear();
        list.addAll(newList);

        if(!list.isEmpty())
        {
            attributesTable.getSelectionModel().setSelected(list.get(0), true);
        }
        else if(wizard!=null) // loading happens before asWidget() is invoked
        {
            wizard.clearValues();
            propertyEditor.clearValues();
        }
    }

    void openWizard(T editedObject) {
        Wizard<T> wizard = getWizard();
        wizard.setIsDialogue(true);

        if (wizard == null)
            return;

        window = new DefaultWindow(
                (editedObject == null ? Console.CONSTANTS.common_label_add() : Console.CONSTANTS.common_label_edit()) + " " +
                        getStackElementName());
        window.setWidth(480);
        window.setHeight(400);
        window.setWidget(wizard.asWidget());

        if (editedObject != null) wizard.edit(editedObject);

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

        // This combination seems to consistently update the details view
        attributesTable.getSelectionModel().setSelected(policy, true);
        SelectionChangeEvent.fire(attributesTable.getSelectionModel());
    }

    public interface Wizard<T> {
        void edit(T object);
        Widget asWidget();
        Wizard setIsDialogue(boolean b);
        void clearValues();
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {

        closePropertyDialoge();

        T currentSelection = getCurrentSelection();

        if(null == currentSelection.getProperties())
            currentSelection.setProperties(new ArrayList<PropertyRecord>());

        currentSelection.getProperties().add(prop);
        save(currentSelection);
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        T currentSelection = getCurrentSelection();
        currentSelection.getProperties().remove(prop);
        save(currentSelection);
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        // Not provided
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {

        propertyWindow = new DefaultWindow(Console.MESSAGES.createTitle("Module Option"));
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);

        propertyWindow.setWidget(
                new NewPropertyWizard(this, "", false).asWidget()
        );

        propertyWindow.setGlassEnabled(true);
        propertyWindow.center();
    }

    @Override
    public void closePropertyDialoge() {
        propertyWindow.hide();
    }


}
