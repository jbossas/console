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

package org.jboss.as.console.client.shared.subsys.jca.wizard;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.shared.subsys.jca.DataSourcePresenter;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.JDBCDriver;
import org.jboss.ballroom.client.widgets.window.TrappedFocusPanel;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/15/11
 */
public class NewDatasourceWizard {

    private DataSourcePresenter presenter;

    private DeckPanel deck;
    private DatasourceStep2 step2;
    private DataSourceStep3 step3;


    private DataSource baseAttributes = null;
    private DataSource driverAttributes = null;
    private ApplicationProperties bootstrap;
    private List<JDBCDriver> drivers;
    private TrappedFocusPanel trap;

    public NewDatasourceWizard(
            DataSourcePresenter presenter,
            List<JDBCDriver> drivers, ApplicationProperties bootstrap) {
        this.presenter = presenter;
        this.bootstrap = bootstrap;
        this.drivers = drivers;
    }

    public List<JDBCDriver> getDrivers() {
        return drivers;
    }

    ApplicationProperties getBootstrap() {
        return bootstrap;
    }

    public Widget asWidget() {


        deck = new DeckPanel() {
            @Override
            public void showWidget(int index) {
                super.showWidget(index);
                trap.getFocus().reset(getWidget(index).getElement());
                trap.getFocus().onFirstInput();
            }
        };

        deck.add(new DatasourceStep1(this).asWidget());

        step2 = new DatasourceStep2(this);
        deck.add(step2.asWidget());

        step3 = new DataSourceStep3(this);
        deck.add(step3.asWidget());


        trap = new TrappedFocusPanel(deck);

        deck.showWidget(0);

        return trap;
    }

    public DataSourcePresenter getPresenter() {
        return presenter;
    }

    public void onConfigureBaseAttributes(DataSource entity) {
        this.baseAttributes = entity;
        step2.edit(entity);
        deck.showWidget(1);
    }

    public void onConfigureDriver(DataSource entity) {
        this.driverAttributes = entity;
        step3.edit(entity);
        deck.showWidget(2);
    }

    public void onFinish(DataSource updatedEntity) {

        // merge previous attributes into single entity

        updatedEntity.setName(baseAttributes.getName());
        updatedEntity.setJndiName(baseAttributes.getJndiName());
        updatedEntity.setEnabled(baseAttributes.isEnabled());

        updatedEntity.setDriverName(driverAttributes.getDriverName());
        updatedEntity.setDriverClass(driverAttributes.getDriverClass());
        updatedEntity.setMajorVersion(driverAttributes.getMajorVersion());
        updatedEntity.setMinorVersion(driverAttributes.getMinorVersion());

        updatedEntity.setPoolName(baseAttributes.getName()+"_Pool");

        presenter.onCreateDatasource(updatedEntity);
    }

}
