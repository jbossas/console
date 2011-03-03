package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public class DisclosureGroupRenderer  implements GroupRenderer {

    int numColumns = 1;

    @Override
    public Widget render(String groupName, Map<String, FormItem> groupItems) {

        DisclosurePanel disclosurePanel = new DisclosurePanel(groupName);
        disclosurePanel.addStyleName("default-disclosure");
        disclosurePanel.add(
                new DefaultGroupRenderer(numColumns).render(groupName, groupItems)
        );

        return disclosurePanel;
    }

    @Override
    public void setNumColumns(int cols) {
        this.numColumns = cols;
    }
}
