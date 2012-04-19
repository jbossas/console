package org.jboss.as.console.client.shared.subsys.messaging.cluster;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.messaging.forms.ClusterConnectionForm;
import org.jboss.as.console.client.shared.subsys.messaging.model.ClusterConnection;
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
public class NewClusterConnectionWizard {
    private MsgClusteringPresenter presenter;
    private List<String> names;

    public NewClusterConnectionWizard(MsgClusteringPresenter presenter, List<String> names) {
        this.presenter = presenter;
        this.names = names;
        
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.addStyleName("window-content");

        final ClusterConnectionForm form = new ClusterConnectionForm(new FormToolStrip.FormCallback<ClusterConnection>() {
            @Override
            public void onSave(Map<String, Object> changeset) {

            }

            @Override
            public void onDelete(ClusterConnection entity) {

            }
        }, false);

        form.setIsCreate(true);
        form.setSocketBindings(names);

        layout.add(form.asWidget());

        DialogueOptions options = new DialogueOptions(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {

                        Form<ClusterConnection> actualForm = form.getForm();
                        FormValidation validation = actualForm .validate();
                        if(!validation.hasErrors()) {
                            ClusterConnection entity = actualForm.getUpdatedEntity();
                            presenter.onCreateClusterConnection(entity);
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
