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
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.model.SubsystemStore;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public class ProfileModelTest {

    private static Injector injector;
    private static final String EXPECTED_SUBSYSTEM = "datasources";

    @BeforeClass
    public static void init() {
        injector = Guice.createInjector(new TestModule());
    }

    @Test
    public void loadProfiles() throws Exception
    {
        ProfileStore profileStore = injector.getInstance(ProfileStore.class);
        TestCallback<List<ProfileRecord>> callback = new TestCallback<List<ProfileRecord>>() {

            @Override
            public void onSuccess(List<ProfileRecord> result) {
                assertFalse(result.isEmpty());
                assertEquals("default", result.get(0).getName());
                didCallback = true;
            }
        };

        profileStore.loadProfiles(callback);

        synchronized (callback) {
            callback.wait(500);
        }

        assertTrue("Callback not executed", callback.hasBeenExecuted());
    }

    @Test
    public void loadSubsystems() throws Exception
    {
        SubsystemStore subsystemStore = injector.getInstance(SubsystemStore.class);
        TestCallback<List<SubsystemRecord>> callback = new TestCallback<List<SubsystemRecord>>() {

            @Override
            public void onSuccess(List<SubsystemRecord> result) {
                assertTrue("No subsystems found", result.size()>0);

                boolean match = false;
                for(SubsystemRecord subsys : result)
                {
                    if(!match)
                        match = subsys.getTitle().equals(EXPECTED_SUBSYSTEM);
                }

                assertTrue("Subsystem '" + EXPECTED_SUBSYSTEM + "' not found", match);

                didCallback = true;
            }
        };

        subsystemStore.loadSubsystems("default", callback);

        synchronized (callback) {
            callback.wait(500);
        }

        assertTrue("Callback not executed", callback.hasBeenExecuted());
    }

}
