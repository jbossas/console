package org.jboss.as.console.client.shared.subsys.messaging;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.messaging.forms.DefaultCFForm;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 4/3/12
 */
public class NewCFWizard {


    MsgDestinationsPresenter presenter;

    public NewCFWizard(MsgDestinationsPresenter  presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.addStyleName("window-content");

        final DefaultCFForm defaultAttributes = new DefaultCFForm(
                new FormToolStrip.FormCallback<ConnectionFactory>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {

                    }
                    @Override
                    public void onDelete(ConnectionFactory entity) {

                    }
                },false
        );

        defaultAttributes.getForm().setNumColumns(1);
        defaultAttributes.getForm().setEnabled(true);
        defaultAttributes.setIsCreate(true);

        layout.add(defaultAttributes.asWidget());

        DialogueOptions options = new DialogueOptions(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {

                        Form<ConnectionFactory> form = defaultAttributes.getForm();
                        FormValidation validation = form.validate();
                        if(!validation.hasErrors())
                            presenter.onCreateCF(form.getUpdatedEntity());
                    }
                },
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.closeDialogue();
                    }
                }
        );



        return new WindowContentBuilder(layout, options).build();

    }
}
