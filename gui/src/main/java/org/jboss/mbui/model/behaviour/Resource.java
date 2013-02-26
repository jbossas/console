package org.jboss.mbui.model.behaviour;

import org.jboss.mbui.model.structure.QName;

/**
 * A resource is an entity that produced and consumed. Typically these are events,
 * domain model entities or generally speaking: data that drives an interaction.
 *
 * @author Heiko Braun
 * @date 10/31/12
 */
public class Resource<T extends Enum<T>> {

    private QName id;
    private QName source;
    private T type;

    public Resource(String namespace, String id, T type) {
        this.id = new QName(namespace, id);
        this.type = type;
    }

    public Resource(QName id, T type) {
        this.id = id;
        this.type = type;
    }

    public QName getId() {
        return id;
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
        return "Resource {" +
                "id='" + id + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;

        Resource event = (Resource) o;

        if (!id.equalsIgnoreSuffix(event.id)) return false;
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
