/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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

/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public class HostModelTest {

    private static Injector injector;

    @BeforeClass
    public static void init() {
        injector = Guice.createInjector(new TestModule());
    }

    @Test
    public void loadHosts() throws Exception
    {
        HostInformationStore store = injector.getInstance(HostInformationStore.class);
        TestCallback<List<Host>> callback = new TestCallback<List<Host>>() {

            @Override
            public void onSuccess(List<Host> result) {
                assertTrue("No hosts loaded", result.size()>0);
                assertEquals("local",result.get(0).getName());
                didCallback = true;
            }
        };

        store.getHosts(callback);

        synchronized (callback) {
            callback.wait(500);
        }

        assertTrue("Callback not executed", callback.hasBeenExecuted());
    }

    @Test
    public void loadServerConfigs() throws Exception
    {
        HostInformationStore store = injector.getInstance(HostInformationStore.class);
        TestCallback<List<Server>> callback = new TestCallback<List<Server>>() {

            @Override
            public void onSuccess(List<Server> result) {
                assertTrue("No server configurations loaded", result.size()>0);
                assertEquals("server-one",result.get(0).getName());
                didCallback = true;
            }
        };

        store.getServerConfigurations("local", callback);

        synchronized (callback) {
            callback.wait(500);
        }

        assertTrue("Callback not executed", callback.hasBeenExecuted());
    }

    @Test
    public void loadServerInstances() throws Exception
    {
        HostInformationStore store = injector.getInstance(HostInformationStore.class);
        TestCallback<List<ServerInstance>> callback = new TestCallback<List<ServerInstance>>() {

            @Override
            public void onSuccess(List<ServerInstance> result) {
                assertTrue("No servers instances loaded", result.size()>0);
                assertEquals("server-one",result.get(0).getName());
                didCallback = true;
            }
        };

        store.getServerInstances("local", callback);

        synchronized (callback) {
            callback.wait(500);
        }

        assertTrue("Callback not executed", callback.hasBeenExecuted());
    }
}

