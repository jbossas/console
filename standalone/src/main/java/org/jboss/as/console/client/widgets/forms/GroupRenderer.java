package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.ui.Widget;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public interface GroupRenderer {
    Widget render(RenderMetaData metaData, String groupName, Map<String, FormItem> groupItems);

}
