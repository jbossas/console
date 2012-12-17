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

import com.google.gwt.user.client.ui.HasName;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.widgets.forms.Binding;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public interface ServerGroupRecord extends HasName
{
    @Binding(detypedName = "profile")
    public String getProfileName();
    public void setProfileName(String name);

    public void setProperties(List<PropertyRecord> props);
    public List<PropertyRecord> getProperties();

    public Jvm getJvm();
    public void setJvm(Jvm jvm);

    @Binding(detypedName = "socket-binding-group")
    public String getSocketBinding();
    public void setSocketBinding(String socketBindingRef);
}
