package org.jboss.as.console.client.mbui.aui.aim;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class Event<T extends Enum<T>> {

    private QName id;
    private T type;

    public Event(String namespace, String id, T type) {
        this.id = new QName(namespace, id);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;

        Event event = (Event) o;

        if (!id.equals(event.id)) return false;
        if (!type.equals(event.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
