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
package com.google.gwt.debugpanel.models;

/**
 * GWT implementation of the {@DebugStatisticsValue}.
 */
public class GwtDebugStatisticsValue extends DebugStatisticsValue {
  private String rpcMethod;
  private Object response;

  public GwtDebugStatisticsValue(String label, String module, double startTime, double endTime) {
    super(label, module, startTime, endTime);
  }

  public String getRpcMethod() {
    return rpcMethod;
  }

  public void setRpcMethod(String method) {
    if (method != null) {
      method = method.replace("_Proxy.", ".");
    }
    this.rpcMethod = method;
  }

  public boolean hasRpcMethod() {
    return rpcMethod != null;
  }

  public Object getResponse() {
    return response;
  }

  public void setResponse(Object response) {
    this.response = response;
  }

  public boolean hasResponse() {
    return response != null;
  }

  public GwtDebugStatisticsValue withTimes(double start, double end) {
    if (start == getStartTime() && end == getEndTime()) {
      return this;
    }
    GwtDebugStatisticsValue r = new GwtDebugStatisticsValue(
        getLabel(), getModuleName(), start, end);
    r.rpcMethod = rpcMethod;
    r.response = response;
    return r;
  }

  public GwtDebugStatisticsValue withChildTime(double millis) {
    return withTimes(Math.min(millis, getStartTime()), Math.max(millis, getEndTime()));
  }

  public GwtDebugStatisticsValue withChildTimes(double childStart, double childEnd) {
    double start = Math.min(childStart, childEnd);
    double end = Math.max(childStart, childEnd);
    return withTimes(Math.min(start, getStartTime()), Math.max(end, getEndTime()));
  }

  public GwtDebugStatisticsValue withEndTime(double millis) {
    return withTimes(getStartTime(), Math.max(getEndTime(), millis));
  }

  public GwtDebugStatisticsValue withStartTime(double millis) {
    return withTimes(Math.min(getStartTime(), millis), getEndTime());
  }
}
