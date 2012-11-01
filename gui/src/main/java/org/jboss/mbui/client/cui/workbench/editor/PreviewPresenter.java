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
package org.jboss.mbui.client.cui.workbench.editor;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.mbui.client.aui.aim.InteractionUnit;
import org.jboss.mbui.client.cui.Context;
import org.jboss.mbui.client.cui.workbench.ApplicationPresenter;
import org.jboss.mbui.client.cui.workbench.reification.ReificationWidget;
import org.jboss.mbui.client.cui.workbench.reification.Reificator;
import org.jboss.mbui.client.cui.workbench.reification.ReifyEvent;
import org.jboss.mbui.client.cui.workbench.repository.Sample;

import static org.jboss.mbui.client.cui.workbench.NameTokens.preview;

/**
 * Listens for
 * <ul>
 *     <li>Reify</li>
 * </ul>
 *
 * @author Harald Pehl
 * @date 10/30/2012
 */
public class PreviewPresenter extends Presenter<PreviewPresenter.MyView, PreviewPresenter.MyProxy> implements ReifyEvent.ReifyHandler
{
    public interface MyView extends View
    {
        void show(ReificationWidget interactionUnit);
    }

    @ProxyStandard
    @NameToken(preview)
    public interface MyProxy extends ProxyPlace<PreviewPresenter>
    {
    }

    private final Reificator reificator;

    @Inject
    public PreviewPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final Reificator reificator)
    {
        super(eventBus, view, proxy);
        this.reificator = reificator;
        getEventBus().addHandler(ReifyEvent.getType(), this);
    }

    @Override
    protected void revealInParent()
    {
        RevealContentEvent.fire(this, ApplicationPresenter.TYPE_SetMainContent, this);
    }

    @Override
    public void onReify(final ReifyEvent event)
    {
        Sample sample = event.getSample();
        if (sample != null)
        {
            InteractionUnit interactionUnit = sample.build();
            if (interactionUnit != null)
            {
                ReificationWidget reificationWidget = reificator.reify(interactionUnit, new Context());
                if (reificationWidget != null)
                {
                    getView().show(reificationWidget);
                }
            }
        }
    }
}
