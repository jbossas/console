/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.google.gwt.debugpanel.common;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Cross-module data about an exception. You can safely "transport" an error
 * via a {@link StatisticsEvent event's} extra parameters using this class.
 */
public class ExceptionData extends JavaScriptObject {
  protected ExceptionData() {
  }

  public final native String getType() /*-{
    return this.type;
  }-*/;

  public final native String getMessage() /*-{
    return this.message;
  }-*/;

  public final native String getTrace() /*-{
    return this.trace;
  }-*/;

  public final native ExceptionData getCause() /*-{
    return this.cause;
  }-*/;

  public static final native ExceptionData create(
      String type, String message, String trace, ExceptionData cause) /*-{
    return {
      type: type,
      message: message,
      trace: trace,
      cause: cause,
      toString: function() {
        return @com.google.gwt.debugpanel.common.ExceptionData::asString(Lcom/google/gwt/debugpanel/common/ExceptionData;)
            (this);
      }
    }
  }-*/;

  public static final String asString(ExceptionData ex) {
    StringBuilder result = new StringBuilder();
    result.append(ex.getType() == null ? "<unknown type>" : ex.getType());
    if (ex.getMessage() != null) {
      result.append(": ").append(ex.getMessage());
    }
    if (ex.getTrace() != null) {
      result.append("\n").append(ex.getTrace());
    }
    if (ex.getCause() != null) {
      result.append("\nCaused by: ").append(asString(ex.getCause()));
    }
    return result.toString();
  }
}
