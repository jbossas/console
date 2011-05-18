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

package org.jboss.as.console.client.domain.groups;

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
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.TitleBar;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * Shows an editable view of a single server group.
 *
 * @author Heiko Braun
 * @date 2/16/11
 */
public class ServerGroupView extends SuspendableViewImpl implements ServerGroupPresenter.MyView {

    private ServerGroupPresenter presenter;
    private Form<ServerGroupRecord> form;
    private ContentHeaderLabel nameLabel;
    private ComboBoxItem socketBindingItem;
    private ToolButton edit;

    private VerticalPanel panel;

    PropertyEditor propertyEditor;
    JvmEditor jvmEditor;

    @Override
    public void setPresenter(ServerGroupPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        TitleBar titleBar = new TitleBar(Console.CONSTANTS.common_label_serverGroup());
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
        ToolButton delete = new ToolButton(Console.CONSTANTS.common_label_delete());
        delete.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                Feedback.confirm(
                        Console.MESSAGES.deleteServerGroup(),
                        Console.MESSAGES.deleteServerGroupConfirm(form.getEditedEntity().getGroupName()),
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

        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_newServerGroup(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewGroupDialoge();
            }
        }));

        layout.add(toolStrip);

        // ----

        panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");
        panel.getElement().setAttribute("style", "padding:15px;");

        ScrollPanel scroll = new ScrollPanel(panel);
        layout.add(scroll);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(toolStrip, 28, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 58, Style.Unit.PX, 100, Style.Unit.PCT);

        // ---------------------------------------------

        nameLabel = new ContentHeaderLabel("");

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.serverGroup());
        horzPanel.add(image);
        horzPanel.add(nameLabel);
        image.getElement().getParentElement().setAttribute("width", "25");

        panel.add(horzPanel);

        // ---------------------------------------------------

        form = new Form<ServerGroupRecord>(ServerGroupRecord.class);
        form.setNumColumns(2);

        TextItem nameField = new TextItem("groupName", Console.CONSTANTS.common_label_name());
        //jvmField = new ComboBoxItem("jvm", "Virtual Machine");
        //jvmField.setValueMap(new String[] {"default"}); // TODO: https://issues.jboss.org/browse/JBAS-9156

        socketBindingItem = new ComboBoxItem("socketBinding", Console.CONSTANTS.common_label_socketBinding());
        socketBindingItem.setDefaultToFirstOption(true);

        // TODO: https://issues.jboss.org/browse/AS7-663
        //profileItem = new ComboBoxItem("profileName", "Profile");
        TextItem profileItem = new TextItem("profileName", Console.CONSTANTS.common_label_profile());

        form.setFields(nameField, profileItem, socketBindingItem);
        //form.setFieldsInGroup("Advanced", new DisclosureGroupRenderer(), socketBindingItem);

        panel.add(new ContentGroupLabel(Console.CONSTANTS.common_label_attributes()));

        panel.add(form.asWidget());

        // ---------------------------------------------------


        TabPanel bottomLayout = new TabPanel();
        bottomLayout.addStyleName("default-tabpanel");


        jvmEditor = new JvmEditor(presenter);
        bottomLayout .add(jvmEditor.asWidget(), Console.CONSTANTS.common_label_virtualMachine());

        propertyEditor = new PropertyEditor(presenter);
        bottomLayout.add(propertyEditor.asWidget(), Console.CONSTANTS.common_label_systemProperties());

        bottomLayout .selectTab(0);

        panel.add(new ContentGroupLabel("Subresources"));
        panel.add(bottomLayout);

        return layout;
    }

    private void onSave() {
        ServerGroupRecord updatedEntity = form.getUpdatedEntity();
        presenter.onSaveChanges(updatedEntity.getGroupName(), form.getChangedValues());
    }

    private void onEdit() {
        presenter.editCurrentRecord();
    }


    public void setSelectedRecord(final ServerGroupRecord record) {

        // title
        final String selectedGroupName = record.getGroupName();
        nameLabel.setHTML(selectedGroupName);

        // form
        form.edit(record);

        propertyEditor.setProperties(record.getGroupName(), record.getProperties());
        jvmEditor.setSelectedRecord(record.getGroupName(), record.getJvm());
    }

    @Override
    public void updateProfiles(List<ProfileRecord> result) {

        /*

        TODO: https://issues.jboss.org/browse/AS7-663

        List<String> names = new ArrayList<String>(result.size());
        for(ProfileRecord rec : result)
            names.add(rec.getName());

        profileItem.setValueMap(names);*/
    }

    public void setEnabled(boolean isEnabled) {

        if(isEnabled)
            panel.addStyleName("edit-panel");
        else
            panel.removeStyleName("edit-panel");

        form.setEnabled(isEnabled);


        edit.setText(
            isEnabled ? Console.CONSTANTS.common_label_save() : Console.CONSTANTS.common_label_edit()
        );
    }

    @Override
    public void updateSocketBindings(List<String> result) {
        socketBindingItem.setValueMap(result);
    }
}
