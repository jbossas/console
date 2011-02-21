package org.jboss.as.console.client.components.img;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Icons extends ClientBundle {

    public static final Icons INSTANCE =  GWT.create(Icons.class);

    @Source("opener_opened.png")
    ImageResource opener_opened();

    @Source("opener_closed.png")
    ImageResource opener_closed();

    @Source("inventory.png")
    ImageResource inventory();

    @Source("add.png")
    ImageResource add();
}
