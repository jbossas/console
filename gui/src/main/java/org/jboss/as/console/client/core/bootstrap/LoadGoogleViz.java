package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.gwt.flow.client.Control;
import org.jboss.gwt.flow.client.Function;

/**
 * @author Heiko Braun
 * @date 12/7/11
 */
public class LoadGoogleViz implements Function<BootstrapContext>
{

    @Override
    public void execute(Control<BootstrapContext> control) {


        VisualizationUtils.loadVisualizationApi(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Loaded Google Vizualization API");
                    }
                }, LineChart.PACKAGE
        );

        // viz can be loaded in background ...
        control.proceed();

    }
}
