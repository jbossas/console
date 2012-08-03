package org.jboss.as.console.client.shared.expr;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.tools.Tool;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 8/3/12
 */
public class ExpressionTool implements Tool {

    private DefaultWindow window;
    private ExpressionResolver resolver;
    private TextAreaItem output;
    private TextBoxItem input;

    public ExpressionTool(ExpressionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void launch() {
        if(null==window)
            window = asWidget();

        window.center();
    }

    DefaultWindow asWidget() {
        final DefaultWindow window = new DefaultWindow("Expressions");
        window.setWidth(480);
        window.setHeight(360);


        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("window-content");

        panel.add(new ContentHeaderLabel("Resolve Expression Values"));

        Form<Expression> form = new Form<Expression>(Expression.class);
        input = new TextBoxItem("input", "Expression");
        output = new TextAreaItem("output", "Resolved Value") {
            @Override
            public String getErrMessage() {
                return "Cannot be resolved!";
            }
        };

        form.setFields(input, output);

        panel.add(new ContentDescription("Expressions can only be resolved on servers that are running."));
        panel.add(form.asWidget());


        ClickHandler submitHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                resolve(input.getValue());
            }
        };

        ClickHandler cancelHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                // Close tool
                window.hide();
            }
        };

        DialogueOptions options = new DialogueOptions(
                "Resolve",submitHandler, "Done",cancelHandler);


        window.trapWidget(new WindowContentBuilder(panel, options).build());

        window.setGlassEnabled(true);

        return window;
    }

    @Override
    public void dispose() {
        if(window!=null)
            window.hide();
    }

    public void resolve(String expr) {

        output.setErroneous(false);
        output.clearValue();
        input.setValue(expr);

        resolver.resolveValue(Expression.fromString(expr), new SimpleCallback<Map<String,String>>() {
            @Override
            public void onSuccess(Map<String,String> serverValues) {
                output.setErroneous(serverValues.isEmpty());

                StringBuilder sb = new StringBuilder();
                for(String server : serverValues.keySet())
                {
                    sb.append(server).append("=").append(serverValues.get(server));
                    sb.append("\n");
                }

                output.setValue(sb.toString());
            }
        });
    }
}
