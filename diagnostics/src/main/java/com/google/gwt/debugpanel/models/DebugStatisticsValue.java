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
 * The value of a statistics key-value pair.
 */
public abstract class DebugStatisticsValue {
  private String label;
  private String moduleName;
  private double startTime;
  private double endTime;

  public DebugStatisticsValue(String label, String moduleName, double startTime, double endTime) {
    this.label = label;
    this.moduleName = moduleName;
    this.startTime = Math.min(startTime, endTime);
    this.endTime = Math.max(startTime, endTime);
  }

  public String getLabel() {
    return label;
  }

  public String getModuleName() {
    return moduleName;
  }

  public double getStartTime() {
    return startTime;
  }

  public double getEndTime() {
    return endTime;
  }
}
