package org.jboss.as.console.client.shared.expr;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.properties.LoadPropertiesCmd;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.dmr.client.ModelNode;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 10/4/11
 */
public class DefaultExpressionManagerImpl extends ExpressionManager {

    private DispatchAsync dispatcher;
    private LoadPropertiesCmd loadPropCmd;
    private BeanFactory factory;

    @Inject
    public DefaultExpressionManagerImpl(
            DispatchAsync dispatcher, BeanFactory factory) {
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.loadPropCmd = new LoadPropertiesCmd(dispatcher, factory, new ModelNode());
    }

    @Override
    public void resolveValue(final Expression expr, final AsyncCallback<String> callback) {

        loadPropCmd.execute(new SimpleCallback<List<PropertyRecord>>() {
            @Override
            public void onSuccess(List<PropertyRecord> result) {

                String resolved =  null;
                for(PropertyRecord prop : result)
                {

                    System.out.println(expr.getKey()+">"+prop.getValue());
                    if(prop.getKey().equals(expr.getKey()))
                    {
                        resolved = prop.getValue();
                        break;
                    }
                }

                if(null==resolved)
                    callback.onSuccess(expr.getDefaultValue()+"");
                else
                    callback.onSuccess(resolved);
            }
        });
    }
}
