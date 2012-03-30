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

package org.jboss.as.console.client.shared.subsys;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TreeItem;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.plugins.SubsystemExtension;
import org.jboss.as.console.client.plugins.SubsystemRegistry;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.ballroom.client.layout.DefaultTreeItem;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.layout.LHSTreeSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 5/24/11
 */
public class SubsystemTreeBuilder {

    public static void build(final LHSTreeSection subsysTree, List<SubsystemRecord> subsystems)
    {

        SubsystemRegistry registry = Console.getSubsystemRegistry();

        Map<String, List<SubsystemExtension>> grouped = new HashMap<String, List<SubsystemExtension>>();
        List<String> groupNames = new ArrayList<String>();
        for(SubsystemExtension ext : registry.getExtensions())
        {
            if(!grouped.containsKey(ext.getGroup()))
            {
                groupNames.add(ext.getGroup());
                grouped.put(ext.getGroup(), new ArrayList<SubsystemExtension>());
            }

            grouped.get(ext.getGroup()).add(ext);
        }

        int includedSubsystems = 0;


        Collections.sort(groupNames);

        // build groups first
        for(String groupName : groupNames)
        {
            List<SubsystemExtension> items = grouped.get(groupName);

            final TreeItem groupTreeItem = new DefaultTreeItem(groupName);

            for(SubsystemExtension candidate : items)
            {
                for(SubsystemRecord actual: subsystems)
                {
                    if(actual.getKey().equals(candidate.getKey()))
                    {
                        includedSubsystems++;

                        final LHSNavTreeItem link = new LHSNavTreeItem(candidate.getName(), candidate.getToken());
                        link.setKey(candidate.getKey());

                        groupTreeItem.addItem(link);
                    }
                }
            }

            // skip empty groups
            if(groupTreeItem.getChildCount()>0)
                subsysTree.addItem(groupTreeItem);

        }

        // fallback in case no manageable subsystems exist
        if(includedSubsystems==0)
        {
            HTML explanation = new HTML("No manageable subsystems exist.");
            explanation.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    displaySubsystemHelp(subsysTree);
                }
            });
            subsysTree.addItem(new TreeItem(explanation));
        }
    }


    private static void displaySubsystemHelp(LHSTreeSection subsysTree) {
        PopupPanel help = new PopupPanel();
        help.setStyleName("help-panel-open");
        help.getElement().setAttribute("style", "padding:15px");
        help.setWidget(new HTML("Mostly likely there is no UI provided to manage a particular subsystem. " +
                "It might as well be, that the profile doesn't include any subsystems at all."));
        help.setPopupPosition(subsysTree.getAbsoluteLeft()+50, subsysTree.getAbsoluteTop()+20);
        help.setWidth("240px");
        help.setHeight("80px");
        help.setAutoHideEnabled(true);
        help.show();

    }
}
