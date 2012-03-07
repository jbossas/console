package org.jboss.as.console.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author Heiko Braun
 * @date 3/7/12
 */
public interface ConsoleResources extends ClientBundle {

    public static final ConsoleResources INSTANCE =  GWT.create(ConsoleResources.class);

    @CssResource.NotStrict
    @Source("org/jboss/as/console/public/console.css")
    public CssResource css();
}

