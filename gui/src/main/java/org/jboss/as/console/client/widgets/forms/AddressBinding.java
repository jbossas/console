package org.jboss.as.console.client.widgets.forms;

import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.ADDRESS;

/**
 * @author Heiko Braun
 * @date 9/23/11
 */
public class AddressBinding {

    private ModelNode address = new ModelNode();

    public AddressBinding() {
    }

    public void add(String parent, String child)
    {
        address.get(ADDRESS).add(parent, child);
    }

    public int getNumWildCards() {

        int counter = 0;
        List<Property> tokens = address.get(ADDRESS).asPropertyList();
        for(Property tok : tokens)
        {
            if(tok.getValue().asString().equals("*"))
                counter++;
        }
        return counter;

    }

}
