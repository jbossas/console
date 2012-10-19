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

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.domain.runtime.DomainRuntimePresenter;
import org.jboss.as.console.client.standalone.ServerMgmtApplicationPresenter;
import org.jboss.as.console.client.standalone.runtime.StandaloneRuntimePresenter;

import javax.inject.Inject;

/**
 * @author Heiko Braun
 * @date 5/25/11
 */
public class RevealStrategy {

    private ApplicationProperties bootstrap;

    @Inject
    public RevealStrategy(ApplicationProperties bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void revealInParent(Presenter presenter) {
         if(bootstrap.isStandalone())
            RevealContentEvent.fire(presenter, ServerMgmtApplicationPresenter.TYPE_MainContent, presenter);
        else
            RevealContentEvent.fire(presenter, ProfileMgmtPresenter.TYPE_MainContent, presenter);
    }

    public void revealInRuntimeParent(Presenter presenter) {
         if(bootstrap.isStandalone())
            RevealContentEvent.fire(presenter, StandaloneRuntimePresenter.TYPE_MainContent, presenter);
        else
            RevealContentEvent.fire(presenter, DomainRuntimePresenter.TYPE_MainContent, presenter);
    }
}
