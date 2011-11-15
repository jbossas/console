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

package org.jboss.as.console.client.shared.general.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 5/17/11
 */
@Address("/interface={0}")
public interface Interface {

    String getName();
    void setName(String name);

    @Binding(detypedName = "any-address")
    boolean isAnyAddress();
    void setAnyAddress(boolean b);

    @Binding(detypedName = "any-ipv4-address")
    boolean isAnyIP6Address();
    void setAnyIP6Address(boolean b);

    @Binding(detypedName = "any-ipv6-address")
    boolean isAnyIP4Address();
    void setAnyIP4Address(boolean b);

    @Binding(expr = true, detypedName = "inet-address")
    String getInetAddress();
    void setInetAddress(String addr);

    @Binding(detypedName = "loopback")
    boolean isLoopback();
    void setLoopback(boolean b);

    @Binding(detypedName = "loopback-address", expr = true)
    String getLoopbackAddress();
    void setLoopbackAddress(String addr);

    boolean isMulticast();
    void setMulticast(boolean b);

    @Binding(expr = true)
    String getNic();
    void setNic(String addr);

    @Binding(expr = true, detypedName = "nic-match")
    String getNicMatch();
    void setNicMatch(String addr);

    @Binding(detypedName = "point-to-point")
    boolean isPointToPoint();
    void setPointToPoint(boolean b);

    @Binding(detypedName = "public-address")
    boolean isPublicAddress();
    void setPublicAddress(boolean b);

    @Binding(detypedName = "site-local-address")
    boolean isSiteLocal();
    void setSiteLocal(boolean b);

    @Binding(detypedName = "link-local-address")
    boolean isLinkLocal();
    void setLinkLocal(boolean b);

    @Binding(expr = true, detypedName = "subnet-match")
    String getSubnetMatch();
    void setSubnetMatch(String addr);

    boolean isUp();
    void setUp(boolean b);

    boolean isVirtual();
    void setVirtual(boolean b);


    // transient

    @Binding(skip = true)
    String getAddressWildcard();
    void setAddressWildcard(String selector);

}
