package org.jboss.as.console.client.components.sgwt;

import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;

/**
 * @author Heiko Braun
 * @date 2/10/11
 */
public class HelpWindow extends Window {

    public HelpWindow(String text) {
        super();

        setTitle("Help");
        setAutoSize(true);
        setAutoCenter(true);

        HTMLFlow htmlFlow = new HTMLFlow("<pre>"+text+"</pre>");

        htmlFlow.setMargin(10);
        addItem(htmlFlow);

        centerInPage();
    }
}
