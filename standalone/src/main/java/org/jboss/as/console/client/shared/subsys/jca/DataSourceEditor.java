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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.SplitEditorPanel;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class DataSourceEditor {

    private DataSourcePresenter presenter;
    private DatasourceTable dataSourceTable;
    private DataSourceDetails details;

    public DataSourceEditor(DataSourcePresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {

        LayoutPanel layout = new LayoutPanel();

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.subsys_jca_newDataSource(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewDatasourceWizard();
            }
        }));

        layout.add(topLevelTools);

        // ----

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.getElement().setAttribute("style", "margin:15px; width:95%");

        ScrollPanel scroll = new ScrollPanel(vpanel);
        layout.add(scroll);

        layout.setWidgetTopHeight(topLevelTools, 0, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 30, Style.Unit.PX, 100, Style.Unit.PCT);

        // ---

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.database());
        horzPanel.add(image);
        horzPanel.add(new ContentHeaderLabel("Datasource Configurations"));
        image.getElement().getParentElement().setAttribute("width", "25");

        vpanel.add(horzPanel);

        dataSourceTable = new DatasourceTable();

        vpanel.add(new ContentGroupLabel("Registered Datasources"));
        vpanel.add(dataSourceTable.asWidget());


        // -----------
        details = new DataSourceDetails(presenter);
        details.bind(dataSourceTable.getCellTable());

        TabPanel bottomPanel = new TabPanel();
        bottomPanel.setStyleName("default-tabpanel");

        bottomPanel.add(details.asWidget(), "Attributes");
        bottomPanel.add(new HTML("todo"), "Metrics");
        bottomPanel.selectTab(0);

        vpanel.add(new ContentGroupLabel("Datasource"));
        vpanel.add(bottomPanel);

        return layout;
    }


    public void updateDataSources(List<DataSource> datasources) {
        dataSourceTable.getDataProvider().setList(datasources);

        if(!datasources.isEmpty())
            dataSourceTable.getCellTable().getSelectionModel().setSelected(datasources.get(0), true);

    }

    public void setEnabled(boolean isEnabled) {
        details.setEnabled(isEnabled);
    }
}
