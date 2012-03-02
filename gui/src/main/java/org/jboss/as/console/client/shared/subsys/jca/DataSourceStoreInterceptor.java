package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.model.ResponseWrapper;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSourceStore;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;

import java.util.List;
import java.util.Map;

/**
 * Intercepts certain opertions and make sure the data source is in an editable state: i.e. disabled.
 *
 * @author Heiko Braun
 * @date 3/2/12
 */
public class DataSourceStoreInterceptor implements DataSourceStore {

    private DataSourceStore delegate;

    public DataSourceStoreInterceptor(DataSourceStore delegate) {
        this.delegate = delegate;
    }

    private void assertDisabled(final String name, boolean isXA, final Command proceedWith) {
        delegate.loadDataSource(name, isXA, new SimpleCallback<DataSource>() {
            @Override
            public void onSuccess(DataSource result) {
                if(!result.isEnabled())
                    proceedWith.execute();
                else
                    Console.error(Console.MESSAGES.subsys_jca_err_ds_enabled(name));
            }
        });
    }

    private void assertEnabled(final String name, boolean isXA, final Command proceedWith) {
        delegate.loadDataSource(name, isXA, new SimpleCallback<DataSource>() {
            @Override
            public void onSuccess(DataSource result) {
                if(result.isEnabled())
                    proceedWith.execute();
                else
                    Console.error(Console.MESSAGES.subsys_jca_err_ds_notEnabled(name));
            }
        });
    }

    @Override
    public void loadDataSource(String name, boolean isXA, AsyncCallback<DataSource> callback) {
        delegate.loadDataSource(name, isXA, callback);
    }

    @Override
    public void loadDataSources(AsyncCallback<List<DataSource>> callback) {
        delegate.loadDataSources(callback);
    }

    @Override
    public void loadXADataSources(AsyncCallback<List<XADataSource>> callback) {
        delegate.loadXADataSources(callback);
    }

    @Override
    public void createDataSource(DataSource datasource, AsyncCallback<ResponseWrapper<Boolean>> callback) {
        delegate.createDataSource(datasource, callback);
    }

    @Override
    public void deleteDataSource(DataSource dataSource, AsyncCallback<Boolean> callback) {
        delegate.deleteDataSource(dataSource, callback);
    }

    @Override
    public void enableDataSource(DataSource dataSource, boolean doEnable, AsyncCallback<ResponseWrapper<Boolean>> callback) {
        delegate.enableDataSource(dataSource, doEnable, callback);
    }

    @Override
    public void updateDataSource(final String name, final Map<String, Object> changedValues, final AsyncCallback<ResponseWrapper<Boolean>> callback) {

        assertDisabled(name, false, new Command() {
            @Override
            public void execute() {
                delegate.updateDataSource(name, changedValues, callback);
            }
        });

    }

    @Override
    public void createXADataSource(XADataSource datasource, AsyncCallback<ResponseWrapper<Boolean>> callback) {
        delegate.createXADataSource(datasource, callback);
    }

    @Override
    public void enableXADataSource(XADataSource entity, boolean doEnable, AsyncCallback<ResponseWrapper<Boolean>> callback) {
        delegate.enableXADataSource(entity, doEnable, callback);
    }

    @Override
    public void deleteXADataSource(XADataSource entity, AsyncCallback<Boolean> callback) {
        delegate.deleteXADataSource(entity, callback);
    }

    @Override
    public void updateXADataSource(final String name, final Map<String, Object> changedValues, final AsyncCallback<ResponseWrapper<Boolean>> callback) {

        assertDisabled(name, true, new Command() {
            @Override
            public void execute() {
                delegate.updateXADataSource(name, changedValues, callback);
            }
        });

    }

    @Override
    public void loadPoolConfig(boolean isXA, String name, AsyncCallback<ResponseWrapper<PoolConfig>> callback) {
        delegate.loadPoolConfig(isXA, name, callback);
    }

    @Override
    public void savePoolConfig(final boolean isXA, final String dsName, final Map<String, Object> changeset, final AsyncCallback<ResponseWrapper<Boolean>> simpleCallback) {

        assertDisabled(dsName, isXA, new Command() {
            @Override
            public void execute() {
                delegate.savePoolConfig(isXA, dsName, changeset, simpleCallback);
            }
        });


    }

    @Override
    public void deletePoolConfig(boolean isXA, String dsName, AsyncCallback<ResponseWrapper<Boolean>> callback) {
        delegate.deletePoolConfig(isXA, dsName, callback);
    }

    @Override
    public void loadXAProperties(String dataSourceName, AsyncCallback<List<PropertyRecord>> callback) {
        delegate.loadXAProperties(dataSourceName, callback);
    }

    @Override
    public void verifyConnection(final String dataSourceName, final boolean isXA, final AsyncCallback<ResponseWrapper<Boolean>> callback) {

        assertEnabled(dataSourceName, isXA, new Command() {
            @Override
            public void execute() {
                delegate.verifyConnection(dataSourceName, isXA, callback);
            }
        });

    }

    @Override
    public void loadConnectionProperties(String reference, AsyncCallback<List<PropertyRecord>> callback) {
        delegate.loadConnectionProperties(reference, callback);
    }

    @Override
    public void createConnectionProperty(final String reference, final PropertyRecord prop, final AsyncCallback<Boolean> callback) {

        assertDisabled(reference, false, new Command() {
            @Override
            public void execute() {
                delegate.createConnectionProperty(reference, prop, callback);
            }
        });

    }

    @Override
    public void deleteConnectionProperty(final String reference, final PropertyRecord prop, final AsyncCallback<Boolean> callback) {
        assertDisabled(reference, false, new Command() {
            @Override
            public void execute() {
                delegate.deleteConnectionProperty(reference, prop, callback);
            }
        });
    }

    @Override
    public void createXAConnectionProperty(final String reference, final PropertyRecord prop, final AsyncCallback<Boolean> callback) {

        assertDisabled(reference, true, new Command() {
            @Override
            public void execute() {
                delegate.createXAConnectionProperty(reference, prop, callback);
            }
        });

    }

    @Override
    public void deleteXAConnectionProperty(final String reference, final PropertyRecord prop, final AsyncCallback<Boolean> callback) {


        assertDisabled(reference, true, new Command() {
            @Override
            public void execute() {
                delegate.deleteXAConnectionProperty(reference, prop, callback);
            }
        });


    }

    @Override
    public void doFlush(boolean xa, String editedName, AsyncCallback<Boolean> callback) {
        delegate.doFlush(xa, editedName,callback);
    }
}
