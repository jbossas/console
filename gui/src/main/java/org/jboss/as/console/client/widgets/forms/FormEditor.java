package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.dmr.client.ModelNode;


/**
 * @author Heiko Braun
 * @date 12/13/11
 */
public class FormEditor<T> {

    protected Form<T> form;
    protected FormToolStrip.FormCallback<T> callback;
    protected ModelNode helpAddress;

    public FormEditor(Class baseType) {
        form = new Form<T>(baseType);
    }

    public void setHelpAddress(ModelNode helpAddress) {
        this.helpAddress = helpAddress;
    }

    public void setCallback(FormToolStrip.FormCallback<T> callback) {
        this.callback = callback;
    }

    public Widget asWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");

        form.setNumColumns(2);
        form.setEnabled(false);

        FormToolStrip<T> tools = new FormToolStrip<T>(form, callback);

        tools.providesDeleteOp(false);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        return helpAddress;
                    }
                }, form
        );

        panel.add(tools.asWidget());
        panel.add(helpPanel.asWidget());
        panel.add(form.asWidget());

        return panel;
    }

    public Form<T> getForm() {
        return form;
    }
}
