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
package org.jboss.as.console.client.tools.mbui.workbench.preview;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.tools.mbui.workbench.ActivateEvent;
import org.jboss.as.console.client.tools.mbui.workbench.ApplicationPresenter;
import org.jboss.as.console.client.tools.mbui.workbench.PassivateEvent;
import org.jboss.as.console.client.tools.mbui.workbench.ReifyEvent;
import org.jboss.as.console.client.tools.mbui.workbench.ResetEvent;
import org.jboss.as.console.client.tools.mbui.workbench.repository.SampleRepository;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.mbui.gui.behaviour.NavigationDelegate;
import org.jboss.mbui.gui.behaviour.as7.CoreGUIContext;
import org.jboss.mbui.gui.kernel.Framework;
import org.jboss.mbui.gui.kernel.Kernel;
import org.jboss.mbui.model.structure.QName;

import static org.jboss.as.console.client.tools.mbui.workbench.NameTokens.preview;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 10/30/2012
 */
public class PreviewPresenter extends Presenter<PreviewPresenter.MyView, PreviewPresenter.MyProxy>
        implements ReifyEvent.ReifyHandler, ActivateEvent.ActivateHandler, ResetEvent.ResetHandler,
        PassivateEvent.PassivateHandler, NavigationDelegate
{
    private final Kernel kernel;

    @Inject
    public PreviewPresenter(
            final EventBus eventBus,
            final MyView view,
            final MyProxy proxy,
            final DispatchAsync dispatcher,
            final SampleRepository sampleRepository)
    {
        super(eventBus, view, proxy);

        CoreGUIContext globalContext = new CoreGUIContext(
                Console.MODULES.getCurrentSelectedProfile(),
                Console.MODULES.getCurrentUser()
        );

        // mbui kernel instance
        this.kernel = new Kernel(sampleRepository, new Framework() {
            @Override
            public DispatchAsync getDispatcher() {
                return dispatcher;
            }
        }, globalContext);
    }

    @Override
    public void onNavigation(QName source, QName target) {
        System.out.println("absolute navigation " + source + ">" + target);
    }

    @Override
    protected void onBind()
    {
        super.onBind();
        getEventBus().addHandler(ReifyEvent.getType(), this);
        getEventBus().addHandler(ResetEvent.getType(), this);
    }

    @Override
    protected void revealInParent()
    {
        RevealContentEvent.fire(this, ApplicationPresenter.TYPE_SetMainContent, this);
    }

    // in real this would be wired to Presenter.onBind()
    @Override
    public void onReify(final ReifyEvent event)
    {
        kernel.reify(event.getSample().getName(), new AsyncCallback<Widget>() {
            @Override
            public void onFailure(Throwable throwable) {
                Console.error("Reification failed", throwable.getMessage());
            }

            @Override
            public void onSuccess(Widget widget) {
                // TODO Call activate instead?
//                kernel.activate();
                getView().show(widget);
            }
        });
    }

    @Override
    public void onActivate(ActivateEvent event) {
        kernel.activate();
    }

    @Override
    public void onReset(ResetEvent event) {
        kernel.reset();
    }

    @Override
    public void onPassivate(PassivateEvent event) {
        kernel.passivate();
    }

    public interface MyView extends View
    {
        void show(Widget widget);
    }

    @ProxyStandard
    @NameToken(preview)
    public interface MyProxy extends ProxyPlace<PreviewPresenter>
    {
    }
}
