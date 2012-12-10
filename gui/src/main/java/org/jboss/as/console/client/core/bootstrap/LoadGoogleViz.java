package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;

import java.util.Iterator;

/**
 * @author Heiko Braun
 * @date 12/7/11
 */
public class LoadGoogleViz extends BoostrapStep {

    @Override
    public void execute(Iterator<BoostrapStep> iterator, AsyncCallback<Boolean> outcome) {

        VisualizationUtils.loadVisualizationApi(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Loaded Google Vizualization API");
                    }
                }, LineChart.PACKAGE
        );

        outcome.onSuccess(Boolean.TRUE);

        next(iterator, outcome);
    }
}
