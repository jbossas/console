package org.jboss.as.console.client.server.path;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.widgets.layout.VLayout;
import org.jboss.as.console.client.components.TitleBar;

/**
 * @author Heiko Braun
 * @date 2/8/11
 */
public class PathToolViewImpl extends ViewImpl implements PathToolPresenter.MyView {

    PathToolPresenter presenter;

    @Override
    public void setPresenter(PathToolPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();

        TitleBar titleBar = new TitleBar("Path");
        layout.addMember(titleBar);

        return layout;
    }
}
