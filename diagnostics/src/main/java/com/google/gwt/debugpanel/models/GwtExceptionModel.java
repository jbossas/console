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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.debugpanel.common.ExceptionData;
import com.google.gwt.debugpanel.common.StatisticsEvent;
import com.google.gwt.debugpanel.common.StatisticsEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Gwt implementation of the {@link ExceptionModel}.
 */
public class GwtExceptionModel implements ExceptionModel, StatisticsEventListener {
  private Listeners listeners;
  private List<ExceptionEvent> exceptions;

  public GwtExceptionModel() {
    listeners = new Listeners();
    exceptions = new ArrayList<ExceptionEvent>();
  }

  //@Override
  public int getExceptionEventCount() {
    return exceptions.size();
  }

  //@Override
  public ExceptionEvent getExceptionEvent(int idx) {
    return exceptions.get(idx);
  }

  //@Override
  public void removeExceptionEvent(int idx) {
    exceptions.remove(idx);
    listeners.exceptionRemoved(idx);
  }

  //@Override
  public void addListener(ExceptionModelListener listener) {
    listeners.add(listener);
  }

  //@Override
  public void removeListener(ExceptionModelListener listener) {
    listeners.remove(listener);
  }

  //@Override
  public void onStatisticsEvent(StatisticsEvent event) {
    if ("error".equals(event.getSubSystem()) && "error".equals(event.getEventGroupKey()) &&
        "error".equals(event.getExtraParameter("type"))) {
      Object error = event.getExtraParameter("error");
      if (error instanceof JavaScriptObject) {
        add(new ExceptionEvent(event.getModuleName(), event.getMillis(), (ExceptionData) error));
      }
    }
  }

  public void add(ExceptionEvent event) {
    int idx = exceptions.size();
    exceptions.add(event);
    listeners.exceptionAdded(idx, event);
  }

  private static class Listeners 
      extends ArrayList<ExceptionModelListener> implements ExceptionModelListener {
    public Listeners() {
    }

    //@Override
    public void exceptionAdded(int idx, ExceptionEvent ev) {
      for (ExceptionModelListener l : this) {
        l.exceptionAdded(idx, ev);
      }
    }

    //@Override
    public void exceptionRemoved(int idx) {
      for (ExceptionModelListener l : this) {
        l.exceptionRemoved(idx);
      }
    }
  }
}
