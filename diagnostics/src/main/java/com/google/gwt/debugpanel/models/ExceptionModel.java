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

import com.google.gwt.debugpanel.common.ExceptionData;

/**
 * A model of the intercepted exceptions.
 */
public interface ExceptionModel {
  public int getExceptionEventCount();
  public ExceptionEvent getExceptionEvent(int idx);
  public void removeExceptionEvent(int idx);

  public void addListener(ExceptionModelListener listener);
  public void removeListener(ExceptionModelListener listener);

  /**
   * An event signaling the occurrence of an exception in a given module at a given time.
   */
  public static class ExceptionEvent {
    public final String module;
    public final double time;
    public final ExceptionData exception;

    public ExceptionEvent(String module, double time, ExceptionData exception) {
      this.module = module;
      this.time = time;
      this.exception = exception;
    }
  }
}
