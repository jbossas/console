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
package org.jboss.as.console.client.shared.subsys.ejb.session;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.SimpleDMRResponseHandler;
import org.jboss.as.console.client.shared.general.MessageWindow;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.ejb.EJBPresenterBase;
import org.jboss.as.console.client.shared.subsys.ejb.EJBViewBase;
import org.jboss.as.console.client.shared.subsys.ejb.session.model.SessionBeans;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

/**
 * @author David Bosschaert
 */
public class SessionBeansPresenter extends EJBPresenterBase<SessionBeans, SessionBeansPresenter.MyView, SessionBeansPresenter.MyProxy> {
    private static final String DEFAULT_POOL_ATTR_NAME = "default-slsb-instance-pool";

    private DefaultWindow window;

    @ProxyCodeSplit
    @NameToken(NameTokens.SessionBeanPresenter)
    public interface MyProxy extends Proxy<SessionBeansPresenter>, Place {
    }

    public interface MyView extends EJBViewBase<SessionBeans> {
        void setPresenter(SessionBeansPresenter presenter);
    }

    @Inject
    public SessionBeansPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher, BeanFactory factory,
            RevealStrategy revealStrategy) {
        super(eventBus, view, proxy, revealStrategy, dispatcher, factory);

    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void loadDetails() {
        loadDetails(factory.sessionBeans().as(), DEFAULT_POOL_ATTR_NAME);
    }

    public void closeDialogue() {
        if (window != null)
            window.hide();
    }

    public void onDefaultPoolChange(final String value) {
        window = new DefaultWindow(Console.CONSTANTS.common_label_areYouSure());
        window.setWidth(320);
        window.setHeight(140);
        window.setWidget(new MessageWindow(Console.MESSAGES.common_validation_requiredField(),
            new MessageWindow.Result() {
                @Override
                public void result(boolean result) {
                    applyDefaultPoolChange(value);
                }
            }).asWidget());
        window.setGlassEnabled(true);
        window.center();
    }

    private void applyDefaultPoolChange(String value) {
        ModelNode operation = createOperation(ModelDescriptionConstants.WRITE_ATTRIBUTE_OPERATION);
        operation.get(ModelDescriptionConstants.NAME).set(DEFAULT_POOL_ATTR_NAME);
        operation.get(ModelDescriptionConstants.VALUE).set(value);

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(ModelDescriptionConstants.WRITE_ATTRIBUTE_OPERATION, DEFAULT_POOL_ATTR_NAME, SUBSYSTEM_NAME,
                    new Command() {
                        @Override
                        public void execute() {
                            loadDetails();
                        }
                }));
    }
}
