package org.jboss.as.console.client.system;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;
import org.jboss.as.console.client.components.TitleBar;

/**
 * @author Heiko Braun
 * @date 2/4/11
 */
public class SystemApplicationViewImpl extends ViewImpl
    implements SystemApplicationPresenter.SystemAppView{

    @Override
    public Widget asWidget() {
        return new SystemAppCanvas();
    }

    // dummy implementation
    class SystemAppCanvas extends VLayout
    {
        @Override
        protected void onInit() {
            super.onInit();

            setWidth100();
            setHeight100();

            TitleBar titleBar = new TitleBar("System Overview");
            addMember(titleBar);

            Label label = new Label("Quick glance at the system status. I.e. number of active service instances, etc.");
            label.setPadding(10);
            addMember(label);

        }
    }
}
