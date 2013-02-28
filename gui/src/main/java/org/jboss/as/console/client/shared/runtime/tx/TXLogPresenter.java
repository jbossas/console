package org.jboss.as.console.client.shared.runtime.tx;

import com.google.gwt.core.client.Scheduler;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.plugins.RuntimeGroup;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.state.ServerSelectionChanged;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.spi.RuntimeExtension;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class TXLogPresenter extends Presenter<TXLogPresenter.MyView, TXLogPresenter.MyProxy>
        implements ServerSelectionChanged.ChangeListener {

    private DispatchAsync dispatcher;
    private EntityAdapter<TXRecord> entityAdapter;
    private RevealStrategy revealStrategy;
    private final EntityAdapter<TXParticipant> participantAdapter;

    @ProxyCodeSplit
    @NameToken("tx-logs")
    @RuntimeExtension(name="Transaction Logs", group=RuntimeGroup.METRICS, key="transactions")
    public interface MyProxy extends Proxy<TXLogPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(TXLogPresenter presenter);

        void clear();

        void updateFrom(List<TXRecord> records);

        void updateParticpantsFrom(List<TXParticipant> records);
    }

    @Inject
    public TXLogPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher,
            ApplicationMetaData metaData, RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;

        this.entityAdapter = new EntityAdapter<TXRecord>(TXRecord.class, metaData);
        this.participantAdapter = new EntityAdapter<TXParticipant>(TXParticipant.class, metaData);
    }

    @Override
    public void onServerSelectionChanged(boolean isRunning) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                getView().clear();
                if(isVisible()) refresh();
            }
        });
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerSelectionChanged.TYPE, this);
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

    public void refresh() {

        // clear at first
        getView().clear();

        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem","transactions");
        address.add("log-store","log-store");

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(address);
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("transactions");
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();

                if(result.isFailure())
                {
                    Console.error("Failed to read transactions logs", result.getFailureDescription());
                }
                else
                {
                    List<Property> items = result.get(RESULT).asPropertyList();
                    List<TXRecord> records = new ArrayList<TXRecord>(items.size());
                    for(Property item : items)
                    {
                        TXRecord txRecord = entityAdapter.fromDMR(item.getValue());
                        txRecord.setId(item.getName());

                        records.add(txRecord);
                    }

                    // update view
                    getView().updateFrom(records);

                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            onProbe(false);
                        }
                    });
                }


            }
        });
    }

    public void onLoadParticipants(final TXRecord selection) {
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem","transactions");
        address.add("log-store","log-store");
        address.add("transactions", selection.getId());

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(address);
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("participants");
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();

                if(result.isFailure())
                {
                    Console.error("Failed to read transactions participants", result.getFailureDescription());
                }
                else
                {
                    List<Property> items = result.get(RESULT).asPropertyList();
                    List<TXParticipant> records = new ArrayList<TXParticipant>(items.size());
                    for(Property item : items)
                    {
                        TXParticipant participant = participantAdapter.fromDMR(item.getValue());
                        participant.setId(item.getName());
                        participant.setLog(selection.getId()); // FK

                        records.add(participant);
                    }

                    // update view
                    getView().updateParticpantsFrom(records);
                }


            }
        });
    }

    public void onDeleteRecord(final TXRecord selection) {
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem","transactions");
        address.add("log-store","log-store");
        address.add("transactions", selection.getId());

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(address);
        operation.get(OP).set("delete");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();

                if(result.isFailure())
                {
                    Console.error(Console.MESSAGES.deletionFailed(selection.getId()), result.getFailureDescription());
                }
                else
                {
                    refresh();
                }

            }
        });
    }


    public void onRefreshParticipant(TXParticipant selection) {
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem","transactions");
        address.add("log-store","log-store");
        address.add("transactions", selection.getLog());
        address.add("participants", selection.getId());

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(address);
        operation.get(OP).set("refresh");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();

                if(result.isFailure())
                {
                    Console.error("Refresh operation failed", result.getFailureDescription());
                }
                else
                {
                    refresh();
                }
            }
        });
    }

    public void onRecoverParticipant(TXParticipant selection) {
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem","transactions");
        address.add("log-store","log-store");
        address.add("transactions", selection.getLog());
        address.add("participants", selection.getId());

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(address);
        operation.get(OP).set("recover");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();

                if(result.isFailure())
                {
                    Console.error("Recover operation failed", result.getFailureDescription());
                }
                else
                {
                    refresh();
                }
            }
        });
    }

    public void onProbe(final boolean refresh) {
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem","transactions");
        address.add("log-store","log-store");

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(address);
        operation.get(OP).set("probe");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();

                if(result.isFailure())
                {
                    Console.error("Probe operation failed", result.getFailureDescription());
                }
                else
                {
                    Console.info(Console.MESSAGES.successful("Probe operation"));
                    if(refresh)
                        refresh();
                }
            }
        });
    }

}
