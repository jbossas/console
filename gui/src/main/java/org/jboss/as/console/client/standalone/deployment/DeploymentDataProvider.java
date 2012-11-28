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
package org.jboss.as.console.client.standalone.deployment;

import com.google.gwt.user.client.Command;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.deployment.model.DeploymentData;

import java.util.List;

/**
 * @author Harald Pehl
 * @date 11/28/2012
 */
public class DeploymentDataProvider<T extends DeploymentData> extends AsyncDataProvider<T>
{
    private Command command;

    public void exec(Command command)
    {
        this.command = command;
    }

    @Override
    protected void onRangeChanged(final HasData<T> display)
    {
        command.execute();
    }

    class UpdateRowsCallback extends SimpleCallback<List<T>>
    {
        @Override
        public void onSuccess(final List<T> result)
        {
            updateRowCount(result.size(), true);
            updateRowData(0, result);
        }
    }
}
