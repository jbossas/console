package org.jboss.as.console.client.server.subsys.threads;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.*;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.NameTokens;
import org.jboss.as.console.client.server.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.util.message.Message;

/**
 * @author Heiko Braun
 * @date 2/9/11
 */
public class ThreadManagementPresenter extends Presenter<ThreadManagementPresenter.MyView, ThreadManagementPresenter.MyProxy> {

    private final PlaceManager placeManager;

    @ProxyCodeSplit
    @NameToken(NameTokens.ThreadManagementPresenter)
    public interface MyProxy extends Proxy<ThreadManagementPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(ThreadManagementPresenter presenter);
    }

    @Inject
    public ThreadManagementPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                                     PlaceManager placeManager) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ServerMgmtApplicationPresenter.TYPE_SetToolContent, this);
    }

    // -----------------------------------------------

    public ListGridRecord[] getFactoryRecords() {
        return records;
    }

    public void onUpdateRecord(ThreadFactoryRecord record) {

        String name = record.getAttribute("name");
        if(name!=null)
        {
            Console.MODULES.getMessageCenter().notify(
                    new Message("Record saved: "+ name, Message.Severity.Info)
            );
        }
    }


    private final ThreadFactoryRecord[] records =
            new ThreadFactoryRecord[]
                    {
                            new ThreadFactoryRecord("DefaultThreadFactory", "default", 1)
                    };
}

