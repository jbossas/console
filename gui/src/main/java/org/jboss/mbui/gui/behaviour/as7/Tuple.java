package org.jboss.mbui.gui.behaviour.as7;

/**
 * @author Heiko Braun
 * @date 2/26/13
 */
public final class Tuple {
    final String key;
    final String value;

    public Tuple(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
