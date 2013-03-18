package org.jboss.mbui.model.behaviour;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public enum ResourceType {

    System,         // produced by system, framework or other context of use
    Statement,      // updates global dialog state
    Interaction,    // produced by interaction units, consumed by behaviours
    Presentation,   // produced by behaviours, consumed by interaction units
    Navigation      // produced by interaction units, consumed by system, framework or other context of use
}
