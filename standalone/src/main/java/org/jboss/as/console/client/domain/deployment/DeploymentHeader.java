package org.jboss.as.console.client.domain.deployment;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.jboss.as.console.client.widgets.TabHeader;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public class DeploymentHeader extends HorizontalPanel{

    public DeploymentHeader() {

        super();

        getElement().setAttribute("style", "width:100%");

        HTML spacerLeft = new HTML("&nbsp;");
        add(spacerLeft);
        spacerLeft.getElement().getParentElement().setAttribute("style", "border-bottom:1px solid #A7ABB4;");

        add(new TabHeader("Domain Deployments"));

        HTML spacerRight= new HTML("&nbsp;");
        add(spacerRight);
        spacerRight.getElement().getParentElement().setAttribute("style", "width:100%;border-bottom:1px solid #A7ABB4;");

    }

}
