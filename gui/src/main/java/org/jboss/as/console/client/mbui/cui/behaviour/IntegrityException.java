package org.jboss.as.console.client.mbui.cui.behaviour;

import org.jboss.as.console.client.mbui.aui.aim.QName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/16/12
 */
public class IntegrityException extends Exception {
    List<QName> errorneousElements = new ArrayList<QName>();

    public void add(QName element)
    {
        errorneousElements.add(element);
    }

    @Override
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        for(QName e : errorneousElements)
            sb.append(e.toString()).append("\n");
        return "Errors on "+sb.toString();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public boolean needsToBeRaised() {
        return errorneousElements.size()>0;
    }
}
