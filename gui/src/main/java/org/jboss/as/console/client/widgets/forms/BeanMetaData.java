package org.jboss.as.console.client.widgets.forms;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 9/23/11
 */
public class BeanMetaData {

    Class<?> beanType;
    AddressBinding address;
    List<PropertyBinding> properties;

    public BeanMetaData(Class<?> beanType, AddressBinding address, List<PropertyBinding> properties) {
        this.beanType = beanType;
        this.address = address;
        this.properties = properties;
    }
}
