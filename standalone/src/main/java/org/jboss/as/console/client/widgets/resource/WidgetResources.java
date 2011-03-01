package org.jboss.as.console.client.widgets.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Heiko Braun
 * @date 3/1/11
 */
public interface WidgetResources extends ClientBundle {

    public static final WidgetResources INSTANCE =  GWT.create(WidgetResources.class);

    @Source("comboBoxPicker_Over.png")
        ImageResource comboPicker_over();

    @Source("comboBoxPicker.png")
    ImageResource comboPicker();

}
