package org.jboss.as.console.client.shared.subsys.jgroups;

import java.util.HashMap;
import java.util.Map;

/**
 * The set of valid JGroups protocol types.
 *
 * @author Richard Achmatowicz (c) 2012 Red Hat Inc.
 */
public enum Protocol {
    UNKNOWN(null),
    UDP("UDP"),
    TCP("TCP"),
    TCP_GOSSIP("TCP_GOSSIP"),
    AUTH("AUTH"),
    PING("PING"),
    MPING("MPING"),
    MERGE2("MERGE2"),
    FD_SOCK("FD_SOCK"),
    FD("FD"),
    VERIFY_SUSPECT("VERIFY_SUSPECT"),
    BARRIER("BARRIER"),
    NAKACK("pbcast.NAKACK"),
    UNICAST2("UNICAST2"),
    STABLE("pbcast.STABLE"),
    GMS("pbcast.GMS"),
    UFC("UFC"),
    MFC("MFC"),
    FRAG2("FRAG2"),
    STATE_TRANSFER("pbcast.STATE_TRANSFER"),
    FLUSH("pbcast.FLUSH"),
    ;

    private final String name;

    Protocol(final String name) {
        this.name = name;
    }

    /**
     * Get the local name of this protocol.
     *
     * @return the local name
     */
    public String getLocalName() {
        return name;
    }

    private static final Map<String, Protocol> elements;

    static {
        final Map<String, Protocol> map = new HashMap<String, Protocol>();
        for (Protocol element : values()) {
            final String name = element.getLocalName();
            if (name != null) map.put(name, element);
        }
        elements = map;
    }


    public static Protocol forName(String localName) {
        final Protocol element = elements.get(localName);
        return element == null ? UNKNOWN : element;
    }
}

