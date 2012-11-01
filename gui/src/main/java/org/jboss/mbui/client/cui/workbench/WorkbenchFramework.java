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

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.ballroom.client.spi.Framework;

/**
 * @author Harald Pehl
 * @date 11/01/2012
 */
public class WorkbenchFramework implements Framework
{
    private final static BeanFactory factory = GWT.create(BeanFactory.class);

    @Override
    public EventBus getEventBus()
    {
        return Workbench.GINJECTOR.getEventBus();
    }

    @Override
    public PlaceManager getPlaceManager()
    {
        return Workbench.GINJECTOR.getPlaceManager();
    }

    @Override
    public AutoBeanFactory getBeanFactory()
    {
        return factory;
    }
}
