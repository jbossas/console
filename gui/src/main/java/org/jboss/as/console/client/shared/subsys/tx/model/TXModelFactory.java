package org.jboss.as.console.client.shared.subsys.tx.model;

import com.google.web.bindery.autobean.shared.AutoBean;
import org.jboss.as.console.spi.BeanFactoryExtension;

/**
 * @author Heiko Braun
 * @date 3/28/12
 */
@BeanFactoryExtension
public interface TXModelFactory {
    AutoBean<TransactionManager> transactionManager();
}
