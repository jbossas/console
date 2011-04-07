/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.widgets.DefaultButton;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.FormValidation;
import org.jboss.as.console.client.widgets.forms.NumberBoxItem;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/28/11
 */
public class NewServerConfigWizard {

    private VerticalPanel layout;

    private ServerConfigPresenter presenter;
    private ComboBoxItem groupItem;

    public NewServerConfigWizard(final ServerConfigPresenter presenter, List<ServerGroupRecord> serverGroups) {
        this.presenter = presenter;

        layout = new VerticalPanel();

        final Form<Server> form = new Form<Server>(Server.class);
        form.setNumColumns(1);

        TextBoxItem nameItem = new TextBoxItem("name", "Server Name")
        {
            @Override
            public boolean validate(String value) {
                boolean hasValue = super.validate(value);
                boolean hasWhitespace = value.contains(" ");
                return hasValue && !hasWhitespace;
            }

            @Override
            public String getErrMessage() {
                return "Not empty, no whitespace";
            }
        };

        CheckBoxItem startedItem = new CheckBoxItem("autoStart", "Start Instances?");
        NumberBoxItem portOffset = new NumberBoxItem("portOffset", "Port Offset");


        List<String> groups = new ArrayList<String>(serverGroups.size());
        for(ServerGroupRecord rec : serverGroups)
            groups.add(rec.getGroupName());

        groupItem = new ComboBoxItem("group", "Server Group");
        groupItem.setDefaultToFirstOption(true);
        groupItem.setValueMap(groups);

        form.setFields(nameItem, groupItem, portOffset, startedItem);

        layout.add(form.asWidget());


        // ---

        DefaultButton submit = new DefaultButton("Save", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Server newServer = form.getUpdatedEntity();

                FormValidation validation = form.validate();
                if(validation.hasErrors())
                    return;

                presenter.createServerConfig(newServer);

            }
        });


        Label cancel = new Label("Cancel");
        cancel.setStyleName("html-link");
        cancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.closeDialoge();
            }
        });

        HorizontalPanel options = new HorizontalPanel();
        options.getElement().setAttribute("style", "width:100%");

        HTML spacer = new HTML("&nbsp;");
        options.add(spacer);
        //spacer.getElement().getParentElement().setAttribute("width", "100%");

        options.add(submit);
        options.add(spacer);
        options.add(cancel);
        cancel.getElement().getParentElement().setAttribute("style","vertical-align:middle");
        submit.getElement().getParentElement().setAttribute("align", "right");
        submit.getElement().getParentElement().setAttribute("width", "100%");


        layout.add(options);

    }

    public Widget asWidget() {

        return layout;
    }
}
