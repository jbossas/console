package org.jboss.mbui.gui.reification.strategy;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.mbui.model.structure.InteractionUnit;

/**
 * @author Heiko Braun
 * @date 11/13/12
 */
interface WidgetStrategy
{
    void add(InteractionUnit unit, Widget widget);

    //void add(Widget widget, String name);

    Widget as();
}
