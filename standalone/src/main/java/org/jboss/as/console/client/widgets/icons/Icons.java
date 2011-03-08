package org.jboss.as.console.client.widgets.icons;

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

    @Source("inventory_small.png")
    ImageResource inventory_small();

    @Source("add.png")
    ImageResource add();

    @Source("add_small.png")
    ImageResource add_small();

    @Source("remove.png")
    ImageResource remove();

    @Source("remove_small.png")
    ImageResource remove_small();

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

    @Source("close.png")
    ImageResource close();

    @Source("profile.png")
    ImageResource profile();

    @Source("server_group.png")
    ImageResource serverGroup();

    @Source("server.png")
    ImageResource server();

    @Source("deployment.png")
    ImageResource deployment();

    @Source("status_red_small.png")
    ImageResource statusRed_small();

    @Source("status_green_small.png")
    ImageResource statusGreen_small();

    @Source("status_yellow_small.png")
    ImageResource statusYellow_small();

    @Source("status_blue_small.png")
    ImageResource statusBlue_small();

}
