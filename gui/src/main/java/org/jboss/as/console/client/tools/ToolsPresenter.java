package org.jboss.as.console.client.tools;

import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.debug.DebugPresenter;
import org.jboss.ballroom.client.widgets.forms.ResolveExpressionEvent;

/**
 * @author Heiko Braun
 * @date 6/15/12
 */
public class ToolsPresenter extends Presenter<ToolsPresenter.MyView, ToolsPresenter.MyProxy>
{

    private final PlaceManager placeManager;
    private BrowserPresenter browser;
    private DebugPresenter debug;

    private String requestedTool;

    @ProxyCodeSplit
    @NameToken(NameTokens.ToolsPresenter)
    public interface MyProxy extends Proxy<ToolsPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(ToolsPresenter presenter);
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent =
            new GwtEvent.Type<RevealContentHandler<?>>();

    @Inject
    public ToolsPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, BrowserPresenter browser) {
        super(eventBus, view, proxy);
        this.placeManager = placeManager;
        //this.debug = debug;
        this.browser = browser;
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        requestedTool = request.getParameter("name", null);
    }

    @Override
    protected void revealInParent() {

        if("expressions".equals(requestedTool))
        {
            getEventBus().fireEventFromSource(new ResolveExpressionEvent("${name:default_value}"), this);
        }
        else if("browser".equals(requestedTool))
        {
            RevealRootPopupContentEvent.fire(this, browser);
        }
        else if("debug-panel".equals(requestedTool))
        {
            RevealRootPopupContentEvent.fire(this, debug);
        }
    }
}
