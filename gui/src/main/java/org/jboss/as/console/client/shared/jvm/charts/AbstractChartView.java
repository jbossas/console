package org.jboss.as.console.client.shared.jvm.charts;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 9/29/11
 */
public abstract class AbstractChartView {
    protected int width = 400;
    protected int height = 240;
    protected String title;

    public abstract Widget asWidget();
}
