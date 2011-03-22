package org.jboss.as.console.client.debug;

/**
 * @author Heiko Braun
 * @date 3/22/11
 */
public final class SimpleMetric {
    String key;
    Integer value;

    SimpleMetric(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
