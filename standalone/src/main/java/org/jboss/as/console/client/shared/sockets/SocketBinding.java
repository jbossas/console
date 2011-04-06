package org.jboss.as.console.client.shared.sockets;

/**
 * @author Heiko Braun
 * @date 4/6/11
 */
public interface SocketBinding {

    String getName();
    void setName(String name);

    int getPort();
    void setPort(int port);

    String getInterface();
    void setInterface(String name);

    String getMultiCastAddress();
    void setMultiCastAddress(String address);

    int getMultiCastPort();
    void setMultiCastPort(int port);
}
