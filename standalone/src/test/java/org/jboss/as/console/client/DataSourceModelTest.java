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

package org.jboss.as.console.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.subsys.jca.DataSourceStore;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Heiko Braun
 * @date 4/19/11
 */
public class DataSourceModelTest {

    private static Injector injector;

    @BeforeClass
    public static void init() {
        injector = Guice.createInjector(new TestModule());
    }

    @Test
    public void loadDataSources() throws Exception
    {
        DataSourceStore store = injector.getInstance(DataSourceStore.class);
        TestCallback<List<DataSource>> callback = new TestCallback<List<DataSource>>() {

            @Override
            public void onSuccess(List<DataSource> result) {
                assertTrue("Expected at least default datasource h2", result.size()>0);

                didCallback = true;
            }
        };

        store.loadDataSources("default", callback);

        synchronized (callback) {
            callback.wait(500);
        }

        assertTrue("Callback not executed", callback.hasBeenExecuted());
    }

    @Test
    public void createDataSources() throws Exception
    {
        DataSourceStore store = injector.getInstance(DataSourceStore.class);
        TestCallback<Boolean> callback = new TestCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean success) {
                assertTrue("Expected successful outcome", success);

                didCallback = true;
            }
        };

        DataSource entity = createEntity();
        store.createDataSource("default", entity, callback);

        synchronized (callback) {
            callback.wait(500);
        }

        assertTrue("Callback not executed", callback.hasBeenExecuted());
    }

    // https://issues.jboss.org/browse/JBAS-9341
    @Test
    @Ignore
    public void enableDataSources() throws Exception
    {
        DataSourceStore store = injector.getInstance(DataSourceStore.class);

        DataSource entity = createEntity();
        entity.setName("java:/H2DS"); // default DS should exist
        TestCallback<Boolean> secondCallback = new TestCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean success) {
                assertTrue("Expected successful outcome", success);

                didCallback = true;
            }
        };

        store.enableDataSource("default", entity, false, secondCallback);

        synchronized (secondCallback) {
            secondCallback.wait(500);
        }

        assertTrue("Callback not executed", secondCallback.hasBeenExecuted());
    }


    private DataSource createEntity() {
        DataSource entity = injector.getInstance(BeanFactory.class).dataSource().as();
        entity.setName("myDS_"+System.currentTimeMillis());
        entity.setJndiName("jdbc/" + entity.getName());
        entity.setConnectionUrl("jdbc:h2:mem:anotherOne;DB_CLOSE_DELAY=-1");
        entity.setDriverClass("org.h2.Driver");
        entity.setDriverName("org.h2.Driver#1.2");
        entity.setEnabled(false);
        entity.setPoolName(entity.getName()+"_Pool");
        entity.setUsername("user");
        entity.setPassword("pass");
        return entity;
    }

}
