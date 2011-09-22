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

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public interface AddressingPattern {

    String getPattern();
    void setPattern(String pattern);

    @Binding(detypedName = "dead-letter-address")
    String getDeadLetterQueue();
    void setDeadLetterQueue(String deadLetterQueue);

    @Binding(detypedName = "expiry-address")
    String getExpiryQueue();
    void setExpiryQueue(String expiryQueue);

    @Binding(detypedName = "redelivery-delay")
    int getRedeliveryDelay();
    void setRedeliveryDelay(int redeliveryDelay);

    @Binding(detypedName = "max-delivery-attempts")
    int getMaxDelivery();
    void setMaxDelivery(int maxAttempts);

}
