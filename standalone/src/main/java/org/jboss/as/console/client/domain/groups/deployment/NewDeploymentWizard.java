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

package org.jboss.as.console.client.domain.groups.deployment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.BeanFactory;

/**
 * @author Heiko Braun
 * @date 4/7/11
 */
public class NewDeploymentWizard  {

    private VerticalPanel layout;

    private DeploymentsPresenter presenter;

    private DeckPanel deck;

    private BeanFactory factory = GWT.create(BeanFactory.class);

    private DeploymentStep1 step1;
    private DeploymentStep2 step2;

    public NewDeploymentWizard(final DeploymentsPresenter presenter) {
        super();
        this.presenter = presenter;

        deck = new DeckPanel();

        step1 = new DeploymentStep1(this);
        step2 = new DeploymentStep2(this);

        deck.add(step1.asWidget());
        deck.add(step2.asWidget());

        deck.showWidget(0);

    }

    public DeploymentsPresenter getPresenter() {
        return presenter;
    }

    public Widget asWidget() {
        return deck;
    }


    public void onUploadComplete(String fileName, String hash) {
        System.out.println(fileName +" > "+hash);

        DeploymentReference deploymentRef = factory.deploymentReference().as();
        deploymentRef.setHash(hash);
        deploymentRef.setName(fileName);
        step2.edit(deploymentRef);

        deck.showWidget(1); // proceed to step2
    }
}
