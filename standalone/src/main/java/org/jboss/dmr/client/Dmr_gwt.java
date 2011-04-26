/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

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
