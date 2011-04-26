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

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.model.ServerGroupImpl;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.DeploymentStore;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public class ServerGroupModelTest {

    private static Injector injector;

    @BeforeClass
    public static void init() {
        injector = Guice.createInjector(new TestModule());
    }

    @Test
    public void loadServerGroups() throws Exception
    {
        ServerGroupStore store = injector.getInstance(ServerGroupStore.class);
        TestCallback<List<ServerGroupRecord>> callback = new TestCallback<List<ServerGroupRecord>>() {

            @Override
            public void onSuccess(List<ServerGroupRecord> result) {
                assertTrue("No groups loaded", result.size()>0);
                assertEquals("main-server-group",result.get(0).getGroupName());
                didCallback = true;
            }
        };

        store.loadServerGroups(callback);

        synchronized (callback) {
            callback.wait(500);
        }

        assertTrue("Callback not executed", callback.hasBeenExecuted());
    }

    @Test
    public void loadDeployments() throws Exception
    {
        DeploymentStore store = injector.getInstance(DeploymentStore.class);
        TestCallback<List<DeploymentRecord>> callback = new TestCallback<List<DeploymentRecord>>() {

            @Override
            public void onSuccess(List<DeploymentRecord> result) {
                assertTrue("Expected no deployments", result.isEmpty());
                didCallback = true;
            }
        };

        ServerGroupRecord group = new ServerGroupImpl();
        group.setGroupName("main-server-group");
        group.setProfileName("default");

        //group.setJvm(new Jvm());
        group.setSocketBinding("standard-sockets");
        List<ServerGroupRecord> groups = new ArrayList<ServerGroupRecord>();
        groups.add(group);

        store.loadDeployments(groups, callback);

        synchronized (callback) {
            callback.wait(500);
        }

        assertTrue("Callback not executed", callback.hasBeenExecuted());
    }


}
