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
package org.jboss.as.console.client.shared.subsys.ejb.mdb;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;

import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.ejb.EJBPresenterBase;
import org.jboss.as.console.client.shared.subsys.ejb.EJBViewBase;
import org.jboss.as.console.client.shared.subsys.ejb.mdb.model.MessageDrivenBeans;

/**
 * @author David Bosschaert
 */
public class MessageDrivenBeanPresenter extends EJBPresenterBase<MessageDrivenBeans, MessageDrivenBeanPresenter.MyView, MessageDrivenBeanPresenter.MyProxy> {
    @ProxyCodeSplit
    @NameToken(NameTokens.MessageDrivenBeanPresenter)
    public interface MyProxy extends Proxy<MessageDrivenBeanPresenter>, Place {
    }

    public interface MyView extends EJBViewBase<MessageDrivenBeans> {
        void setPresenter(MessageDrivenBeanPresenter presenter);
    }

    @Inject
    public MessageDrivenBeanPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher, BeanFactory factory, RevealStrategy revealStrategy) {
        super(eventBus, view, proxy, revealStrategy, dispatcher, factory);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void loadDetails() {
        loadDetails(factory.messageDrivenBeans().as(), "default-mdb-instance-pool");
    }
}
