package org.jboss.as.console.client.shared.runtime.jms;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.messaging.AggregatedJMSModel;
import org.jboss.as.console.client.shared.subsys.messaging.LoadJMSCmd;
import org.jboss.as.console.client.shared.subsys.messaging.model.JMSEndpoint;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 12/9/11
 */
public class JMSMetricPresenter extends Presenter<JMSMetricPresenter.MyView, JMSMetricPresenter.MyProxy>
        implements ServerSelectionEvent.ServerSelectionListener {

    private DispatchAsync dispatcher;
    private RevealStrategy revealStrategy;
    private CurrentServerSelection serverSelection;
    private JMSEndpoint selectedTopic;
    private BeanFactory factory;
    private LoadJMSCmd loadJMSCmd;

    @ProxyCodeSplit
    @NameToken(NameTokens.JmsMetricPresenter)
    public interface MyProxy extends Proxy<JMSMetricPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(JMSMetricPresenter presenter);
        void clearSamples();
        void setNonDurableMetric(Metric durableMetric);
        void setDurableMetric(Metric durableMetric);

        void setTopics(List<JMSEndpoint> topics);
    }

    @Inject
    public JMSMetricPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher,
            ApplicationMetaData metaData, RevealStrategy revealStrategy,
            CurrentServerSelection serverSelection, BeanFactory factory) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.serverSelection = serverSelection;
        this.factory = factory;

        this.loadJMSCmd = new LoadJMSCmd(dispatcher, factory);
    }

    public void setSelectedTopic(JMSEndpoint topic) {
        this.selectedTopic= topic;
        if(topic!=null)
            loadTopicMetrics();

    }

    @Override
    public void onServerSelection(String hostName, String serverName) {

        getView().clearSamples();

        // refresh if needed. Otherwise it will happen onReset()
        if(isVisible()) refresh();
    }

    public void refresh() {

        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "messaging");
        address.add("hornetq-server", "default");

        loadJMSCmd.execute(address, new SimpleCallback<AggregatedJMSModel>() {
            @Override
            public void onSuccess(AggregatedJMSModel result) {
                getView().setTopics(result.getTopics());
                // TODO: remaining items
            }
        });
    }

    private void loadTopicMetrics() {

        if(null==selectedTopic)
            throw new RuntimeException("topic selection is null!");

        getView().clearSamples();

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(RuntimeBaseAddress.get());
        operation.get(ADDRESS).add("subsystem", "messaging");
        operation.get(ADDRESS).add("hornetq-server", "default");
        operation.get(ADDRESS).add("jms-topic", selectedTopic.getName());

        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = ModelNode.fromBase64(dmrResponse.getResponseText());

                if(response.isFailure())
                {
                    Console.error("Error loading metrics", response.getFailureDescription());
                }
                else
                {
                    ModelNode result = response.get(RESULT).asObject();

                    long messageCount = result.get("message-count").asLong();
                    long messagesAdded = result.get("messages-added").asLong();
                    long delivering = result.get("delivering-count").asLong();

                    Metric nonDurableMetric = new Metric(
                            messageCount,
                            messagesAdded,
                            delivering,
                            result.get("non-durable-message-count").asLong(),
                            result.get("non-durable-subscription-count").asLong(),
                            result.get("subscription-count").asLong()
                    );

                    Metric durableMetric = new Metric(
                            messageCount,
                            messagesAdded,
                            delivering,
                            result.get("durable-message-count").asLong(),
                            result.get("durable-subscription-count").asLong(),
                            result.get("subscription-count").asLong()
                    );

                    getView().setNonDurableMetric(nonDurableMetric);
                    getView().setDurableMetric(durableMetric);
                }
            }
        });
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        refresh();
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInRuntimeParent(this);
    }
}
