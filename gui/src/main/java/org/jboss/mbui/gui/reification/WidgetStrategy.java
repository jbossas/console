package org.jboss.mbui.gui.reification;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 11/13/12
 */
interface WidgetStrategy
{
    void add(Widget widget);

    //void add(Widget widget, String name);

    Widget as();
}
