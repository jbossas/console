package org.jboss.as.console.client.shared;

import com.google.gwt.resources.client.ImageResource;
import org.jboss.as.console.client.widgets.icons.Icons;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class SubsystemIconMapping {

    static Map<String, ImageResource> mapping = new HashMap<String,ImageResource>();

    static {
        mapping.put("datasources", Icons.INSTANCE.database());
        mapping.put("jms", Icons.INSTANCE.messaging());
    }

    public static ImageResource getIcon(String subsysName)
    {
        ImageResource icon = mapping.get(subsysName);
        if(null == icon)
            icon = Icons.INSTANCE.noIcon();

        return icon;
    }
}
