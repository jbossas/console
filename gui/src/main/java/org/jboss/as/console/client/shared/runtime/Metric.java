package org.jboss.as.console.client.shared.runtime;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/3/11
 */
public class Metric {

    private List<String> values = new LinkedList<String>();

    public Metric(String... values) {
        for(String s : values)
            add(s);
    }

    public Metric(int... values) {
        for(int i : values)
            add(String.valueOf(i));
    }

    public Metric(long... values) {
        for(long l : values)
            add(String.valueOf(l));
    }

    public void add(String value)
    {
        values.add(value);
    }

    public String get(int i)
    {
        return values.get(i);
    }

    public List<String> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "Metric{" +
                "values=" + values +
                '}';
    }
}
