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

package org.jboss.as.console.client.shared.subsys.infinispan;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.infinispan.model.CacheContainer;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.Columns.NameColumn;
import org.jboss.as.console.client.shared.viewframework.EntityDetails;
import org.jboss.as.console.client.shared.viewframework.EntityEditor;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.FrameworkPresenter;
import org.jboss.as.console.client.shared.viewframework.SingleEntityView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Main view class for Infinispan Cache Containers.
 *
 * @author Stan Silvert
 */
public class CacheContainerView extends AbstractEntityView<CacheContainer> implements CacheContainerPresenter.MyView {

    private EntityToDmrBridge bridge;
    private DefaultCacheContainerWindow defaultCacheContainerWindow;

    private EmbeddedAliasesView aliasesView;

    @Inject
    public CacheContainerView(ApplicationMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(CacheContainer.class, propertyMetaData);
        bridge = new CacheContainerEntityToDmrBridge(propertyMetaData, CacheContainer.class, this, dispatcher);
        defaultCacheContainerWindow = new DefaultCacheContainerWindow(propertyMetaData, dispatcher);
        setDescription(Console.CONSTANTS.subsys_infinispan_cache_container_desc());
    }

    @Override
    public EntityToDmrBridge getEntityBridge() {
        return bridge;
    }

    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_infinispan_cache_containers();
    }

    @Override
    protected ToolStrip createToolStrip() {
        ToolStrip toolStrip = super.createToolStrip();

        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_setDefault(),
                    new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                           defaultCacheContainerWindow.setChoices(bridge.getEntityList());
                           defaultCacheContainerWindow.show();
                        }
                    }));
        return toolStrip;
    }

    @Override
    protected FormAdapter<CacheContainer> makeAddEntityForm() {
        Form<CacheContainer> form = new Form(CacheContainer.class);
        form.setNumColumns(1);
        form.setFields(getFormMetaData().findAttribute("name").getFormItemForAdd());
                      // getFormMetaData().findAttribute("defaultCache").getFormItemForAdd());
        return form;
    }

    @Override
    protected DefaultCellTable<CacheContainer> makeEntityTable() {
        DefaultCellTable<CacheContainer> table = new DefaultCellTable<CacheContainer>(4);

        table.addColumn(new NameColumn(), NameColumn.LABEL);

        TextColumn<CacheContainer> defaultCacheColumn = new TextColumn<CacheContainer>() {
            @Override
            public String getValue(CacheContainer record) {
                return record.getDefaultCache();
            }
        };

        table.addColumn(defaultCacheColumn, Console.CONSTANTS.subsys_infinispan_default_cache());

        return table;
    }

    @Override
    protected EntityEditor<CacheContainer> makeEntityEditor() {
        EntityDetails<CacheContainer> details = new EntityDetails<CacheContainer>(
                this, getEntityDisplayName(),
                makeEditEntityDetailsForm(),
                getAddress(),
                hideButtons);
        return new EntityEditor<CacheContainer>(this, getEntityDisplayName(), makeAddEntityPopup(), makeEntityTable(), details, hideButtons);
    }

    @Override
    protected List<SingleEntityView<CacheContainer>> provideAdditionalTabs(
            Class<?> beanType,
            FormMetaData formMetaData,
            FrameworkPresenter presenter) {

        List<SingleEntityView<CacheContainer>> additionalTabs =
                new ArrayList<SingleEntityView<CacheContainer>>();

        this.aliasesView = new EmbeddedAliasesView(new FrameworkPresenter() {
            @Override
            public EntityToDmrBridge getEntityBridge() {
                return CacheContainerView.this.getEntityBridge();
            }
        });
        additionalTabs.add(aliasesView);

        return additionalTabs;
    }

}
