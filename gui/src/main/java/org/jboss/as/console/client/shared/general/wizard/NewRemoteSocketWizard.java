package org.jboss.as.console.client.shared.general.wizard;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.general.SocketBindingPresenter;
import org.jboss.as.console.client.shared.general.forms.RemoteSocketForm;
import org.jboss.as.console.client.shared.general.model.RemoteSocketBinding;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 4/4/12
 */
public class NewRemoteSocketWizard {
    private SocketBindingPresenter presenter;

    public NewRemoteSocketWizard(SocketBindingPresenter presenter) {
        this.presenter = presenter;                
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.addStyleName("window-content");

        final RemoteSocketForm form = new RemoteSocketForm(new FormToolStrip.FormCallback<RemoteSocketBinding>() {
            @Override
            public void onSave(Map<String, Object> changeset) {

            }

            @Override
            public void onDelete(RemoteSocketBinding entity) {

            }
        }, false);

        form.setIsCreate(true);

        layout.add(form.asWidget());

        DialogueOptions options = new DialogueOptions(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {

                        Form<RemoteSocketBinding> actualForm = form.getForm();
                        FormValidation validation = actualForm .validate();
                        if(!validation.hasErrors()) {
                            RemoteSocketBinding entity = actualForm.getUpdatedEntity();
                            presenter.onCreateRemoteSocketBinding(entity);
                        }
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
