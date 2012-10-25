package org.jboss.as.console.client.debug;

import com.google.gwt.debugpanel.common.ExceptionData;

/**
 * Integration with the lightweight metrics systems.
 *
 * @author Heiko Braun
 * @date 10/25/12
 */
public class Diagnostics {

    public static void logError(String module, double millis, String errMsg)
    {
        if(isEnabled())
            _logError(module, millis, ExceptionData.create("Error", errMsg, null, null));
    }

    public static void logRpc(
                String type, String id, double millis)
    {
        if(isEnabled())
            _logRpc(type, id, millis);
    }

    public static void logRpc(
            String type, String id, double millis, String method)
    {
        if(isEnabled())
            _logRpc(type, id, millis, method);
    }

    private static void logEvent(
               String moduleName, String subSystem,
               String eventGroup, double millis, String type)
    {
        if(isEnabled())
            _logEvent(moduleName, subSystem, eventGroup, millis, type);
    }

    public static native boolean isEnabled () /*-{
           return (typeof $wnd.__gwtStatsEvent == 'function');
         }-*/;

    private static native void _logRpc(
            String type, String id, double millis) /*-{
       $wnd.__gwtStatsEvent({
         'moduleName' : "dmr-invocation",
         'subSystem' : "rpc",
         'evtGroup' : id,
         'millis' : millis,
         'type' : type
       });
     }-*/;

    private static native void _logRpc(
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

    private static native void _logEvent(
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



    private static native void _logError (String module, double millis, ExceptionData errMsg) /*-{
       $wnd.__gwtStatsEvent({
         'moduleName' : module,
         'subSystem' : "error",
         'evtGroup' : "error",
         'millis' : millis,
         'type' : "error",
         'error' : errMsg
       });
     }-*/;
}
