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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.jvm.JvmEditor;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public class ServerConfigView extends SuspendableViewImpl implements ServerConfigPresenter.MyView{


    private ServerConfigPresenter presenter;
    private Form<Server> form;
    private ContentHeaderLabel nameLabel;
    private ComboBoxItem socketItem;
    private ToolButton edit;

    private JvmEditor jvmEditor;
    private PropertyEditor propertyEditor;
    private ToolButton cancelBtn = null;

    @Override
    public void setPresenter(ServerConfigPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel(Console.CONSTANTS.common_label_serverConfig());
        layout.add(titleBar);

        // ----

        final ToolStrip toolStrip = new ToolStrip();
        edit = new ToolButton(Console.CONSTANTS.common_label_edit());
        edit.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                if(edit.getText().equals(Console.CONSTANTS.common_label_edit()))
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

        cancelBtn = new ToolButton(Console.CONSTANTS.common_label_cancel());
        cancelBtn.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                cancelBtn.setVisible(false);
                form.cancel();
                form.setEnabled(false);
                edit.setText(Console.CONSTANTS.common_label_edit());
            }
        });
        toolStrip.addToolButton(cancelBtn);
        cancelBtn.setVisible(false);

        ToolButton delete = new ToolButton(Console.CONSTANTS.common_label_delete());
        delete.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                Feedback.confirm(
                        Console.MESSAGES.deleteServerConfig(),
                        Console.MESSAGES.deleteServerConfigConfirm(form.getEditedEntity().getName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if(isConfirmed)
                                    presenter.tryDeleteCurrentRecord();
                            }
                        });
            }
        });


        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler(){
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewConfigDialoge();
            }
        }));

        toolStrip.addToolButtonRight(delete);

        toolStrip.addToolButtonRight(new ToolButton("Ports", new ClickHandler(){
            @Override
            public void onClick(ClickEvent event) {
                presenter.onShowEffectivePorts();
            }
        }));


        layout.add(toolStrip);

        // ---

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        ScrollPanel scrollPanel = new ScrollPanel(panel);
        layout.add(scrollPanel);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(toolStrip, 28, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scrollPanel, 58, Style.Unit.PX, 100, Style.Unit.PCT);


        // --------------------------------------------------------

        nameLabel = new ContentHeaderLabel("");

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.server());
        horzPanel.add(image);
        horzPanel.add(nameLabel);
        image.getElement().getParentElement().setAttribute("width", "25");

        panel.add(horzPanel);

        // ----------------------------------------------------------------------


        panel.add(new ContentGroupLabel(Console.CONSTANTS.common_label_attributes()));

        form = new Form<Server>(Server.class);
        form.setNumColumns(2);

        TextItem nameItem = new TextItem("name", "Name");

        CheckBoxItem startedItem = new CheckBoxItem("autoStart", Console.CONSTANTS.common_label_autoStart());
        //groupItem = new ComboBoxItem("group", "Server Group");

        // TODO: https://issues.jboss.org/browse/AS7-661
        TextItem groupItem = new TextItem("group", Console.CONSTANTS.common_label_serverGroup());

        // ------------------------------------------------------

        final NumberBoxItem portOffset = new NumberBoxItem("portOffset", Console.CONSTANTS.common_label_portOffset());

        socketItem = new ComboBoxItem("socketBinding", Console.CONSTANTS.common_label_socketBinding())
        {
            @Override
            public boolean validate(String value) {
                boolean parentValid = super.validate(value);
                //boolean portDefined = !portOffset.isModified();
                return parentValid ;//&& portDefined;
            }

            @Override
            public String getErrMessage() {
                return Console.MESSAGES.common_validation_portOffsetUndefined(super.getErrMessage());
            }
        };


        form.setFields(nameItem, groupItem, startedItem);
        form.setFieldsInGroup(
                "Advanced",
                new DisclosureGroupRenderer(),
                socketItem, portOffset
        );


        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = new ModelNode();
                        address.add("host", presenter.getSelectedHost());
                        address.add("server-config", "*");
                        return address;
                    }
                }, form
        );
        panel.add(helpPanel.asWidget());

        panel.add(form.asWidget());
        form.setEnabled(false);

        // ------------------------------------------------------

        TabPanel bottomLayout = new TabPanel();
        bottomLayout.addStyleName("default-tabpanel");

        // jvm editor
        jvmEditor = new JvmEditor(presenter);
        jvmEditor.setAddressCallback(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = new ModelNode();
                address.add("host", presenter.getSelectedHost());
                address.add("server-config", nameLabel.getText());
                address.add("jvm", "*");
                return address;
            }
        });
        bottomLayout.add(jvmEditor.asWidget(), Console.CONSTANTS.common_label_virtualMachine());

        propertyEditor = new PropertyEditor(presenter);
        propertyEditor.setHelpText("A system property to set on this server.");
        bottomLayout.add(propertyEditor.asWidget(), Console.CONSTANTS.common_label_systemProperties());
        propertyEditor.setAllowEditProps(false);


        panel.add(new ContentGroupLabel("Subresources"));

        panel.add(bottomLayout);

        bottomLayout.selectTab(0);

        return layout;
    }

    private void onSave() {

        FormValidation validation = form.validate();

        if(!validation.hasErrors())
        {
            cancelBtn.setVisible(true);

            Server updatedEntity = form.getUpdatedEntity();

            System.out.println(updatedEntity.getName());

            Map<String,Object> changedValues = form.getChangedValues();

            // https://issues.jboss.org/browse/AS7-662
            if(changedValues.containsKey("portOffset"))
                changedValues.put("socketBinding", updatedEntity.getSocketBinding());
            else if(changedValues.containsKey("socketBinding"))
                changedValues.put("portOffset", updatedEntity.getPortOffset());

            presenter.onSaveChanges(updatedEntity.getName(), changedValues);
        }
    }

    private void onEdit() {
        presenter.editCurrentRecord();
        cancelBtn.setVisible(true);
    }

    @Override
    public void setSelectedRecord(Server selectedRecord) {

        nameLabel.setText(selectedRecord.getName());
        form.edit(selectedRecord);

        jvmEditor.setSelectedRecord(selectedRecord.getName(), selectedRecord.getJvm());
        propertyEditor.setProperties(selectedRecord.getName(), selectedRecord.getProperties());
    }

    @Override
    public void updateServerGroups(List<ServerGroupRecord> serverGroupRecords) {

        /*

        TODO: https://issues.jboss.org/browse/AS7-661

        String[] names = new String[serverGroupRecords.size()];
        int i=0;
        for(ServerGroupRecord group : serverGroupRecords)
        {
            names[i] = group.getGroupName();
            i++;
        }
        groupItem.setValueMap(names);*/
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
                isEnabled ? Console.CONSTANTS.common_label_save() : Console.CONSTANTS.common_label_edit()
        );
    }
}
