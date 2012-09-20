package org.jboss.as.console.client.core.gin;

import com.google.gwt.inject.client.AsyncProvider;
import org.jboss.as.console.client.shared.subsys.tx.TransactionPresenter;
import org.jboss.as.console.spi.GinExtension;

/**
 * @author Heiko Braun
 * @date 3/27/12
 */
@GinExtension("org.jboss.as.console.App")
public interface ExampleExtension {

    AsyncProvider<TransactionPresenter> getTransactionPresenter();

}
