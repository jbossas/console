package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import org.jboss.as.console.client.shared.dispatch.AsyncCommand;

/**
 * @author Heiko Braun
 * @date 12/7/11
 */
public class LoadGoogleViz implements AsyncCommand<Boolean> {
    @Override
    public void execute(AsyncCallback<Boolean> callback) {
        VisualizationUtils.loadVisualizationApi(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Loaded Google Vizualization API");
                    }
                }, LineChart.PACKAGE
        );

        callback.onSuccess(Boolean.TRUE);
    }
}
