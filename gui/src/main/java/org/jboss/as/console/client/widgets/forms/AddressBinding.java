package org.jboss.as.console.client.widgets.forms;

import org.jboss.dmr.client.ModelNode;

import java.util.LinkedList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 9/23/11
 */
public class AddressBinding {

    private List<String[]> address = new LinkedList<String[]>();

    public AddressBinding() {
    }

    public void add(String parent, String child)
    {
        address.add(new String[]{parent, child});
    }

    public int getNumWildCards() {

        int counter = 0;

        for(String[] tuple : address)
        {
            if(tuple[0].startsWith("{"))
                counter++;

            if(tuple[1].startsWith("{"))
                counter++;
        }
        return counter;
    }

    public ModelNode asProtoType(String... args) {

        assert getNumWildCards() ==args.length :
                "Address arguments don't match number of wildcards: "+args.length+","+getNumWildCards();

        ModelNode model = new ModelNode();

        int argsCounter = 0;
        for(String[] tuple : address)
        {
            String parent = tuple[0];
            String child = tuple[1];

            if(parent.startsWith("{"))
            {
                parent = args[argsCounter];
                argsCounter++;
            }

            if(child.startsWith("{"))
            {
                child = args[argsCounter];
                argsCounter++;
            }

            model.get(ADDRESS).add(parent, child);
        }
        return model;
    }

}
