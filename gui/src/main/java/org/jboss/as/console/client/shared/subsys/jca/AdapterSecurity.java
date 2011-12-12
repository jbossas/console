package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 12/12/11
 */
public class AdapterSecurity {


    private ResourceAdapterPresenter presenter;

    public AdapterSecurity(ResourceAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        return new HTML("");

    }
}
