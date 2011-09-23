package org.jboss.as.console.rebind.forms;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 9/23/11
 */
public class AddressDeclaration {

    List<String[]> address;

    public AddressDeclaration(List<String[]> address) {
        this.address = address;
    }

    public List<String[]> getAddress() {
        return address;
    }
}
