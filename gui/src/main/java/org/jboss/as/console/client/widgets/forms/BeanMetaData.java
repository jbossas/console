package org.jboss.as.console.client.widgets.forms;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 9/23/11
 */
public class BeanMetaData {

    private Class<?> beanType;
    private AddressBinding address;
    private List<PropertyBinding> properties;
    private FormMetaData formMetaData;

    public BeanMetaData(Class<?> beanType, AddressBinding address, List<PropertyBinding> properties) {
        this.beanType = beanType;
        this.address = address;
        this.properties = properties;
        this.formMetaData = new FormMetaData(this);
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public AddressBinding getAddress() {
        return address;
    }

    public List<PropertyBinding> getProperties() {
        return properties;
    }
    
    public FormMetaData getFormMetaData() {
        return formMetaData;
    }
}
