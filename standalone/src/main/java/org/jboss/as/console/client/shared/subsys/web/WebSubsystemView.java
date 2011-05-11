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

package org.jboss.as.console.client.shared.subsys.web;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.subsys.web.model.HttpConnector;
import org.jboss.as.console.client.shared.subsys.web.model.JSPContainerConfiguration;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.NumberBoxItem;
import org.jboss.as.console.client.widgets.forms.StateItem;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.icons.Icons;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/11/11
 */
public class WebSubsystemView extends DisposableViewImpl implements WebPresenter.MyView{

    private WebPresenter presenter;
    private Form<JSPContainerConfiguration> form;
    private ConnectorList connectorList;

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new RHSContentPanel("HTTP");

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.messaging());
        horzPanel.add(image);
        horzPanel.add(new ContentHeaderLabel("Web Subsystem Configuration"));
        image.getElement().getParentElement().setAttribute("width", "25");

        layout.add(horzPanel);

        // ----

        layout.add(new ContentGroupLabel("JSP Container"));

        form = new Form(JSPContainerConfiguration.class);
        form.setNumColumns(2);

        CheckBoxItem listing = new CheckBoxItem("listings", "Listings?");

        StateItem disabled= new StateItem("disabled", "Disabled?");

        CheckBoxItem development= new CheckBoxItem("development", "Development?");

        CheckBoxItem keepGenerated= new CheckBoxItem("keepGenerated", "Keep Generated?");

        NumberBoxItem checkInterval = new NumberBoxItem("checkInterval", "Check Interval");

        TextBoxItem workDir = new TextBoxItem("scratchDir", "Work Dir");

        CheckBoxItem poweredBy= new CheckBoxItem("poweredBy", "Advertise JSP Engine?");

        CheckBoxItem sourceFragment= new CheckBoxItem("sourceFragment", "Display Source?");


        form.setFields(disabled, development);
        form.setFieldsInGroup("Advanced", new DisclosureGroupRenderer(), listing, keepGenerated, checkInterval, workDir, poweredBy, sourceFragment);

        layout.add(form.asWidget());

        // ----

        layout.add(new ContentGroupLabel("Subresources"));
        TabPanel bottomLayout = new TabPanel();
        bottomLayout.addStyleName("default-tabpanel");
        bottomLayout.getElement().setAttribute("style", "padding-top:20px;");


        connectorList = new ConnectorList(presenter);
        bottomLayout.add(connectorList.asWidget(),"Connectors");

        bottomLayout.add(new HTML(),"Virtual Servers");

        bottomLayout.add(new HTML(),"Rewrite Rules");

        bottomLayout.selectTab(0);

        layout.add(bottomLayout);

        return layout;
    }

    @Override
    public void setPresenter(WebPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setConnectors(List<HttpConnector> connectors) {
        connectorList.setConnectors(connectors);
    }
}
