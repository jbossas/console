package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 9/16/11
 */
public class PoolConfigurationView {

    private Form<PoolConfig> form;
    private DataSourcePresenter presenter;
    private String editedName = null;
    private boolean isXA = false;

    public PoolConfigurationView(DataSourcePresenter presenter) {
        this.presenter = presenter;
    }

    public PoolConfigurationView(DataSourcePresenter presenter, boolean isXA) {
        this.isXA = isXA;
        this.presenter = presenter;
    }

    Widget asWidget() {

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout");
        form = new Form<PoolConfig>(PoolConfig.class);
        form.setNumColumns(2);

        NumberBoxItem maxCon = new NumberBoxItem("maxPoolSize", "Max Pool Size");
        NumberBoxItem minCon = new NumberBoxItem("minPoolSize", "Min Pool Size");
        CheckBoxItem strictMin = new CheckBoxItem("poolStrictMin", "Use Strict Min?");
        CheckBoxItem prefill = new CheckBoxItem("poolPrefill", "Pool Prefill?");

        form.setFields(minCon, maxCon, strictMin, prefill);
        form.setEnabled(false);

        FormToolStrip<PoolConfig> toolStrip = new FormToolStrip<PoolConfig>(
                form,
                new FormToolStrip.FormCallback<PoolConfig>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSavePoolConfig(editedName, changeset, isXA());
                    }

                    @Override
                    public void onDelete(PoolConfig entity) {
                       presenter.onDeletePoolConfig(editedName, entity, isXA());
                    }
                }, "Reset"
        );


        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {

                ModelNode address = Baseadress.get();
                address.add("subsystem", "datasources");
                String subaddress = isXA ? "xa-data-source" : "data-source";
                address.add(subaddress, "*");
                return address;
            }
        }, form);

        panel.add(toolStrip.asWidget());
        panel.add(helpPanel.asWidget());
        panel.add(form.asWidget());

        return panel;
    }

    public void updateFrom(String name, PoolConfig poolConfig) {
        this.editedName = name;
        form.edit(poolConfig);
    }

    private boolean isXA()
    {
        return isXA;
    }
}
