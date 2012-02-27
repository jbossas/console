package org.jboss.as.console.client.shared.subsys.modcluster;

import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.modcluster.model.Modcluster;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Pavel Slegr
 * @date 02/22/11
 */
public class NewModclusterWizard {
    private ModclusterPresenter presenter;

    public NewModclusterWizard(ModclusterPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<Modcluster> form = new Form<Modcluster>(Modcluster.class);
        
        CheckBoxItem advertise = new CheckBoxItem("advertise", "Advertise") {
            {
                setRequired(false);
            }
        };
        TextBoxItem advertiseSocket = new TextBoxItem("advertiseSocket", "Advertise Socket") {
            {
                setRequired(false);
            }
        };
        TextBoxItem excludedContexts = new TextBoxItem("excludedContexts", "Excluded Contexts") {
            {
                setRequired(false);
            }
        };
        CheckBoxItem autoEnableContexts = new CheckBoxItem("autoEnableContexts", "Auto Enable Contexts") {
            {
                setRequired(false);
            }
        };
        TextBoxItem balancer = new TextBoxItem("balancer", "Balancer") {
            {
                setRequired(false);
            }
        };
        NumberBoxItem maxAttemps = new NumberBoxItem("maxAttemps", "Max Attemps") {
            {
                setRequired(false);
            }
        };
        CheckBoxItem flushPackets = new CheckBoxItem("flushPackets", "Flush Packets") {
            {
                setRequired(false);
            }
        };
        NumberBoxItem flushWait = new NumberBoxItem("flushWait", "Flush Wait") {
            {
                setRequired(false);
            }
        };
        NumberBoxItem nodeTimeout = new NumberBoxItem("nodeTimeout", "Node Timeout") {
            {
                setRequired(false);
            }
        };
        NumberBoxItem ping = new NumberBoxItem("ping", "Ping") {
            {
                setRequired(false);
            }
        };
        TextBoxItem proxyList = new TextBoxItem("proxyList", "Proxy List") {
            {
                setRequired(false);
            }
        };
        TextBoxItem proxyUrl = new TextBoxItem("proxyUrl", "Proxy Url") {
            {
                setRequired(false);
            }
        };
        NumberBoxItem socketTimeout = new NumberBoxItem("socketTimeout", "Socket Timeout") {
            {
                setRequired(false);
            }
        };
        NumberBoxItem stopContextTimeout = new NumberBoxItem("stopContextTimeout", "Stop Context Timeout") {
            {
                setRequired(false);
            }
        };
        CheckBoxItem stickySession = new CheckBoxItem("stickySession", "Sticky Session") {
            {
                setRequired(false);
            }
        };
        CheckBoxItem stickySessionForce = new CheckBoxItem("stickySessionForce", "Sticky Session Force") {
            {
                setRequired(false);
            }
        };
        CheckBoxItem stickySessionRemove = new CheckBoxItem("stickySessionRemove", "Sticky Session Remove") {
            {
                setRequired(false);
            }
        };
        NumberBoxItem workerTimeout = new NumberBoxItem("workerTimeout", "Worker Timeout") {
            {
                setRequired(false);
            }
        };
        NumberBoxItem ttl = new NumberBoxItem("ttl", "TTL") {
            {
                setRequired(false);
            }
        };
        


        form.setFields(
        		advertise, autoEnableContexts, advertiseSocket, excludedContexts,
        		balancer,maxAttemps,flushPackets,flushWait,
        		nodeTimeout,ping,proxyList,proxyUrl,
        		socketTimeout,stopContextTimeout,
        		stickySession,stickySessionForce,stickySessionRemove,workerTimeout,
        		ttl);

        DialogueOptions options = new DialogueOptions(

                // save
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        // merge base

                        FormValidation validation = form.validate();
                        if(validation.hasErrors())
                            return;

                        presenter.onCreateModcluster(form.getUpdatedEntity());

                    }
                },

                // cancel
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.closeDialoge();
                    }
                }

        );

        // ----------------------------------------

        Widget formWidget = form.asWidget();

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "modecluster");
                        address.add("mod-cluster-config", "*");
                        address.add("configuration","*");
                        return address;
                    }
                }, form
        );

        layout.add(helpPanel.asWidget());

        layout.add(formWidget);

        return new WindowContentBuilder(layout, options).build();
    }
}
