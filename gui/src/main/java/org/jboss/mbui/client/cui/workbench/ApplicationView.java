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
package org.jboss.mbui.client.cui.workbench;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
public class ApplicationView extends ViewImpl implements ApplicationPresenter.MyView
{
    public interface Binder extends UiBinder<Widget, ApplicationView>
    {
    }


    private final Widget widget;
    @UiField SimplePanel mainPanel;
    @UiField SimplePanel repositoryPanel;
    @UiField SimplePanel contextPanel;

    @Inject
    public ApplicationView(final Binder binder)
    {
        this.widget = binder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget()
    {
        return widget;
    }


    @Override
    public void setInSlot(Object slot, Widget widget)
    {
        if (slot == ApplicationPresenter.TYPE_SetMainContent)
        {
            mainPanel.setWidget(widget);
        }
        else if (slot == ApplicationPresenter.Repository_Slot)
        {
            repositoryPanel.setWidget(widget);
        }
        else if (slot == ApplicationPresenter.Context_Slot)
        {
            contextPanel.setWidget(widget);
        }
        else
        {
            super.setInSlot(slot, widget);
        }
    }
}
