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
package org.jboss.as.console.client.shared.subsys.osgi.runtime.model;

import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * @author David Bosschaert
 */
@Address("/subsystem=osgi/bundle={0}")
public interface OSGiBundle extends NamedEntity {
    @Override
    @Binding(detypedName="id", key=true)
    @FormItem(localLabel="subsys_osgi_bundleID",
              formItemTypeForEdit="TEXT",
              order=1)
    public String getName();
    @Override
    public void setName(String name);

    @FormItem(localLabel="subsys_osgi_bundleState", order=6)
    public String getState();
    public void setState(String s);

    @Binding(detypedName="symbolic-name")
    @FormItem(localLabel="subsys_osgi_bundleSymbolicName", order=2)
    public String getSymbolicName();
    public void setSymbolicName(String bsn);

    @Binding(detypedName="startlevel")
    @FormItem(localLabel="subsys_osgi_capabilityStartLevel",
              formItemTypeForEdit="NUMBER_BOX",
              order=5)
    public int getStartLevel();
    public void setStartLevel(int sl);

    @FormItem(localLabel="common_label_type", order=4)
    public String getType();
    public void setType(String type);

    @FormItem(localLabel="subsys_osgi_bundleVersion", order=3)
    public String getVersion();
    public void setVersion(String version);
}
