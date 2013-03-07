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
package org.jboss.as.console.client.tools.mbui.workbench.repository;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import org.jboss.as.console.client.tools.mbui.workbench.ReifyEvent;
import org.jboss.as.console.client.tools.mbui.workbench.ResetEvent;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;

/**
 * Lists the available interaction units and let the user create new interaction units.
 *
 * Events fired:
 * <ul>
 *     <li>Reify</li>
 * </ul>
 *
 * @author Harald Pehl
 * @date 10/30/2012
 */
public class RepositoryPresenter extends PresenterWidget<RepositoryPresenter.MyView>
{

    public interface MyView extends View
    {
        void setPresenter(RepositoryPresenter presenter);
    }


    @Inject
    public RepositoryPresenter(final EventBus eventBus, final MyView view)
    {
        super(eventBus, view);
    }

    @Override
    protected void onBind()
    {
        super.onBind();
        getView().setPresenter(this);
    }

    public void visualize(final Sample sample)
    {
        DialogVisualization visualization = new DialogVisualization(sample.getDialog());
        DefaultWindow window = new DefaultWindow("Visualization");
        window.setWidth(800);
        window.setHeight(600);
        window.trapWidget(new ScrollPanel(visualization.getChart()));
        window.center();
    }

    public void reify(final Sample sample)
    {
        ReifyEvent.fire(this, new ReifyEvent(sample));
    }

    public void reset() {
        ResetEvent.fire(this, new ResetEvent());
    }
}
