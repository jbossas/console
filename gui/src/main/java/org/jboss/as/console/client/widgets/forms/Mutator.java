package org.jboss.as.console.client.widgets.forms;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 9/23/11
 */
public class Mutator<T> {

    private Map<String, Setter> setters = new HashMap<String, Setter>();

    public void add(String javaName, Setter<T> setter)
    {
        setters.put(javaName, setter);
    }

    public void register(String javaName, Setter<T> setter)
    {
        setters.put(javaName, setter);
    }

    public Setter<T> get(String javaName)
    {
        Setter setter = setters.get(javaName);
        if(null==setter)
            throw new IllegalArgumentException("No setter for field "+javaName);

        return setter;
    }

    public void mutate(T entity, String javaName, Object value)
    {
        Setter<T> setter = get(javaName);
        setter.invoke(entity, value);
    }



}
