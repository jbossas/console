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
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.infinispan.model.LocalCache;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import javax.inject.Inject;

/**
 * Main view class for Infinispan LocalCache Containers.
 *
 * @author Stan Silvert
 */
public class LocalCacheView extends AbstractCacheView<LocalCache> implements LocalCachePresenter.MyView {

    private LocalCachePresenter localCachePresenter;
    private DefaultCellTable<LocalCache> table;

    @Inject
    public LocalCacheView(ApplicationMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(LocalCache.class, propertyMetaData, dispatcher);
        setDescription(Console.CONSTANTS.subsys_infinispan_local_cache_desc());
    }

    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_infinispan_localCache();
    }

    @Override
    protected FormAdapter<LocalCache> makeAddEntityForm() {
        Form<LocalCache> form = new Form(beanType);
        form.setNumColumns(1);
        form.setFields(getFormMetaData().findAttribute("name").getFormItemForAdd(),
                       getFormMetaData().findAttribute("cacheContainer").getFormItemForAdd(this));
        return form;
    }

    @Override
    protected DefaultCellTable<LocalCache> makeEntityTable() {
        table = super.makeEntityTable();
        return table;
    }

    @Override
    protected ToolStrip createToolStrip() {
        ToolStrip toolStrip = super.createToolStrip();
        ToolButton clearBtn = new ToolButton(Console.CONSTANTS.subsys_infinispan_local_cache_clear_cache(),
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        final LocalCache localCache = (LocalCache) ((SingleSelectionModel)
                                table.getSelectionModel()).getSelectedObject();
                        localCachePresenter.clearCache(localCache.getCacheContainer(), localCache.getName());
                    }
                });

        // standalone only
        if (Console.getBootstrapContext().isStandalone())
            toolStrip.addToolButtonRight(clearBtn);
        return toolStrip;
    }

    @Override
    public void setPresenter(LocalCachePresenter presenter) {
        this.localCachePresenter = presenter;
    }
}
