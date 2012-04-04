package org.jboss.as.console.client.shared.subsys.messaging;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.messaging.forms.DivertForm;
import org.jboss.as.console.client.shared.subsys.messaging.model.Divert;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 4/3/12
 */
public class NewDivertWizard {


    private MsgDestinationsPresenter presenter;
    private List<String> queueNames;

    public NewDivertWizard(MsgDestinationsPresenter presenter, List<String> queueNames) {
        this.presenter = presenter;
        this.queueNames = queueNames;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.addStyleName("window-content");

        final DivertForm divertForm = new DivertForm(
                new FormToolStrip.FormCallback<Divert>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {

                    }
                    @Override
                    public void onDelete(Divert entity) {

                    }
                },false
        );

        divertForm.setQueueNames(queueNames);
        divertForm.getForm().setNumColumns(1);
        divertForm.setIsCreate(true);

        layout.add(divertForm.asWidget());

        DialogueOptions options = new DialogueOptions(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {

                        Form<Divert> form = divertForm.getForm();
                        FormValidation validation = form.validate();
                        if(!validation.hasErrors())
                            presenter.onCreateDivert(form.getUpdatedEntity());
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
