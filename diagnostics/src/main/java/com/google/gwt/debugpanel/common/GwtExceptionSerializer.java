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

/**
 * GWT implementation of the {@link ExceptionSerializer}.
 */
public class GwtExceptionSerializer implements ExceptionSerializer {
  public GwtExceptionSerializer() {
  }

  //@Override
  public ExceptionData serialize(Throwable t) {
    Throwable causeEx = t.getCause();
    ExceptionData cause = (causeEx == null) ? null : serialize(causeEx);
    return ExceptionData.create(t.getClass().getName(), t.getMessage(), getStackTrace(t), cause);
  }

  private String getStackTrace(Throwable error) {
    StackTraceElement[] trace = null;
    // This fails on some type of exceptions, so we need to guard against that.
    try {
      trace = error.getStackTrace();
    } catch (Throwable t) {
      // Ignored.
    }

    if (trace == null || trace.length == 0) {
      return null;
    }
    StringBuilder result = new StringBuilder();
    append(result.append("   at "), trace[0]);
    for (int i = 1; i < trace.length; i++) {
      append(result.append("\n   at "), trace[i]);
    }
    return result.toString();
  }

  private void append(StringBuilder sb, StackTraceElement el) {
    sb.append(el.getClassName()).append(".").append(el.getMethodName());
    String file = el.getFileName();
    if (file != null) {
      sb.append("(").append(file);
      int line = el.getLineNumber();
      if (line >= 0) {
        sb.append(":").append(line);
      }
      sb.append(")");
    } else {
      sb.append("(Unknown Source)");
    }
  }
}
