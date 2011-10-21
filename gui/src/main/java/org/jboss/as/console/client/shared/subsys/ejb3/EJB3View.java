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
package org.jboss.as.console.client.shared.subsys.ejb3;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.ejb3.model.EJB3Subsystem;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.EntityDetails;
import org.jboss.as.console.client.shared.viewframework.EntityEditor;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.FrameworkButton;
import org.jboss.as.console.client.shared.viewframework.SingleEntityToDmrBridgeImpl;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.ObservableFormItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * @author David Bosschaert
 */
public class EJB3View extends AbstractEntityView<EJB3Subsystem> implements EJB3Presenter.MyView {
    private final EntityToDmrBridge<EJB3Subsystem> bridge;
    private final BeanPoolsView beanPoolsView;
    private final ServicesView servicesView;
    private final ThreadPoolsView threadPoolsView;
    private ComboBoxItem defaultSLSBPoolItem, defaultMDBPoolItem;

    @Inject
    public EJB3View(PropertyMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(EJB3Subsystem.class, propertyMetaData, EnumSet.of(FrameworkButton.ADD, FrameworkButton.REMOVE));
        bridge = new SingleEntityToDmrBridgeImpl<EJB3Subsystem>(propertyMetaData, EJB3Subsystem.class, this, dispatcher);

        servicesView = new ServicesView(propertyMetaData, dispatcher);
        beanPoolsView = new BeanPoolsView(propertyMetaData, dispatcher);
        threadPoolsView = new ThreadPoolsView(propertyMetaData, dispatcher);
    }

    @Override
    public void itemAction(Action action, ObservableFormItem item) {
        if (action != Action.CREATED)
            return;

        String javaName = item.getPropertyBinding().getJavaName();
        if (javaName.equals("defaultSLSBPool"))
            defaultSLSBPoolItem = (ComboBoxItem) item.getWrapped();
        else if (javaName.equals("defaultMDBPool"))
            defaultMDBPoolItem = (ComboBoxItem) item.getWrapped();
    }

    @Override
    public Widget createWidget() {

//        entityEditor = makeEntityEditor();

        // overall layout
        TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutPanel.addStyleName("default-tabpanel");

//        Widget entityEditorWidget = entityEditor.asWidget();
//        entityEditorWidget.addStyleName("rhs-content-panel");

        tabLayoutPanel.add(createEmbeddableWidget(), getEntityDisplayName());
        tabLayoutPanel.add(servicesView.asWidget(), "Services");
        tabLayoutPanel.add(beanPoolsView.asWidget(), beanPoolsView.getEntityDisplayName());
        tabLayoutPanel.add(threadPoolsView.asWidget(), threadPoolsView.getEntityDisplayName());


        return tabLayoutPanel;
    }

    @Override
    protected EntityEditor<EJB3Subsystem> makeEntityEditor() {
        EntityDetails<EJB3Subsystem> details = new EntityDetails<EJB3Subsystem>(getEntityDisplayName(),
                                                                                makeEditEntityDetailsForm(),
                                                                                getEntityBridge(),
                                                                                getAddress(),
                                                                                hideButtons);
        return new EntityEditor<EJB3Subsystem>(getEntityDisplayName(), null, makeEntityTable(), details, hideButtons);
    }

    @Override
    protected EntityToDmrBridge<EJB3Subsystem> getEntityBridge() {
        return bridge;
    }

    @Override
    protected DefaultCellTable<EJB3Subsystem> makeEntityTable() {
        DefaultCellTable<EJB3Subsystem> table = new DefaultCellTable<EJB3Subsystem>(5);
        table.setVisible(false);
        return table;
    }

    @Override
    protected FormAdapter<EJB3Subsystem> makeAddEntityForm() {
        return null;
    }

    @Override
    protected String getEntityDisplayName() {
        return "Container"; // TODO i18n // is this one used at all?
    }

    @Override
    public void setBeanPoolNames(List<String> poolNames) {
        if (defaultMDBPoolItem != null)
            defaultMDBPoolItem.setValueMap(poolNames);
        if (defaultSLSBPoolItem != null)
            defaultSLSBPoolItem.setValueMap(poolNames);
    }

    @Override
    public void setThreadPoolNames(List<String> threadPoolNames) {
        servicesView.setThreadPoolNames(threadPoolNames);
    }

    @Override
    public void setPoolTimeoutUnits(Collection<String> units, String defaultUnit) {
        beanPoolsView.setTimeoutUnits(units, defaultUnit);
    }

    @Override
    public void loadPools() {
        beanPoolsView.initialLoad();
    }

    @Override
    public void loadThreadPools() {
        threadPoolsView.initialLoad();
    }

    @Override
    public void loadServices() {
        servicesView.initialLoad();
    }
}
