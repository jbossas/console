package org.jboss.as.console.client.icons;

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

    @Source("user.png")
    ImageResource user();


    @Source("icn_info_blank.png")
    ImageResource info_blank();

    @Source("icn_info_blue.png")
    ImageResource info_blue();

    @Source("icn_info_orange.png")
    ImageResource info_orange();

    @Source("icn_info_red.png")
    ImageResource info_red();
}
