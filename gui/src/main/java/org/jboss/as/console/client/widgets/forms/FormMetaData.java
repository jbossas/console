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
package org.jboss.as.console.client.widgets.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.as.console.client.Console;

/**
 * Aggregator for PropertyBinding instances.  Allows searching and grouping.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class FormMetaData {
    public static final String DEFAULT_TAB = Console.CONSTANTS.common_label_attributes();
    public static final String CUSTOM_TAB = "CUSTOM";

    private Comparator<PropertyBinding> orderComparator = new Comparator<PropertyBinding>() {
        @Override
        public int compare(PropertyBinding item1, PropertyBinding item2) {
            return item1.getOrder() - item2.getOrder();
        }
    };

    private List<PropertyBinding> baseAttributes = new ArrayList<PropertyBinding>();
    private Map<String, List<PropertyBinding>> groupedAttributes = new LinkedHashMap<String, List<PropertyBinding>>();
    private Map<String, List<PropertyBinding>> tabbedAttributes = new LinkedHashMap<String, List<PropertyBinding>>();
    private boolean isFlattened = false;
    private Class<?> type;

    public FormMetaData(Class<?> type, List<PropertyBinding> propertyMetaData) {
        this.type = type;

        // Sort the input, this means that everything will be sorted appropriately
        // regardless of where it appears (including tabs)
        Collections.sort(propertyMetaData, orderComparator);

        // make sure default is first
        tabbedAttributes.put(DEFAULT_TAB, new ArrayList<PropertyBinding>());

        for (PropertyBinding binding : propertyMetaData) {
           String subgroup = binding.getSubgroup();

           if ("".equals(subgroup)) {
               baseAttributes.add(binding);
           } else {
               List<PropertyBinding> subgroupData = groupedAttributes.get(subgroup);
               if (subgroupData == null) {
                   subgroupData = new ArrayList<PropertyBinding>();
                   groupedAttributes.put(subgroup, subgroupData);
               }
               subgroupData.add(binding);
           }

            List<PropertyBinding> tabData = tabbedAttributes.get(binding.getTabName());
            if (tabData == null) {
                tabData = new ArrayList<PropertyBinding>();
                tabbedAttributes.put(binding.getTabName(), tabData);
            }

            if(!CUSTOM_TAB.equals(binding.getTabName())) // items in CUSTOM_TAB will be skipped and need to be provided manually
                tabData.add(binding);


           if (binding.getDetypedName().contains("/")) isFlattened = true;
        }

        if (tabbedAttributes.get(DEFAULT_TAB).isEmpty()) tabbedAttributes.remove(DEFAULT_TAB);

        doGroupCheck();
    }

    // make sure all grouped attributes are on the same tab
    private void doGroupCheck() {
        if (!hasTabs()) return;

        for (String groupName : getGroupNames()) {
            String tabName = getGroupedAttribtes(groupName).get(0).getTabName();
            for (PropertyBinding propBinding : getGroupedAttribtes(groupName)) {
                if (!tabName.equals(propBinding.getTabName())) {
                    throw new RuntimeException("FormItem " + propBinding.getJavaName() + " must be on the same tab with all members of its subgroup.");
                }
            }
        }
    }

    public List<PropertyBinding> getBaseAttributes() {
        return Collections.unmodifiableList(this.baseAttributes);
    }

    public void setGroupedAttributes(String groupName, List<PropertyBinding> attributes) {
        groupedAttributes.put(groupName, attributes);
    }

    public List<PropertyBinding> getGroupedAttribtes(String groupName) {
        return groupedAttributes.get(groupName);
    }

    /**
     * Returns all group names.  Calling iterator() on the returned Set
     * will give you an iterator that maintains the names in the order they were added.
     *
     * @return The group names.
     */
    public Set<String> getGroupNames() {
        return groupedAttributes.keySet();
    }

    /**
     * Returns true if the Form contains one or more attributes that are accessed as
     * sub-attributes.
     *
     * @return <code>true</code> if the structure has been flattened, <code>false</code> otherwise.
     */
    public boolean isFlattened() {
        return this.isFlattened;
    }

    public boolean hasTabs() {
        return this.tabbedAttributes.size() > 1;
    }

    public Map<String, List<PropertyBinding>> getTabbedAttributes() {
        return this.tabbedAttributes;
    }

    /**
     * Find a PropertyBinding with the given bean (Java Bean Name) property.
     * @param beanPropName The name of the bean property.
     * @return The PropertyBinding
     * @throws IllegalArgumentException if the PropertyBinding is not found.
     */
    public PropertyBinding findAttribute(String beanPropName) {
        for (PropertyBinding attrib : baseAttributes) {
            if (attrib.getJavaName().equals(beanPropName)) return attrib;
        }

        for (Map.Entry<String, List<PropertyBinding>> entry : groupedAttributes.entrySet()) {
            for (PropertyBinding attrib : entry.getValue()) {
                if (attrib.getJavaName().equals(beanPropName)) return attrib;
            }
        }

        throw new IllegalArgumentException("Unknown Attribute with beanPropName name " + beanPropName);
    }

}
