package org.jboss.as.console.client.components;

import com.smartgwt.client.widgets.tree.TreeNode;

public class NavTreeNode extends TreeNode
{
    public NavTreeNode(String name, String title) {
        super(name);
        setTitle(title);
        //setIcon("/images/blank.png");
    }

    public NavTreeNode(String name, boolean isSecondary, NavTreeNode... children) {
        super(name);
        //setIcon("/images/blank.png");

        setChildren(children);

        if(isSecondary)
        {
            setCustomStyle("lhs-secondary");
            for(NavTreeNode child : children)
                child.setCustomStyle("lhs-secondary");
        }
        else
        {
            setCustomStyle("lhs-primary-header");
        }
    }

    public NavTreeNode(String name, String title, boolean isSecondary, NavTreeNode... children) {
        this(name, title);

        setChildren(children);

        if(isSecondary)
        {
            setCustomStyle("lhs-secondary");
            for(NavTreeNode child : children)
                child.setCustomStyle("lhs-secondary");
        }
        else
        {
            setCustomStyle("lhs-primary-header");
        }
    }

    @Override
    public String getIcon() {
        return null;
    }
}
