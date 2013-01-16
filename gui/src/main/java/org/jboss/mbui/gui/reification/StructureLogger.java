package org.jboss.mbui.gui.reification;

import org.jboss.mbui.model.structure.InteractionUnit;

/**
 * @author Heiko Braun
 * @date 11/15/12
 */
public class StructureLogger {

    private int tabCount;
    private StringBuffer log;

    public StructureLogger() {
        reset();
    }

    public void reset() {
        log = new StringBuffer();
        tabCount = 0;
    }

    public void start(InteractionUnit parentUnit) {
        tabCount++;
        for(int i=0; i<tabCount;i++)
            log.append("\t");
        log.append("<").append(parentUnit.getName()).append(">");
        log.append("\n");
    }

    public void end(InteractionUnit parentUnit) {
        for(int i=0; i<tabCount;i++)
            log.append("\t");
        log.append("</").append(parentUnit.getName()).append(">");
        log.append("\n");
        tabCount--;
    }

    public String flush()
    {
        String msg = log.toString();
        reset();
        return msg;
    }
}
