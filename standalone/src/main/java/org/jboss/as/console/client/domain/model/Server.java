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

package org.jboss.as.console.client.domain.model;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public interface Server {

    String getName();
    void setName(String name);
    
    String getGroup();
    void setGroup(String group);

    @Binding(detypedName = "auto-start")
    boolean isAutoStart();
    void setAutoStart(boolean b);

    @Binding(detypedName = "none", ignore = true)
    boolean isStarted();
    void setStarted(boolean b);

    @Binding(detypedName = "socket-binding-group")
    String getSocketBinding();
    void setSocketBinding(String socketBindingRef);

    @Binding(detypedName = "socket-binding-port-offset")
    int getPortOffset();
    void setPortOffset(int offset);

    Jvm getJvm();
    void setJvm(Jvm jvm);
}
