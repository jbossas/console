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

package org.jboss.as.console.client.shared.subsys.messaging.model;

import org.jboss.as.console.client.widgets.forms.Binding;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public interface MessagingProvider {

    String getName();
    void setName(String name);

    @Binding(detypedName = "persistence-enabled")
    boolean isPersistenceEnabled();
    void setPersistenceEnabled(boolean persistenceEnabled);

    List<SecurityPattern> getSecurityPatterns();
    void setSecurityPatterns(List<SecurityPattern> patterns);

    List<AddressingPattern> getAddressPatterns();
    void setAddressPatterns(List<AddressingPattern> addrPatterns);

    String getConnectorBinding();
    void setConnectorBinding(String binding);

    String getAcceptorBinding();
    void setAcceptorBinding(String binding);

    @Binding(detypedName = "security-enabled")
    boolean isSecurityEnabled();
    void setSecurityEnabled(boolean b);

    @Binding(detypedName = "message-counter-enabled")
    boolean isMessageCounterEnabled();
    void setMessageCounterEnabled(boolean b);
}
