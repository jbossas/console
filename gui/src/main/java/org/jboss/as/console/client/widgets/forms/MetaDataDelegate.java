package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.core.client.GWT;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/29/12
 */
public class MetaDataDelegate implements ApplicationMetaData {

    ApplicationMetaData delegate = GWT.create(ApplicationMetaData.class);

    @Override
    public List<PropertyBinding> getBindingsForType(Class<?> type) {
        return delegate.getBindingsForType(type);
    }

    @Override
    public BeanMetaData getBeanMetaData(Class<?> type) {
        return delegate.getBeanMetaData(type);
    }

    @Override
    public Mutator getMutator(Class<?> type) {
        return delegate.getMutator(type);
    }

    @Override
    public <T> EntityFactory<T> getFactory(Class<T> type) {
        return delegate.getFactory(type);
    }

    @Override
    public FormMetaData getFormMetaData(Class<?> type) {
        return delegate.getFormMetaData(type);
    }
}
