package org.jboss.as.console.client.tools;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.tools.mapping.DescriptionMapper;
import org.jboss.as.console.client.tools.mapping.RequestParameter;
import org.jboss.as.console.client.tools.mapping.ResponseParameter;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;
import org.jboss.dmr.client.Property;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 7/16/12
 */
@Deprecated
public class FormView {

    private VerticalPanel formContainer;

    private Form form;
    private List<FormItem> items = new LinkedList<FormItem>();

    Widget asWidget() {
        LayoutPanel layout = new LayoutPanel();
        layout.setStyleName("fill-layout");

        layout.getElement().setAttribute("style", "padding:10px");


        formContainer = new VerticalPanel();
        formContainer.setStyleName("fill-layout-width");


        final ScrollPanel scroll = new ScrollPanel(formContainer);
        layout.add(scroll);

        layout.setWidgetTopHeight(scroll, 0, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    public void updateDescription(ModelNode address, ModelNode description)
    {
        DescriptionMapper mapper = new DescriptionMapper(address, description);
        mapper.map(new DescriptionMapper.Mapping() {

            @Override
            public void onAttribute(String name, String description, String type, boolean required, boolean expressions) {

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
                formContainer.clear();
                items.clear();
                form = new Form(Object.class);
                form.setNumColumns(1);
            }

            @Override
            public void onFinish() {

                form.setFields(items.toArray(new FormItem[0]));
                //form.setEnabled(false);
                formContainer.add(form.asWidget());

            }
        });
    }

    private void bind(Property payload) {
        // late binding when form is initialized
        // TODO: this would required generic autobean types...

        final String entityName = payload.getName();
        final ModelNode entityValue = payload.getValue().asObject();

        for(String attribute : entityValue.keys())
        {
            for(FormItem item : items)
            {
                if(item.getName().equals(attribute))
                {
                    try {
                        if(ModelType.BOOLEAN.equals(entityValue.get(attribute).getType()))
                        {
                            item.setValue(entityValue.get(attribute).asBoolean());
                        }
                        else if(ModelType.LONG.equals(entityValue.get(attribute).getType()))
                        {
                            item.setValue(entityValue.get(attribute).asLong());
                        }
                        else if(ModelType.INT.equals(entityValue.get(attribute).getType()))
                        {
                            item.setValue(entityValue.get(attribute).asInt());
                        }
                        else if(ModelType.DOUBLE.equals(entityValue.get(attribute).getType()))
                        {
                            item.setValue(entityValue.get(attribute).asDouble());
                        }
                        else if(ModelType.STRING.equals(entityValue.get(attribute).getType()))
                        {
                            item.setValue(entityValue.get(attribute).asString());
                        }
                        else if(ModelType.EXPRESSION.equals(entityValue.get(attribute).getType()))
                        {
                            item.setExpressionValue(entityValue.get(attribute).asString());
                        }
                        else
                        {
                            System.out.println("Unsupported binding: "+entityValue.get(attribute).getType());
                        }
                    } catch (Throwable e) {
                        System.out.println("Failed to bind " + attribute);
                        e.printStackTrace();
                    }

                    break;
                }
            }
        }
    }

    public void display(Property model) {
        bind(model);
    }

    public void clearForm() {
        formContainer.clear();
    }
}
