package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public class DisclosureGroupRenderer  implements GroupRenderer {

    @Override
    public Widget render(RenderMetaData metaData, String groupName, Map<String, FormItem> groupItems) {

        DisclosurePanel disclosurePanel = new DisclosurePanel(groupName);
        disclosurePanel.addStyleName("default-disclosure");
        DefaultGroupRenderer renderer = new DefaultGroupRenderer();

        disclosurePanel.add(
                renderer.render(metaData, groupName, groupItems)
        );

        return disclosurePanel;
    }
}
