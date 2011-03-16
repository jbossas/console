package org.jboss.dmr.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Dmr_gwt implements EntryPoint {
	
	// We need synchronous requests to do timings
	public static native String doRequest(String url, String accept)/*-{
		var req = new XMLHttpRequest();
		req.open('GET', url, false);
		req.setRequestHeader('Accept', accept);
		req.send(null);
		return req.responseText;
	}-*/;
	
	public static native JavaScriptObject parseJson(String jsonStr) /*-{
	  return eval('(' + jsonStr + ')');
	}-*/;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		long time = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			String response = doRequest("http://localhost:9990/domain-api?recursive=true", "application/json");
			parseJson(response).cast();
			
		}
		long total = System.currentTimeMillis() - time;
		
		double avg = total / 100.0;
		
		RootPanel.get("nameFieldContainer").add(new HTML("<pre>" + "total: " + total + "avg: " + avg + "</pre>"));
		
		time = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			String response = doRequest("http://localhost:9990/domain-api?recursive=true", "application/dmr-encoded");
			ModelNode.fromBase64(response);
		}
		total = System.currentTimeMillis() - time;
		
		avg = total / 100.0;
		
		RootPanel.get("nameFieldContainer").add(new HTML("<pre>" + "total: " + total + "avg: " + avg + "</pre>"));
	}	
}
