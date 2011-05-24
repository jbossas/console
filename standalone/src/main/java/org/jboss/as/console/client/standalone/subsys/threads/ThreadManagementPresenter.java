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

package org.jboss.as.console.client.standalone.subsys.threads;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.standalone.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.shared.BeanFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/9/11
 */
public class ThreadManagementPresenter extends Presenter<ThreadManagementPresenter.MyView, ThreadManagementPresenter.MyProxy> {

    private final PlaceManager placeManager;

    BeanFactory beanFactory = GWT.create(BeanFactory.class);

    @ProxyCodeSplit
    @NameToken(NameTokens.ThreadManagementPresenter)
    public interface MyProxy extends Proxy<ThreadManagementPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
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
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ServerMgmtApplicationPresenter.TYPE_MainContent, this);
    }

    // -----------------------------------------------

    public List<ThreadFactoryRecord> getFactoryRecords() {

        List<ThreadFactoryRecord> result = new ArrayList<ThreadFactoryRecord>();
        ThreadFactoryRecord record = beanFactory.threadFactory().as();
        record.setName("Default Thread Factory");
        record.setGroup("system");
        record.setPriority(1);

        result.add(record);

        return result;
    }

    public void onUpdateRecord(ThreadFactoryRecord record) {

        String name = record.getName();
        if(name!=null)
        {
            Console.MODULES.getMessageCenter().notify(
                    new Message("Record saved: "+ name, Message.Severity.Info)
            );
        }
    }
}

