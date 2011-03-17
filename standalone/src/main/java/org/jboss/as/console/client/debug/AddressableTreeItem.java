package org.jboss.as.console.client.debug;

import com.google.gwt.user.client.ui.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/16/11
 */
public class AddressableTreeItem extends TreeItem
{
    List<String> address = new ArrayList<String>();
    String title;

    AddressableTreeItem(String title, String... addresses) {
        super(title);
        this.title = title;
        for(String a : addresses)
            address.add(a);
    }

    public List<String> getAddress() {
        return address;
    }

    public boolean isTuple() {
        return address.size() % 2 == 0;
    }

    public String addressString() {
        StringBuilder sb = new StringBuilder();
        for(String s: address)
            sb.append("/").append(s);
        return sb.toString();
    }

}