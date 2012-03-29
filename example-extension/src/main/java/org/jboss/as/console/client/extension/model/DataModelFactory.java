package org.jboss.as.console.client.extension.model;

import com.google.gwt.autobean.shared.AutoBean;
import org.jboss.as.console.spi.BeanFactoryExtension;

/**
 * @author Heiko Braun
 * @date 3/29/12
 */
@BeanFactoryExtension
public interface DataModelFactory {
    AutoBean<DataModel> getDataModel();
}
