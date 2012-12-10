package org.jboss.as.console.client.domain.groups;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 10/22/12
 */
public class CopyGroupWizard {
    private ServerGroupPresenter presenter;
    private ServerGroupRecord orig;

    public CopyGroupWizard(ServerGroupPresenter presenter, ServerGroupRecord orig) {
        this.presenter = presenter;
        this.orig = orig;
    }

    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");


        layout.add(new ContentDescription("<h3>Create copy</h3> You are about to create a copy of server-goup <b>'"+orig.getGroupName()+
                "'</b>. The newly created group will inherit all properties of the original."));

        final Form<ServerGroupRecord> form = new Form<ServerGroupRecord>(ServerGroupRecord.class);
        form.setNumColumns(1);

        TextBoxItem nameItem = new TextBoxItem("groupName", Console.CONSTANTS.common_label_name())
        {
            @Override
            public boolean validate(String value) {
                boolean hasValue = super.validate(value);
                boolean hasWhitespace = value.contains(" ");
                return hasValue && !hasWhitespace;
            }

            @Override
            public String getErrMessage() {
                return Console.MESSAGES.common_validation_notEmptyNoSpace();
            }
        };

        nameItem.setValue(orig.getGroupName()+"_copy");

        form.setFields(nameItem);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = new ModelNode();
                        address.add("host", Console.MODULES.getDomainEntityManager().getSelectedHost());
                        address.add("server-group", "*");
                        return address;
                    }
                }, form
        );
        layout.add(helpPanel.asWidget());

        layout.add(form.asWidget());


        // ---

        ClickHandler saveHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final ServerGroupRecord newGroup = form.getUpdatedEntity();

                FormValidation validation = form.validate();
                if (!validation.hasErrors())
                {
                    presenter.onSaveCopy(orig, newGroup);
                }
            }
        };


        ClickHandler cancelHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.closeDialoge();
            }
        };

        DialogueOptions options = new DialogueOptions(saveHandler, cancelHandler);

        return new WindowContentBuilder(layout, options).build();

    }

}
