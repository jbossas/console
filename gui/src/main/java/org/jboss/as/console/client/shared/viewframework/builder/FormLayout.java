package org.jboss.as.console.client.shared.viewframework.builder;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class FormLayout {

    private Form form;
    private FormHelpPanel help;
    private FormToolStrip tools;

    public FormLayout setForm(Form form) {
        this.form = form;
        return this;
    }

    public FormLayout setHelp(FormHelpPanel help) {

        this.help = help;
        return this;
    }

    public FormLayout setSetTools(FormToolStrip tools) {

        this.tools = tools;
        return this;
    }

    public Widget build() {

        if(null==form)
            throw new IllegalStateException("form not set");

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.getElement().setAttribute("style", "margin-top:15px;");

        if(tools!=null)layout.add(tools.asWidget());
        if(help!=null)layout.add(help.asWidget());
        layout.add(form.asWidget());

        return layout;
    }
}
