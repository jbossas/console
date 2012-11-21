package org.jboss.as.console.client.mbui.cui.behaviour;

import org.jboss.as.console.client.mbui.aui.aim.QName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/16/12
 */
public class IntegrityException extends Exception {
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
        return "Errors on "+sb.toString();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public boolean needsToBeRaised() {
        return errorneousElements.size()>0;
    }
}
