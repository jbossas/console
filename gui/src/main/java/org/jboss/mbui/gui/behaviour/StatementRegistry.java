package org.jboss.mbui.gui.behaviour;

import java.util.HashMap;

/**
 * A registry for dialog statements. It reflects the current dialog state.
 *
 * @author Heiko Braun
 * @date 1/22/13
 */
public class StatementRegistry extends HashMap<String,String> {

    public void dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("Statement Registry\n");
        sb.append("------------------\n");
        for(String key : keySet())
        {
            sb.append(key).append(":").append("\t\t\t").append(get(key)).append("\n");
        }
        sb.append("------------------\n");

        System.out.println(sb.toString());
    }
}
