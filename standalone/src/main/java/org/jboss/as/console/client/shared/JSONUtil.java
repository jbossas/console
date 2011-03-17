package org.jboss.as.console.client.shared;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Heiko Braun
 * @date 3/16/11
 */
public class JSONUtil {

     public static native JavaScriptObject parseJson(String jsonStr) /*-{
	  return eval('(' + jsonStr + ')');
	}-*/;

    public static native String pretty(JavaScriptObject obj, String indent)/*-{

        var result = "";
        if (indent == null) indent = "";

        for (var property in obj)
        {
            var value = obj[property];
            if (typeof value == 'string')
                value = "'" + value + "'";
            else if (typeof value == 'object')
            {
                if (value instanceof Array)
                {
                    // Just let JS convert the Array to a string!
                    value = "[ " + value + " ]";
                }
                else
                {
                    // Recursive dump
                    // (replace "  " by "\t" or something else if you prefer)
                    var od = @org.jboss.as.console.client.shared.JSONUtil::pretty(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(value, indent + "\t");
                    // If you like { on the same line as the key
                    //value = "{\n" + od + "\n" + indent + "}";
                    // If you prefer { and } to be aligned
                    value = "\n" + indent + "{\n" + od + "\n" + indent + "}";
                }
            }
            result += indent + "'" + property + "' : " + value + ",\n";
        }
        return result.replace(/,\n$/, "");

    }-*/;
}
