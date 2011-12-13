package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
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
    private String editedName = null;
    private PoolManagement management;
    private boolean xaDisplay = false;

    public PoolConfigurationView(PoolManagement management) {
        this.management = management;
    }

    public void setXADisplay(boolean b) {
        this.xaDisplay =b;
    }

    Widget asWidget() {

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout");
        form = new Form<PoolConfig>(PoolConfig.class);
        form.setNumColumns(2);

        NumberBoxItem maxCon = new NumberBoxItem("maxPoolSize", "Max Pool Size");
        NumberBoxItem minCon = new NumberBoxItem("minPoolSize", "Min Pool Size");
        CheckBoxItem strictMin = new CheckBoxItem("poolStrictMin", "Strict Minimum");
        CheckBoxItem prefill = new CheckBoxItem("poolPrefill", "Prefill enabled");

        CheckBoxItem rmOverride = new CheckBoxItem("enableRMOverride", "Same RM Override");
        CheckBoxItem interleave = new CheckBoxItem("enableInterleave", "Interleave");
        CheckBoxItem padXid = new CheckBoxItem("padXid", "Pad XID");
        CheckBoxItem wrap = new CheckBoxItem("wrapXaResource", "Wrap XA");

        if(!xaDisplay)
            form.setFields(minCon, maxCon, strictMin, prefill);
        else
            form.setFields(minCon, maxCon, strictMin, prefill, rmOverride, interleave, padXid, wrap);

        form.setEnabled(false);

        FormToolStrip<PoolConfig> toolStrip = new FormToolStrip<PoolConfig>(
                form,
                new FormToolStrip.FormCallback<PoolConfig>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        management.onSavePoolConfig(editedName, changeset);
                    }

                    @Override
                    public void onDelete(PoolConfig entity) {
                       management.onResetPoolConfig(editedName, entity);
                    }
                }, Console.CONSTANTS.common_label_reset()
        );

        // disable "reset" functionality
        toolStrip.providesDeleteOp(false);



        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {

                ModelNode address = Baseadress.get();
                address.add("subsystem", "datasources");

                if(xaDisplay)
                    address.add("xa-data-source", "*");
                else
                    address.add("data-source", "*");
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
}
