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

package org.jboss.as.console.client.shared.subsys;

import org.jboss.as.console.client.domain.profiles.CurrentProfileSelection;
import org.jboss.dmr.client.ModelNode;

import javax.inject.Inject;

/**
 * @author Heiko Braun
 * @date 5/25/11
 */
public class Baseadress {

    @Inject private static CurrentProfileSelection profileSelection;
    @Inject private static Baseadress instance;

    @Inject
    public Baseadress(CurrentProfileSelection profileSelection) {
        this.profileSelection = profileSelection;
    }

    public static ModelNode get() {
        return instance.getAdress();
    }

    public ModelNode getAdress() {
        ModelNode baseAddress = new ModelNode();
        baseAddress.setEmptyList();

        if(profileSelection.getName()!=null)
            baseAddress.add("profile", profileSelection.getName());

        return baseAddress;
    }

}
