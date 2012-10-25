package org.jboss.as.console.client.debug;

/**
 * @author Heiko Braun
 * @date 10/25/12
 */
public class DiagnoseLogger {
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
}
