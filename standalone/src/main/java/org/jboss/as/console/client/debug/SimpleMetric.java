package org.jboss.as.console.client.debug;

/**
 * @author Heiko Braun
 * @date 3/22/11
 */
public final class SimpleMetric implements Comparable{
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

    @Override
    public int compareTo(Object o) {

        if((o instanceof SimpleMetric) == false)
            throw new RuntimeException("Comparing apples and pies!");

        SimpleMetric other = (SimpleMetric)o;

        if(this.value>other.value)
            return -1;
        else if(this.value==other.value)
            return 0;
        else
            return 1;
    }
}
