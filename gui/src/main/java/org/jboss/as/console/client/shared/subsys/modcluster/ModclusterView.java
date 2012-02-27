package org.jboss.as.console.client.shared.subsys.modcluster;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.subsys.modcluster.model.Modcluster;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

/**
 * @author Pavel Slegr
 * @date 02/16/12
 */
public class ModclusterView extends DisposableViewImpl implements ModclusterPresenter.MyView{

    private ModclusterPresenter presenter;
    private ModclusterForm form;
    private ModclusterForm contextForm;
    private ModclusterForm proxyForm;
    private ModclusterForm sessionForm;
    private ModclusterForm networkingForm;

    @Override
    public Widget createWidget() {


        form = new ModclusterForm(presenter);

        CheckBoxItem advertise = new CheckBoxItem("advertise", "Advertise");
        TextBoxItem advertiseSocket = new TextBoxItem("advertiseSocket", "Advertise Socket");
        TextBoxItem advertiseKey= new TextBoxItem("advertiseKey", "Advertise Key", false);

        TextBoxItem balancer = new TextBoxItem("balancer", "Balancer", false);
        TextBoxItem domain = new TextBoxItem("domain", "Domain", false);

        form.setFields(domain, balancer, advertiseSocket, advertiseKey, advertise);

        // ---

        contextForm = new ModclusterForm(presenter);

        TextAreaItem excludedContexts = new TextAreaItem("excludedContexts", "Excluded Contexts");
        CheckBoxItem autoEnableContexts = new CheckBoxItem("autoEnableContexts", "Auto Enable Contexts");

        contextForm.setFields(autoEnableContexts, excludedContexts);


        // ---

        proxyForm = new ModclusterForm(presenter);

        TextAreaItem proxyList = new TextAreaItem("proxyList", "Proxy List");
        TextBoxItem proxyUrl = new TextBoxItem("proxyUrl", "Proxy Url");

        proxyForm.setFields(proxyUrl, proxyList);


        //---
        sessionForm = new ModclusterForm(presenter);

        CheckBoxItem stickySession = new CheckBoxItem("stickySession", "Sticky Session");
        CheckBoxItem stickySessionForce = new CheckBoxItem("stickySessionForce", "Sticky Session Force");
        CheckBoxItem stickySessionRemove = new CheckBoxItem("stickySessionRemove", "Sticky Session Remove");

        sessionForm.setFields(stickySession, stickySessionForce, stickySessionRemove);

        // --

        networkingForm = new ModclusterForm(presenter);

        NumberBoxItem nodeTimeout = new NumberBoxItem("nodeTimeout", "Node Timeout");
        NumberBoxItem socketTimeout = new NumberBoxItem("socketTimeout", "Socket Timeout");
        NumberBoxItem stopContextTimeout = new NumberBoxItem("stopContextTimeout", "Stop Context Timeout");

        NumberBoxItem maxAttemps = new NumberBoxItem("maxAttemps", "Max Attemps");
        CheckBoxItem flushPackets = new CheckBoxItem("flushPackets", "Flush Packets");
        NumberBoxItem flushWait = new NumberBoxItem("flushWait", "Flush Wait");
        NumberBoxItem ping = new NumberBoxItem("ping", "Ping");
        NumberBoxItem workerTimeout = new NumberBoxItem("workerTimeout", "Worker Timeout");
        NumberBoxItem ttl = new NumberBoxItem("ttl", "TTL");

        networkingForm.setFields(nodeTimeout, socketTimeout, stopContextTimeout, maxAttemps, flushPackets, flushWait, ping, ttl, workerTimeout);
        OneToOneLayout layout = new OneToOneLayout()
                .setTitle("Modcluster")
                .setHeadline("Modcluster")
                .setDescription("The modcluster configuration.")
                .setMaster("Advertising", form.asWidget())
                .addDetail("Sessions", sessionForm.asWidget())
                .addDetail("Web Contexts", contextForm.asWidget())
                .addDetail("Proxies", proxyForm.asWidget())
                .addDetail("SSL", new HTML("TODO: Implement"))
                .addDetail("Networking", networkingForm.asWidget());


        return layout.build();
    }

    @Override
    public void setPresenter(ModclusterPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateFrom(Modcluster modcluster) {
        form.updateFrom(modcluster);
        sessionForm.updateFrom(modcluster);
        contextForm.updateFrom(modcluster);
        proxyForm.updateFrom(modcluster);
        networkingForm.updateFrom(modcluster);
    }
}
