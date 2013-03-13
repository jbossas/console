package org.jboss.dmr.client.dispatch.impl;

import com.google.gwt.debugpanel.common.ExceptionData;
import org.jboss.dmr.client.dispatch.Diagnostics;

/**
 * Integration with the lightweight metrics systems.
 *
 * @author Heiko Braun
 * @date 10/25/12
 */
public class LMSDiagnostics implements Diagnostics {


    public void logError(String module, double millis, String errMsg)
    {
        if(isEnabled())
            _logError(module, millis, ExceptionData.create("Error", errMsg, null, null));
    }

    public void logRpc(
            String type, String id, double millis)
    {
        if(isEnabled())
            _logRpc(type, id, millis);
    }

    public void logRpc(
            String type, String id, double millis, String method)
    {
        if(isEnabled())
            _logRpc(type, id, millis, method);
    }

    public void logEvent(
            String moduleName, String subSystem,
            String eventGroup, double millis, String type)
    {
        if(isEnabled())
            _logEvent(moduleName, subSystem, eventGroup, millis, type);
    }

    public boolean isEnabled () {
        return _isEnabled();
    }

    public static native boolean _isEnabled () /*-{
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
