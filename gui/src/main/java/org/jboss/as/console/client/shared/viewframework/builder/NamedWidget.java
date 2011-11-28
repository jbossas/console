package org.jboss.as.console.client.shared.viewframework.builder;

import com.google.gwt.user.client.ui.Widget;

class NamedWidget {
    String title;
    Widget widget;

    NamedWidget(String title, Widget widget) {
        this.title = title;
        this.widget = widget;
    }
}
