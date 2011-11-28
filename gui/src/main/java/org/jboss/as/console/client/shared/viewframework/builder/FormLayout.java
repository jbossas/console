package org.jboss.as.console.client.shared.viewframework.builder;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.ballroom.client.widgets.forms.Form;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class FormLayout {


    private Form form;
    private FormHelpPanel help;

    public FormLayout setForm(Form form) {
        this.form = form;
        return this;
    }

    public FormLayout setHelp(FormHelpPanel help) {

        this.help = help;
        return this;
    }

    public Widget build() {

        if(null==help)
            throw new IllegalStateException("help not set");

        if(null==form)
            throw new IllegalStateException("form not set");

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        layout.add(help.asWidget());
        layout.add(form.asWidget());

        return layout;
    }
}
