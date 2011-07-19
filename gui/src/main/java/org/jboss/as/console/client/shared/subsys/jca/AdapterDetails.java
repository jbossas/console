package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.ballroom.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

/**
 * @author Heiko Braun
 * @date 7/19/11
 */
public class AdapterDetails {

    private VerticalPanel layout;
    private Form<ResourceAdapter> form;
    private ToolButton editBtn;

    public AdapterDetails() {
        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        form = new Form<ResourceAdapter>(ResourceAdapter.class);


        ToolStrip detailToolStrip = new ToolStrip();
        editBtn = new ToolButton(Console.CONSTANTS.common_label_edit());
        ClickHandler editHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(editBtn.getText().equals(Console.CONSTANTS.common_label_edit()))
                    //TODO
                    System.out.println("edit");
                else
                    //TODO
                    System.out.println("save");
            }
        };
        editBtn.addClickHandler(editHandler);
        detailToolStrip.addToolButton(editBtn);


        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                ResourceAdapter ra = form.getEditedEntity();

                Feedback.confirm(
                        "Delete Resource Adapter",
                        "Really delete this Adapter'" + ra.getName() + "' ?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    // TODO
                                }
                            }
                        });
            }
        };
        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.addClickHandler(clickHandler);
        detailToolStrip.addToolButton(deleteBtn);

        layout.add(detailToolStrip);

        // ----

        TextItem nameItem = new TextItem("name", "Name");
        TextBoxItem poolItem = new TextBoxItem("poolName", "Pool");
        TextBoxItem jndiItem = new TextBoxItem("jndiName", "JNDI");
        TextItem archiveItem = new TextItem("archive", "Archive");

        TextBoxItem txItem = new TextBoxItem("transactionSupport", "TX");
        TextBoxItem classItem = new TextBoxItem("connectionClass", "Connection Class");


        form.setFields(nameItem, jndiItem, poolItem, archiveItem);
        form.setFieldsInGroup("Advanced", new DisclosureGroupRenderer(), txItem, classItem);

        layout.add(form.asWidget());

        form.setEnabled(false   );

    }

    Widget asWidget() {
        return layout;
    }

    public Form<ResourceAdapter> getForm() {
        return form;
    }
}
