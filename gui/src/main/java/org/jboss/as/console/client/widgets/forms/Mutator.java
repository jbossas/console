package org.jboss.as.console.client.widgets.forms;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic access to bean properties.
 *
 * @author Heiko Braun
 * @date 9/23/11
 */
public class Mutator<T> {

    private Map<String, Setter> setters = new HashMap<String, Setter>();
    private Map<String, Getter> getters = new HashMap<String, Getter>();

    public void register(String javaName, Getter<T> getter)
    {
        getters.put(javaName, getter);
    }

    public void register(String javaName, Setter<T> setter)
    {
        setters.put(javaName, setter);
    }

    public Setter<T> setter(String javaName)
    {
        Setter setter = setters.get(javaName);
        if(null==setter)
            throw new IllegalArgumentException("No setter for field "+javaName);

        return setter;
    }

    public Getter<T> getter(String javaName)
    {
        Getter getter = getters.get(javaName);
        if(null==getter)
            throw new IllegalArgumentException("No getter for field "+javaName);

        return getter;
    }

    public void setValue(T entity, String javaName, Object value)
    {
        setter(javaName).invoke(entity, value);
    }

    public Object getValue(T entity, String javaName)
    {
        return getter(javaName).invoke(entity);
    }

}
