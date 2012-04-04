package org.jboss.as.console.client.shared.subsys.messaging.connections;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.messaging.forms.AcceptorForm;
import org.jboss.as.console.client.shared.subsys.messaging.forms.DivertForm;
import org.jboss.as.console.client.shared.subsys.messaging.model.Acceptor;
import org.jboss.as.console.client.shared.subsys.messaging.model.AcceptorType;
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
 * @date 4/4/12
 */
public class NewAcceptorWizard {
    private MsgConnectionsPresenter presenter;
    private List<String> socketBindings;
    private AcceptorType type;

    public NewAcceptorWizard(MsgConnectionsPresenter presenter, List<String> socketBindings, AcceptorType type) {
        this.presenter = presenter;
        this.socketBindings = socketBindings;
        this.type = type;
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.addStyleName("window-content");

        final AcceptorForm form = new AcceptorForm(new FormToolStrip.FormCallback<Acceptor>() {
            @Override
            public void onSave(Map<String, Object> changeset) {

            }

            @Override
            public void onDelete(Acceptor entity) {

            }
        }, type);

        form.setIsCreate(true);

        layout.add(form.asWidget());

        DialogueOptions options = new DialogueOptions(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {

                        Form<Acceptor> actualForm = form.getForm();
                        FormValidation validation = actualForm .validate();
                        if(!validation.hasErrors()) {
                            Acceptor entity = actualForm.getUpdatedEntity();
                            entity.setType(type);
                            presenter.onCreateAcceptor(entity);
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
