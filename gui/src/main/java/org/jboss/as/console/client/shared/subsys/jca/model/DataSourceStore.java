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

package org.jboss.as.console.client.shared.subsys.jca.model;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.shared.model.ResponseWrapper;
import org.jboss.as.console.client.shared.properties.PropertyRecord;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 4/19/11
 */
public interface DataSourceStore {

    void loadDataSources(AsyncCallback<List<DataSource>> callback);
    void loadDataSource(String name, boolean isXA, final AsyncCallback<DataSource> callback) ;
    void loadXADataSources(final AsyncCallback<List<XADataSource>> callback);
    void createDataSource(DataSource datasource, AsyncCallback<ResponseWrapper<Boolean>> callback);
    void deleteDataSource(DataSource dataSource, AsyncCallback<Boolean> callback);
    void enableDataSource(DataSource dataSource, boolean doEnable, AsyncCallback<ResponseWrapper<Boolean>> callback);

    void updateDataSource(String name, Map<String,Object> changedValues, AsyncCallback<ResponseWrapper<Boolean>> callback);

    void createXADataSource(XADataSource datasource, AsyncCallback<ResponseWrapper<Boolean>> callback);

    void enableXADataSource(XADataSource entity, boolean doEnable, AsyncCallback<ResponseWrapper<Boolean>> callback);
    void deleteXADataSource(XADataSource entity, AsyncCallback<Boolean> callback);
    void updateXADataSource(String name, Map<String, Object> changedValues, AsyncCallback<ResponseWrapper<Boolean>> callback);

    void loadPoolConfig(boolean isXA, String name, AsyncCallback<ResponseWrapper<PoolConfig>> callback);
    void savePoolConfig(boolean isXA, String dsName, Map<String, Object> changeset, AsyncCallback<ResponseWrapper<Boolean>> simpleCallback);
    void deletePoolConfig(boolean isXA, String dsName, AsyncCallback<ResponseWrapper<Boolean>> callback);

    void loadXAProperties(String dataSourceName, AsyncCallback<List<PropertyRecord>> callback);

    void verifyConnection(String dataSourceName, boolean isXA, AsyncCallback<ResponseWrapper<Boolean>> callback);

    void loadConnectionProperties(String reference, AsyncCallback<List<PropertyRecord>> callback);
    void createConnectionProperty(String reference, PropertyRecord prop, AsyncCallback<Boolean> callback);
    void deleteConnectionProperty(String reference, PropertyRecord prop, AsyncCallback<Boolean> callback);

    void createXAConnectionProperty(String reference, PropertyRecord prop, AsyncCallback<Boolean> callback);
    void deleteXAConnectionProperty(String reference, PropertyRecord prop, AsyncCallback<Boolean> callback);

    void doFlush(boolean xa, String editedName, AsyncCallback<Boolean> callback);
}
