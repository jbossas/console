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

package org.jboss.as.console.client.shared.properties;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.AddressableModelCmd;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 5/18/11
 */
public class LoadPropertiesCmd extends AddressableModelCmd implements AsyncCommand<List<PropertyRecord>> {


    public LoadPropertiesCmd(DispatchAsync dispatcher, BeanFactory factory, ModelNode address) {
        super(dispatcher, factory, address);
    }

    @Override
    public void execute(final AsyncCallback<List<PropertyRecord>> callback) {

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).set(address);
        operation.get(CHILD_TYPE).set("system-property");
        operation.get(RECURSIVE).set(Boolean.TRUE);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();

                List<PropertyRecord> properties = new ArrayList<PropertyRecord>();

                if(response.hasDefined(RESULT))
                {
                    List<Property> payload = response.get(RESULT).asPropertyList();


                    for(Property prop : payload)
                    {

                        String key = prop.getName();
                        ModelNode item = prop.getValue();

                        PropertyRecord propertyRecord = factory.property().as();
                        propertyRecord.setKey(key);
                        propertyRecord.setValue(item.get("value").asString());

                        if(item.hasDefined("boot-time"))
                            propertyRecord.setBootTime(item.get("boot-time").asBoolean());

                        properties.add(propertyRecord);

                    }
                }

                callback.onSuccess(properties);
            }
        });

    }
}
