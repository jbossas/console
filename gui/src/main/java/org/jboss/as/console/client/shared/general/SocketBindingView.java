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

package org.jboss.as.console.client.shared.general;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.general.model.SocketBinding;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.ballroom.client.widgets.forms.ComboBox;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/6/11
 */
public class SocketBindingView extends DisposableViewImpl implements SocketBindingPresenter.MyView {

    private SocketBindingPresenter presenter;

    private SocketTable socketTable;
    private ComboBox groupFilter;
    private ToolButton editBtn;
    private Form<SocketBinding> form;

    @Override
    public Widget createWidget() {
        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Socket Binding Groups");
        layout.add(titleBar);

        ToolStrip toolstrip = new ToolStrip();
        toolstrip.addToolButtonRight(new ToolButton("New Socket Binding", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewSocketDialogue();
            }
        }));

        layout.add(toolstrip);

        // -----------
        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        ContentHeaderLabel nameLabel = new ContentHeaderLabel("Socket Binding Declarations");

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        //Image image = new Image(Icons.INSTANCE.deployment());
        //horzPanel.add(image);
        //image.getElement().getParentElement().setAttribute("width", "25");

        horzPanel.add(nameLabel);

        panel.add(horzPanel);

        socketTable = new SocketTable();

        HorizontalPanel tableOptions = new HorizontalPanel();
        tableOptions.getElement().setAttribute("cellpadding", "2px");

        groupFilter = new ComboBox();
        groupFilter.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.onFilterGroup(event.getValue());
            }
        });
        Widget groupFilterWidget = groupFilter.asWidget();
        groupFilterWidget.getElement().setAttribute("style", "width:200px;");


        tableOptions.add(new Label("Socket Binding Group:"));
        tableOptions.add(groupFilterWidget);


        tableOptions.getElement().setAttribute("style", "float:right;");
        panel.add(tableOptions);
        DefaultCellTable socketTableWidget = socketTable.asWidget();
        panel.add(socketTableWidget);

        DefaultPager pager = new DefaultPager();
        pager.setPage(0);
        pager.setPageSize(6);
        pager.setDisplay(socketTableWidget);
        panel.add(pager);


        ScrollPanel scroll = new ScrollPanel(panel);
        layout.add(scroll);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(toolstrip, 28, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 58, Style.Unit.PX, 100, Style.Unit.PCT);

        // -----------

        ToolStrip detailToolStrip = new ToolStrip();
        editBtn = new ToolButton(Console.CONSTANTS.common_label_edit());
        ClickHandler editHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                SocketBinding editedEntity = form.getEditedEntity();
                if(editBtn.getText().equals(Console.CONSTANTS.common_label_edit())) {
                    presenter.editSocketBinding(editedEntity);
                }
                else {
                    presenter.saveSocketBinding(editedEntity.getName(), editedEntity.getGroup(), form.getChangedValues());
                }
            }
        };
        editBtn.addClickHandler(editHandler);
        detailToolStrip.addToolButton(editBtn);


        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                SocketBinding socket = form.getEditedEntity();

                Feedback.confirm(
                        "Delete Socket Binding",
                        "Really delete this socket binding '" + socket.getName() + "' ?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onDelete(form.getEditedEntity());
                                }
                            }
                        });
            }
        };
        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.addClickHandler(clickHandler);
        detailToolStrip.addToolButton(deleteBtn);

        panel.add(new ContentGroupLabel("Socket Binding"));

        panel.add(detailToolStrip);
        // ---

        form = new Form<SocketBinding>(SocketBinding.class);
        form.setNumColumns(2);

        TextItem nameItem = new TextItem("name", "Name");
        TextItem interfaceItem = new TextItem("interface", "Interface");
        NumberBoxItem portItem = new NumberBoxItem("port", "Port");
        TextBoxItem multicastItem = new TextBoxItem("multiCastAddress", "Multicast Address");
        NumberBoxItem multicastPortItem = new NumberBoxItem("multiCastPort", "Multicast Port");

        form.setFields(nameItem, interfaceItem, portItem, multicastItem, multicastPortItem);
        form.bind(socketTable.getCellTable());

        Widget formWidget = form.asWidget();
        form.setEnabled(false);


        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = new ModelNode();
                        address.add("socket-binding-group", form.getEditedEntity().getGroup());
                        address.add("socket-binding", "*");
                        return address;
                    }
                }, form
        );
        panel.add(helpPanel.asWidget());

        panel.add(formWidget);

        // ------------------------------------------

        return layout;
    }

    @Override
    public void setPresenter(SocketBindingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateGroups(List<String> groups) {
        groupFilter.setValues(groups);

        int i=0;
        for(String group : groups)
        {
            if(group.equals("standard-sockets"))
                break;
            i++;
        }

        groupFilter.setItemSelected(i, true);
    }

    @Override
    public void setBindings(String groupName, List<SocketBinding> bindings) {
        socketTable.updateFrom(groupName, bindings);
    }

    @Override
    public void setEnabled(boolean b) {
        form.setEnabled(b);

        String text = b ? "Save" : "Edit";
        editBtn.setText(text);
    }
}
