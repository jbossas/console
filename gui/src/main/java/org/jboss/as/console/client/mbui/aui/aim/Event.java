package org.jboss.as.console.client.mbui.aui.aim;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class Event<T extends Enum<T>> {

    private String id;
    private T type;

    public Event(String id, T type) {
        this.id = id;
        this.type = type;
    }

    public T getType()
    {
        return type;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", type=" + type +
                '}';
    }
}
