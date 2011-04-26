/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author Heiko Braun
 * @date 2/28/11
 */
public class ProfileHeader extends HorizontalPanel {

    HTML prefix;

    public ProfileHeader(String title) {

        prefix = new HTML("");
        prefix.setStyleName("header-content");
        HTML changeButton = new HTML("(change)");

        changeButton.setStyleName("html-link");
        changeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.alert("Changing profiles not implemented yet!");
            }
        });

        add(prefix);
        //add(changeButton);

        //changeButton.getElement().getParentElement().setAttribute("style", "vertical-align:middle");

        setProfileName(title);
    }

    private void setProfileName(String title) {
        prefix.setHTML(title + "&nbsp;&nbsp;");
    }


}
