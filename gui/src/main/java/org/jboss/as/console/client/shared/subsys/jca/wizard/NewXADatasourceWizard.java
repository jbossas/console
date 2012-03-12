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
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.jca.DataSourcePresenter;
import org.jboss.as.console.client.shared.subsys.jca.model.JDBCDriver;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.ballroom.client.widgets.window.TrappedFocusPanel;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/15/11
 */
public class NewXADatasourceWizard {

    private DataSourcePresenter presenter;

    private DeckPanel deck;
    private XADatasourceStep2 step2;
    private XADatasourceStep3 step3;
    private XADatasourceStep4 step4;

    private XADataSource baseAttributes = null;
    private XADataSource driverAttributes = null;
    private List<PropertyRecord> properties;
    private ApplicationProperties bootstrap;
    private List<JDBCDriver> drivers;
    private TrappedFocusPanel trap;

    public NewXADatasourceWizard(
            DataSourcePresenter presenter,
            List<JDBCDriver> drivers, ApplicationProperties bootstrap) {
        this.presenter = presenter;
        this.bootstrap = bootstrap;
        this.drivers = drivers;
    }

    public List<JDBCDriver> getDrivers() {
        return drivers;
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

        deck.add(new XADatasourceStep1(this).asWidget());

        step2 = new XADatasourceStep2(this);
        deck.add(step2.asWidget());

        step3 = new XADatasourceStep3(this);
        deck.add(step3.asWidget());

        step4 = new XADatasourceStep4(this);
        deck.add(step4.asWidget());

        trap = new TrappedFocusPanel(deck);

        deck.showWidget(0);

        return trap;
    }

    public DataSourcePresenter getPresenter() {
        return presenter;
    }

    public void onConfigureBaseAttributes(XADataSource entity) {
        this.baseAttributes = entity;
        step2.edit(entity);
        deck.showWidget(1);
    }

    public void onConfigureDriver(XADataSource entity) {
        this.driverAttributes = entity;
        step3.edit(entity);
        deck.showWidget(2);
    }

    public void onFinish(XADataSource updatedEntity) {

        // merge previous attributes into single entity

        updatedEntity.setName(baseAttributes.getName());
        updatedEntity.setJndiName(baseAttributes.getJndiName());
        updatedEntity.setEnabled(baseAttributes.isEnabled());

        updatedEntity.setDataSourceClass(driverAttributes.getDataSourceClass());
        updatedEntity.setDriverName(driverAttributes.getDriverName());
        updatedEntity.setDriverClass(driverAttributes.getDriverClass());
        updatedEntity.setMajorVersion(driverAttributes.getMajorVersion());
        updatedEntity.setMinorVersion(driverAttributes.getMinorVersion());

        updatedEntity.setProperties(properties);

        updatedEntity.setPoolName(baseAttributes.getName()+"_Pool");

        presenter.onCreateXADatasource(updatedEntity);
    }

    public void onConfigureProperties(List<PropertyRecord> properties) {
        // merge it right away

        this.properties = properties;

        step4.edit(driverAttributes);
        deck.showWidget(3);
    }

    public ApplicationProperties getBootstrap() {
        return this.bootstrap;
    }
}
