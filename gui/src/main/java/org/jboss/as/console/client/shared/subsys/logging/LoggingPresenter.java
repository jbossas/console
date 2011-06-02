/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.shared.subsys.logging;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import java.util.Map;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Stan Silvert
 * @date 3/29/11
 */
public class LoggingPresenter extends Presenter<LoggingPresenter.MyView, LoggingPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private RevealStrategy revealStrategy;
    private LoggingInfo loggingInfo;
    DispatchAsync dispatcher;

    @ProxyCodeSplit
    @NameToken(NameTokens.LoggingPresenter)
    public interface MyProxy extends Proxy<LoggingPresenter>, Place {
    }

    public interface MyView extends View {

        void setPresenter(LoggingPresenter presenter);

        void updateLoggingInfo(LoggingInfo loggingInfo);
        
        void enableLoggerDetails(boolean isEnabled);
    }

    @Inject
    public LoggingPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory, RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.revealStrategy = revealStrategy;
        this.loggingInfo = new LoggingInfo(dispatcher, factory, view);
        this.dispatcher = dispatcher;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        loggingInfo.refreshView();
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }
    
    public void onEditLogger() {
        getView().enableLoggerDetails(true);
    }
    
    public void onSaveLoggerDetails(final String name, Map<String, Object> changedValues) {
        getView().enableLoggerDetails(false);
        if (changedValues.isEmpty()) return;
        
        String newLevel = (String)changedValues.get("level");
        if (newLevel == null) return;
        
        // can only change level for now
        ModelNode operation = null;
        if (name.equals("root-logger")) {
            operation = LoggingOperation.make("change-root-log-level");
        } else {
            operation = LoggingOperation.make("change-log-level");
            operation.get(ADDRESS).add("logger", name);
        }
        operation.get("level").set(newLevel);
        
        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error(Console.CONSTANTS.common_error_unknownError(), caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                Console.info("Success: Updated Log Level");
                loggingInfo.refreshView();
            }
        });
    }

}
