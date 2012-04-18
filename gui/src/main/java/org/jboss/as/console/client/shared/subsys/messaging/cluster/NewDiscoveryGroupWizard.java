package org.jboss.as.console.client.shared.subsys.messaging.cluster;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.messaging.forms.DiscoveryGroupForm;
import org.jboss.as.console.client.shared.subsys.messaging.model.DiscoveryGroup;
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
public class NewDiscoveryGroupWizard {
    private MsgClusteringPresenter presenter;
    private List<String> names;

    public NewDiscoveryGroupWizard(MsgClusteringPresenter presenter, List<String> names) {
        this.presenter = presenter;
        this.names = names;
        
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.addStyleName("window-content");

        final DiscoveryGroupForm form = new DiscoveryGroupForm(new FormToolStrip.FormCallback<DiscoveryGroup>() {
            @Override
            public void onSave(Map<String, Object> changeset) {

            }

            @Override
            public void onDelete(DiscoveryGroup entity) {

            }
        }, false);

        form.setIsCreate(true);
        form.setSocketBindings(names);

        layout.add(form.asWidget());

        DialogueOptions options = new DialogueOptions(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {

                        Form<DiscoveryGroup> actualForm = form.getForm();
                        FormValidation validation = actualForm .validate();
                        if(!validation.hasErrors()) {
                            DiscoveryGroup entity = actualForm.getUpdatedEntity();
                            presenter.onCreateDiscoveryGroup(entity);
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
