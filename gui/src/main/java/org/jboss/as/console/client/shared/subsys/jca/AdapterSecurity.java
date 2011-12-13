package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.jca.model.ConnectionDefinition;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 12/12/11
 */
public class AdapterSecurity {


    private ResourceAdapterPresenter presenter;
    private Form<ConnectionDefinition> form;

    public AdapterSecurity(ResourceAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");


        form = new Form<ConnectionDefinition>(ConnectionDefinition.class);

        TextBoxItem domain = new TextBoxItem("securityDomain", "Security Domain") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };
        TextBoxItem application = new TextBoxItem("application", "Application") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };
        TextBoxItem domainApplication = new TextBoxItem("domainAndApplication", "Domain And Application")
        {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        form.setFields(domain, application, domainApplication);
        form.setEnabled(false);
        form.setNumColumns(2);

        FormToolStrip<ConnectionDefinition> tools = new FormToolStrip<ConnectionDefinition>(
                form,

                new FormToolStrip.FormCallback<ConnectionDefinition>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveConnectionSecurity(form.getEditedEntity(), changeset);
                    }

                    @Override
                    public void onDelete(ConnectionDefinition entity) {

                    }
                }
        );

        tools.providesDeleteOp(false);

        layout.add(tools.asWidget());
        layout.add(form.asWidget());
        return layout;
    }

    public void updateFrom(ConnectionDefinition selectedObject) {
        form.edit(selectedObject);
    }
}
