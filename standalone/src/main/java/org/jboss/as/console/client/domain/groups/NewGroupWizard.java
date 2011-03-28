package org.jboss.as.console.client.domain.groups;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.widgets.DefaultButton;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/1/11
 */
class NewGroupWizard {

    VerticalPanel layout;

    public NewGroupWizard(final ServerGroupPresenter presenter, final List<ServerGroupRecord> existing) {
        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.getElement().setAttribute("cellpadding", "10");

        final Form<ServerGroupRecord> form = new Form(ServerGroupRecord.class);

        TextBoxItem nameField = new TextBoxItem("groupName", "Group Name");

        final ComboBoxItem basedOnSelection = new ComboBoxItem("based-on", "Based On");

        String[] exists = new String[existing.size()];
        int i=0;
        for(ServerGroupRecord rec : existing)
        {
            exists[i] = rec.getGroupName();
            i++;
        }

        basedOnSelection.setDefaultToFirstOption(true);
        basedOnSelection.setValueMap(exists);

        form.setFields(nameField, basedOnSelection);

        Button submit = new DefaultButton("Save");
        submit.getElement().setAttribute("style", "width:50px;height:18px");
        submit.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // merge base
                ServerGroupRecord newGroup = form.getUpdatedEntity();
                if(newGroup.getGroupName().equals("")
                        || newGroup.getGroupName() == null) return;

                ServerGroupRecord base = null;
                for(ServerGroupRecord rec : existing)
                {
                    if(rec.getGroupName().equals(basedOnSelection.getValue()))
                    {
                        base = rec;
                        break;
                    }
                }

                newGroup.setJvm(base.getJvm());
                newGroup.setSocketBinding(base.getSocketBinding());
                newGroup.setProfileName(base.getProfileName());
                newGroup.setProperties(base.getProperties());

                presenter.createNewGroup(newGroup);

            }
        });


        Label cancel = new Label("Cancel");
        cancel.setStyleName("html-link");
        cancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        });

        HorizontalPanel options = new HorizontalPanel();
        options.getElement().setAttribute("style", "width:100%");

        HTML spacer = new HTML("&nbsp;");
        options.add(spacer);
        spacer.getElement().getParentElement().setAttribute("width", "100%");

        options.add(submit);
        options.add(spacer);
        options.add(cancel);
        cancel.getElement().getParentElement().setAttribute("style","vertical-align:middle");
        submit.getElement().getParentElement().setAttribute("align", "right");
        submit.getElement().getParentElement().setAttribute("width", "100%");

        // ----------------------------------------

        Widget formWidget = form.asWidget();

        layout.add(new HTML("Create a new server group based on an existing one. " +
                "The new group will inherit the properties of the selected group."));
        layout.add(formWidget);
        layout.add(options);

    }

    public Widget asWidget() {
        return layout;
    }
}
