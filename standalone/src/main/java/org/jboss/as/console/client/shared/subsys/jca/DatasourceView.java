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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/24/11
 */
public class DatasourceView extends SuspendableViewImpl implements DataSourcePresenter.MyView {

    private DataSourcePresenter presenter;
    private DataSourceEditor dataSourceEditor;

    @Override
    public Widget createWidget() {

        this.dataSourceEditor = new DataSourceEditor(presenter);

        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");


        tabLayoutpanel.add(dataSourceEditor.asWidget(), "Data Sources");
        tabLayoutpanel.add(new HTML("Not implemented yet"), "XA Data Sources");

        tabLayoutpanel.selectTab(0);

        return tabLayoutpanel;
    }

    @Override
    public void setPresenter(DataSourcePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateDataSources(List<DataSource> datasources) {
        dataSourceEditor.updateDataSources(datasources);
    }

    @Override
    public void setEnabled(boolean b) {
        dataSourceEditor.setEnabled(b);
    }
}
