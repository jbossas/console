package org.jboss.as.console.client.shared.subsys.messaging.connections;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.messaging.forms.ConnectorServiceForm;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectorService;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 4/4/12
 */
public class NewConnectorServiceWizard {
    private MsgConnectionsPresenter presenter;

    public NewConnectorServiceWizard(MsgConnectionsPresenter presenter) {
        this.presenter = presenter;

    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.addStyleName("window-content");

        final ConnectorServiceForm form = new ConnectorServiceForm(new FormToolStrip.FormCallback<ConnectorService>() {
            @Override
            public void onSave(Map<String, Object> changeset) {

            }

            @Override
            public void onDelete(ConnectorService entity) {

            }
        });

        form.setIsCreate(true);

        layout.add(form.asWidget());

        DialogueOptions options = new DialogueOptions(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {

                        Form<ConnectorService> actualForm = form.getForm();
                        FormValidation validation = actualForm .validate();
                        if(!validation.hasErrors()) {
                            ConnectorService entity = actualForm.getUpdatedEntity();

                            presenter.onCreateConnectorService(entity);
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
