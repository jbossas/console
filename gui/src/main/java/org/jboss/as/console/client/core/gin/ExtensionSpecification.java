package org.jboss.as.console.client.core.gin;

import com.google.gwt.inject.client.AsyncProvider;
import org.jboss.as.console.client.shared.subsys.tx.TransactionPresenter;
import org.jboss.as.console.spi.Plugin;

/**
 * @author Heiko Braun
 * @date 3/27/12
 */
@Plugin
public interface ExtensionSpecification {

    AsyncProvider<TransactionPresenter> getTransactionPresenter();

}
