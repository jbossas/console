package org.jboss.as.console.client.domain.groups;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.LayoutPanel;
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

    LayoutPanel layout;

    public NewGroupWizard(final ServerGroupPresenter presenter, final List<ServerGroupRecord> existing) {
        layout = new LayoutPanel();
        layout.setStyleName("fill-layout-width");
        layout.getElement().setAttribute("style", "margin:15px;");

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

        basedOnSelection.setValueMap(exists);
        basedOnSelection.setDefaultToFirstOption(true);

        form.setFields(nameField, basedOnSelection);

        Button submit = new DefaultButton("Save");
        submit.getElement().setAttribute("style", "float:right");
        submit.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
              // merge base
                ServerGroupRecord newGroup = form.getUpdatedEntity();
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

                presenter.onNewGroup(newGroup);

            }
        });

        // ----------------------------------------

        Widget formWidget = form.asWidget();
        layout.add(formWidget);
        layout.add(submit);

        layout.setWidgetTopHeight(formWidget, 0, Style.Unit.PX, 150, Style.Unit.PX);
        layout.setWidgetTopHeight(submit, 160, Style.Unit.PX, 20, Style.Unit.PX);

    }

    public Widget asWidget() {
        return layout;
    }
}
