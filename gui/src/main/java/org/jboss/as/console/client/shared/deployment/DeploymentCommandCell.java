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

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.model.DeploymentRecord;

/**
 * Cell that renders a button capable of firing off a DeploymentCommand.
 *
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 */
public class DeploymentCommandCell extends ActionCell<DeploymentRecord> {

    private DeploymentCommand command;

    /**
     * Create a new DeploymentCommandCell
     *
     * @param executor The delegate that knows how to execute the command on the server.
     * @param command The command that the button will fire.
     */
    public DeploymentCommandCell(DeployCommandExecutor executor, DeploymentCommand command) {
        super("", new DeploymentCommandDelegate(executor, command));
        this.command = command;
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, DeploymentRecord record, NativeEvent event, ValueUpdater<DeploymentRecord> valueUpdater) {
        if (isCellActive(record)) {
            super.onBrowserEvent(context, parent, record, event, valueUpdater);
        }
    }

    @Override
    protected void onEnterKeyDown(Context context, Element parent, DeploymentRecord record, NativeEvent event, ValueUpdater<DeploymentRecord> valueUpdater) {
        if (isCellActive(record)) {
            super.onEnterKeyDown(context, parent, record, event, valueUpdater);
        }
    }

    private boolean isCellActive(DeploymentRecord record) {
        return record.isPersistent();
    }

    @Override
    public void render(Context context, DeploymentRecord record, SafeHtmlBuilder sb) {
        if (isCellActive(record)) {
            SafeHtml html = new SafeHtmlBuilder()
                .appendHtmlConstant("<a href='javascript:void(0)' tabindex=\"-1\" class='textlink-cell'>")
                .appendHtmlConstant(this.command.getLabel(record))
                .appendHtmlConstant("</a>")
                .toSafeHtml();
            sb.append(html);
        } else {
            SafeHtml html = new SafeHtmlBuilder()
                .appendHtmlConstant("<i>")
                .appendHtmlConstant(Console.CONSTANTS.common_label_useFileSystem())
                .appendHtmlConstant("</i>")
                .toSafeHtml();
            sb.append(html);
        }
    }

}
