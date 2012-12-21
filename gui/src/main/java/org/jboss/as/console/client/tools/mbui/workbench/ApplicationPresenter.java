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
package org.jboss.as.console.client.tools.mbui.workbench;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.tools.mbui.workbench.context.ContextPresenter;
import org.jboss.as.console.client.tools.mbui.workbench.repository.RepositoryPresenter;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
public class ApplicationPresenter extends Presenter<ApplicationPresenter.MyView, ApplicationPresenter.MyProxy>
{
    public interface MyView extends View
    {
    }

    @NameToken("mbui")
    @ProxyStandard
    public interface MyProxy extends Proxy<ApplicationPresenter>
    {
    }

    @ContentSlot
    public static final Type<RevealContentHandler<?>> TYPE_SetMainContent = new Type<RevealContentHandler<?>>();

    public final static Object Header_Slot = new Object();
    public final static Object Repository_Slot = new Object();
    public final static Object Context_Slot = new Object();
    public final static Object Footer_Slot = new Object();

    final HeaderPresenter headerPresenter;
    final RepositoryPresenter repositoryPresenter;
    final ContextPresenter contextPresenter;
    final FooterPresenter footerPresenter;

    @Inject
    public ApplicationPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
            final HeaderPresenter headerPresenter, final RepositoryPresenter repositoryPresenter,
            final ContextPresenter contextPresenter, final FooterPresenter footerPresenter)
    {
        super(eventBus, view, proxy);
        this.headerPresenter = headerPresenter;
        this.repositoryPresenter = repositoryPresenter;
        this.contextPresenter = contextPresenter;
        this.footerPresenter = footerPresenter;
    }

    @Override
    protected void revealInParent()
    {
        RevealContentEvent.fire(this, MainLayoutPresenter.TYPE_MainContent, this);
    }

    @Override
    protected void onReveal()
    {
        super.onReveal();
        setInSlot(Header_Slot, headerPresenter);
        setInSlot(Repository_Slot, repositoryPresenter);
        setInSlot(Context_Slot, contextPresenter);
        setInSlot(Footer_Slot, footerPresenter);
    }

    @Override
    protected void onHide()
    {
        super.onHide();
        removeFromSlot(Footer_Slot, footerPresenter);
        removeFromSlot(Context_Slot, contextPresenter);
        removeFromSlot(Repository_Slot, repositoryPresenter);
        removeFromSlot(Header_Slot, headerPresenter);
    }
}
