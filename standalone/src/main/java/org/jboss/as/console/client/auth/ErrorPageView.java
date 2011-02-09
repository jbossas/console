package org.jboss.as.console.client.auth;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * @author Heiko Braun
 * @date 2/7/11
 */
public class ErrorPageView extends ViewImpl implements ErrorPagePresenter.MyView{

    HTMLPanel panel = new HTMLPanel("<div>Error</div>");

    @Override
    public Widget asWidget() {
        return panel;
    }
}
