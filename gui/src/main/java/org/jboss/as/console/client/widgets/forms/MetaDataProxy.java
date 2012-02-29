package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.AsyncProxy;

/**
 * @author Heiko Braun
 * @date 2/29/12
 */

@AsyncProxy.ConcreteType(MetaDataDelegate.class)
public interface MetaDataProxy extends AsyncProxy<ApplicationMetaData>, ApplicationMetaData {
}
