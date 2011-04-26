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

package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.PropertyTable;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.TitleBar;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.NumberBoxItem;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public class ServerConfigView extends SuspendableViewImpl implements ServerConfigPresenter.MyView{


    private ServerConfigPresenter presenter;
    private Form<Server> form;
    private ContentHeaderLabel nameLabel;
    private ComboBoxItem groupItem;
    private ComboBoxItem socketItem;
    private ComboBoxItem jvmItem;

    private LayoutPanel layout;
    private ToolButton edit;

    @Override
    public void setPresenter(ServerConfigPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        layout = new LayoutPanel();

        TitleBar titleBar = new TitleBar("Server Configuration");
        layout.add(titleBar);

        // ----

        final ToolStrip toolStrip = new ToolStrip();
        edit = new ToolButton("Edit");
        edit.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                if(edit.getText().equals("Edit"))
                {
                    onEdit();
                }
                else
                {
                    onSave();
                }
            }
        });

        toolStrip.addToolButton(edit);
        ToolButton delete = new ToolButton("Delete");
        delete.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                Feedback.confirm(
                        "Delete Server Configuration",
                        "Do you want to delete server config '"+form.getEditedEntity().getName()+"'?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if(isConfirmed)
                                    presenter.deleteCurrentRecord();
                            }
                        });
            }
        });

        toolStrip.addToolButton(delete);
        toolStrip.addToolButtonRight(new ToolButton("Create New", new ClickHandler(){
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewConfigDialoge();
            }
        }));

        layout.add(toolStrip);

        // ---

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");
        panel.getElement().setAttribute("style", "padding:15px;");

        layout.add(panel);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(toolStrip, 28, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(panel, 58, Style.Unit.PX, 100, Style.Unit.PCT);


        // --------------------------------------------------------

        nameLabel = new ContentHeaderLabel("Name here ...");

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.server());
        horzPanel.add(image);
        horzPanel.add(nameLabel);
        image.getElement().getParentElement().setAttribute("width", "25");

        panel.add(horzPanel);

        // ----------------------------------------------------------------------


        panel.add(new ContentGroupLabel("Attributes"));

        form = new Form<Server>(Server.class);
        form.setNumColumns(2);

        TextItem nameItem = new TextItem("name", "Server Name");
        CheckBoxItem startedItem = new CheckBoxItem("autoStart", "Start Instances?");
        groupItem = new ComboBoxItem("group", "Server Group");

        // ------------------------------------------------------

        socketItem = new ComboBoxItem("socketBinding", "Socket Binding");
        NumberBoxItem portOffset = new NumberBoxItem("portOffset", "Port Offset");

        //jvmItem = new ComboBoxItem("jvm", "Virtual Machine");

        form.setFields(nameItem, startedItem, groupItem);
        form.setFieldsInGroup(
                "Advanced",
                new DisclosureGroupRenderer(),
                socketItem, portOffset
        );

        panel.add(form.asWidget());
        form.setEnabled(false);

        // ------------------------------------------------------

        panel.add(new ContentGroupLabel("System Properties"));

        PropertyTable properties = new PropertyTable();
        panel.add(properties);

        return layout;
    }

    private void onSave() {
        Server updatedEntity = form.getUpdatedEntity();
        presenter.onSaveChanges(updatedEntity.getName(), form.getChangedValues());
    }

    private void onEdit() {
        presenter.editCurrentRecord();
    }

    @Override
    public void setSelectedRecord(Server selectedRecord) {
        nameLabel.setText(selectedRecord.getName());
        form.edit(selectedRecord);
    }

    @Override
    public void updateServerGroups(List<ServerGroupRecord> serverGroupRecords) {

        String[] names = new String[serverGroupRecords.size()];
        int i=0;
        for(ServerGroupRecord group : serverGroupRecords)
        {
            names[i] = group.getGroupName();
            i++;
        }
        groupItem.setValueMap(names);
    }

    @Override
    public void updateSocketBindings(List<String> result) {
        socketItem.setValueMap(result);
    }

    @Override
    public void updateVirtualMachines(List<String> result) {
        //jvmItem.setValueMap(result);
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        form.setEnabled(isEnabled);

        edit.setText(
                isEnabled ? "Save" : "Edit"
        );
    }
}
