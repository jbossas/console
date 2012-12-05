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
package org.jboss.as.console.client.shared.deployment;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.jboss.as.console.client.shared.deployment.model.DeploymentData;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYUP;

/**
* @author Harald Pehl
* @date 11/28/2012
*/
public class DeploymentDataCell<T extends DeploymentData> extends AbstractCell<T>
{
    private final DeploymentBrowser deploymentBrowser;

    DeploymentDataCell(final DeploymentBrowser deploymentBrowser)
    {
        super(CLICK, KEYUP);
        this.deploymentBrowser = deploymentBrowser;
    }

    @Override
    public void render(final Context context, final T value, final SafeHtmlBuilder sb)
    {
        sb.appendEscaped(value.getName());
    }

    @Override
    public void onBrowserEvent(final Context context, final Element parent, final T value, final NativeEvent event,
            final ValueUpdater<T> valueUpdater)
    {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        String type = event.getType();
        if (CLICK.equals(type))
        {
            deploymentBrowser.updateContext(value);
        }
        else if (KEYUP.equals(type) && event.getKeyCode() == 32) // space
        {
            deploymentBrowser.updateContext(value);
        }
    }
}
