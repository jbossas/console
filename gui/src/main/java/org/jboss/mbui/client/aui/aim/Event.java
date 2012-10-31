package org.jboss.mbui.client.aui.aim;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class Event<T extends EventType> {

    private String id;
    private T type;

    public Event(String id) {
        this.id = id;
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
