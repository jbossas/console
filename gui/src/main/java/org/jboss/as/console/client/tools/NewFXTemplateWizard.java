package org.jboss.as.console.client.tools;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.ListItem;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 7/23/12
 */
public class NewFXTemplateWizard {

    private FXTemplatesPresenter presenter;

    public NewFXTemplateWizard(FXTemplatesPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.addStyleName("window-content");

        final SimpleForm form = new SimpleForm();
        form.setEnabled(true);

        final TextAreaItem name = new TextAreaItem("name", "Name", true);
        final TextAreaItem address = new TextAreaItem("address", "Address", true);
        final ComboBoxItem type = new ComboBoxItem("execType", "ExecType")
        {
            @Override
            public boolean isRequired() {
                return true;
            }
        };

        type.setValueMap(new String[] {
                FXModel.ExecutionType.CREATE.name(),
                FXModel.ExecutionType.UPDATE.name(),
                FXModel.ExecutionType.DELETE.name()
        });

        type.setDefaultToFirstOption(true);
        type.selectItem(1);

        final ListItem fieldNames = new ListItem("fieldNames", "FieldNames")
        {
            @Override
            public boolean isRequired() {
                return false;
            }
        };


        form.setFields(name, address, type, fieldNames);
        layout.add(form.asWidget());

        DialogueOptions options = new DialogueOptions(
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {

                        final FormValidation validation = form.validate();
                        ModelNode addressNode = new ModelNode();
                        try {
                            List<String[]> tuple = AddressBinding.parseAddressString(address.getValue());
                            addressNode = new AddressBinding(tuple).asResource().get("address");
                        } catch (Throwable e) {
                            validation.addError("Invalid address value");
                            address.setErroneous(true);
                        }

                        if(!validation.hasErrors())
                        {
                            FXTemplate template = new FXTemplate(
                                    name.getValue(),
                                    UUID.uuid()
                            );

                            FXModel model = new FXModel(
                                    FXModel.ExecutionType.valueOf(type.getValue()),
                                    addressNode

                            );
                            model.getFieldNames().addAll(fieldNames.getValue());

                            template.getModels().add(model);

                            presenter.onCreateTemplate(template);
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
