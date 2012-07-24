package org.jboss.as.console.client.tools;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.tools.mapping.DescriptionMapper;
import org.jboss.as.console.client.tools.mapping.RequestParameter;
import org.jboss.as.console.client.tools.mapping.ResponseParameter;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 7/24/12
 */
public class FormProxy {

    private FXModel model;
    private SimpleForm form;
    private FXFormManager manager;
    private VerticalPanel formLayout;

    public FormProxy(FXModel model, ModelNode dmrDescription, FXFormManager manager) {
        this.model = model;
        this.form = new SimpleForm();
        this.manager = manager;

        layoutStub();

        // init form from dmr description
        createForm(dmrDescription);
    }

    private void createForm(ModelNode description) {

        DescriptionMapper mapper = new DescriptionMapper(model.getAddress(), description);
        mapper.map(new DescriptionMapper.Mapping() {

            List<FormItem> items = new LinkedList<FormItem>();

            @Override
            public void onAttribute(String name, String description, String type, boolean required) {

                // whitelist
                if(!model.getFieldNames().contains(name))
                    return;

                if("STRING".equals(type))
                {
                    TextBoxItem item = new TextBoxItem(name, name.toUpperCase(), required);
                    items.add(item);
                }
                else if("INT".equals(type))
                {
                    NumberBoxItem item = new NumberBoxItem(name, name.toUpperCase(), required);
                    items.add(item);
                }
                else if("BOOLEAN".equals(type))
                {
                    CheckBoxItem item = new CheckBoxItem(name, name.toUpperCase());
                    items.add(item);
                }
                else if("DOUBLE".equals(type))
                {
                    NumberBoxItem item = new NumberBoxItem(name, name.toUpperCase(), required);
                    items.add(item);
                }
                else if("FLOAT".equals(type))
                {
                    NumberBoxItem item = new NumberBoxItem(name, name.toUpperCase(), required);
                    items.add(item);
                }
                else if("LONG".equals(type))
                {
                    NumberBoxItem item = new NumberBoxItem(name, name.toUpperCase(), required);
                    items.add(item);
                }
                else
                {
                    System.out.println("Unsupported type: "+type);
                }
            }

            @Override
            public void onOperation(String name, String description, List<RequestParameter> parameter, ResponseParameter response) {

            }

            @Override
            public void onChild(String name, String description) {

            }

            @Override
            public void onBegin() {

            }

            @Override
            public void onFinish() {

                System.out.println("create "+items.size()+ " items");
                form.setFields(items.toArray(new FormItem[0]));
                formLayout.add(form.asWidget());
            }
        });
    }

    private void layoutStub() {
        SimpleFormToolStrip formTools = new SimpleFormToolStrip(form, new SimpleFormToolStrip.FormCallback() {
            @Override
            public void onSave(Map<String, Object> changeset) {

            }

            @Override
            public void onDelete(Object entity) {

            }
        }) ;

        formLayout = new VerticalPanel();
        formLayout.setStyleName("fill-layout-width");
        formLayout.add(formTools.asWidget());

    }

    public Widget asWidget() {

        return formLayout;
    }
}
