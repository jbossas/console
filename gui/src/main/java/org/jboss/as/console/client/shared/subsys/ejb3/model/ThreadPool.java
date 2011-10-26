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
package org.jboss.as.console.client.shared.subsys.ejb3.model;

import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * @author David Bosschaert
 */
@Address("/subsystem=ejb3/thread-pool={0}")
public interface ThreadPool extends NamedEntity {
    @Override
    @Binding(detypedName="name", key=true)
    @FormItem(defaultValue="",
              localLabel="common_label_name",
              required=true,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="TEXT_BOX",
              order=1)
    String getName();
    @Override
    void setName(String name);

    @Binding(detypedName="keepalive-time")
    @FormItem(defaultValue="100",
              label="Keep-Alive Time",
              required=true,
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              order=10)
    int getKeepAliveTime();
    void setKeepAliveTime(int millis);

    @Binding(detypedName="max-threads")
    @FormItem(defaultValue="4",
              label="Max Threads",
              required=true,
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              order=5)
    int getMaxThreads();
    void setMaxThreads(int threads);
}
