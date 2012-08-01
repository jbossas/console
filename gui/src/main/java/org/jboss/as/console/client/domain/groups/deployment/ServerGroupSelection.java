package org.jboss.as.console.client.domain.groups.deployment;

import org.jboss.as.console.client.domain.model.ServerGroupRecord;

/**
 * @author Heiko Braun
 * @date 8/1/12
 */
public  class ServerGroupSelection implements CheckboxColumn.Selectable {
    private ServerGroupRecord group;
    private boolean isSelected = false;

    ServerGroupSelection(ServerGroupRecord group) {
        this.group = group;
    }

    public String getName() {
        return group.getGroupName();
    }

    public String getProfileName() {
        return group.getProfileName();
    }

    @Override
    public boolean isSelected() {
        return this.isSelected;
    }

    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
