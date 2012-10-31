package org.jboss.mbui.client.aui.aim;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class EventType {



    public static EventType Interaction = new TypeInteraction();
    public static EventType Transition = new TypeTransition();
    public static EventType System = new TypeSystem();
    public static EventType External = new TypeExternal();

    public static class TypeInteraction extends  EventType {};
    public static class TypeTransition extends  EventType {};
    public static class TypeSystem extends  EventType {};
    public static class TypeExternal extends  EventType {};





}
