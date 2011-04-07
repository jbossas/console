/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

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