package org.jboss.as.console.client.mbui.aui.aim;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class Trigger<T extends Enum<T>> {

    private QName id;
    private QName source;
    private T type;

    public Trigger(String namespace, String id, T type) {
        this.id = new QName(namespace, id);
        this.type = type;
    }

    public Trigger(QName id, T type) {
        this.id = id;
        this.type = type;
    }

    public QName getSource() {
        return source;
    }

    public void setSource(QName source) {
        this.source = source;
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
        if (!(o instanceof Trigger)) return false;

        Trigger event = (Trigger) o;

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
