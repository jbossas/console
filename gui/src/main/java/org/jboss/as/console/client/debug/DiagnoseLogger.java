package org.jboss.as.console.client.debug;

/**
 * @author Heiko Braun
 * @date 10/25/12
 */
public class DiagnoseLogger {

    private static final String RPC = "rpc";
    private static final String RPC_SERIALIZED = "requestSerialized";
    private static final String RPC_SENT = "requestSent";
    private static final String RPC_RESPONSE = "responseReceived";
    private static final String RPC_DESERIALIZED = "responseDeserialized";

    public static native void logRpc(
            String type, String id, double millis) /*-{
       $wnd.__gwtStatsEvent({
         'moduleName' : "dmr-invocation",
         'subSystem' : "rpc",
         'evtGroup' : id,
         'millis' : millis,
         'type' : type
       });
     }-*/;

    public static native void logRpc(
            String type, String id, double millis, String method) /*-{
          $wnd.__gwtStatsEvent({
            'moduleName' : "dmr-invocation",
            'subSystem' : "rpc",
            'evtGroup' : id,
            'millis' : millis,
            'type' : type,
            'method' : method
          });
        }-*/;

    public static native void logEvent(
            String moduleName, String subSystem,
            String eventGroup, double millis, String type) /*-{
    $wnd.__gwtStatsEvent({
      'moduleName' : moduleName,
      'subSystem' : subSystem,
      'evtGroup' : eventGroup,
      'millis' : millis,
      'type' : type
    });
  }-*/;

    public static native void logError (String module, double millis, String errMsg) /*-{
       $wnd.__gwtStatsEvent({
         'moduleName' : module,
         'subSystem' : 'error',
         'evtGroup' : 'error',
         'millis' : millis,
         'type' : 'error',
         'error' : errMsg
       });
     }-*/;
}
