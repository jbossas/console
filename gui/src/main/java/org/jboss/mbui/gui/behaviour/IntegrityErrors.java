package org.jboss.mbui.gui.behaviour;

import org.jboss.mbui.model.structure.QName;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/16/12
 */
public class IntegrityErrors extends Exception {
    Map<QName, String> errorneousElements = new HashMap<QName, String>();

    public void add(QName element, String message)
    {
        errorneousElements.put(element, message);
    }

    @Override
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        for(QName e : errorneousElements.keySet())
        {
            sb.append(e.toString()).append(" => ");
            sb.append(errorneousElements.get(e));
            sb.append("\n");
        }
        return "Errors on "+sb.toString();
    }

    public boolean needsToBeRaised() {
        return errorneousElements.size()>0;
    }
}
