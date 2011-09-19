package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

/**
 * @author Heiko Braun
 * @date 9/16/11
 */
public class PoolConfiguration {

    private Form<PoolConfig> form;

    Widget asWidget() {

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout");
        form = new Form<PoolConfig>(PoolConfig.class);
        form.setNumColumns(2);

        NumberBoxItem maxCon = new NumberBoxItem("maxPoolSize", "Max Pool Size");
        NumberBoxItem minCon = new NumberBoxItem("minPoolSize", "Min Pool Size");

        form.setFields(minCon, maxCon);
        form.setEnabled(false);

        panel.add(form.asWidget());

        return panel;
    }

    public void updateFrom(PoolConfig poolConfig) {
        form.edit(poolConfig);
    }
}
