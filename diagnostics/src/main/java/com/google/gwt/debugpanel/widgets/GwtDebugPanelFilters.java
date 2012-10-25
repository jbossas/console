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
package com.google.gwt.debugpanel.widgets;

import com.google.gwt.debugpanel.common.Utils;
import com.google.gwt.debugpanel.models.DebugPanelFilter;
import com.google.gwt.debugpanel.models.DebugStatisticsValue;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsValue;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;

/**
 * Contains the filters used by the GWT debug panel.
 */
public class GwtDebugPanelFilters {
  protected static final DateTimeFormat FORMAT = DateTimeFormat.getFormat("HH:mm:ss.SSS");

  private GwtDebugPanelFilters() {
  }

  public static DebugPanelFilter[] getFilters() {
    return new DebugPanelFilter[] {
        new TimeFilter(),
        new DurationFilter(),
        new RpcFilter()
    };
  }

  protected static Label createFormLabel(String text) {
    Label label = new Label(text);
    label.setStyleName(Utils.style() + "-filterSettingsLabel");
    return label;
  }

  protected static TextBox createTextBox(int visibleLength) {
    TextBox textbox = new TextBox();
    textbox.setVisibleLength(visibleLength);
    return textbox;
  }

  /**
   * Filters events that happen in a specific time range.
   */
  public static class TimeFilter implements DebugPanelFilter {
    private Config config;
    private Date start, end;

    public TimeFilter() {
      config = new TimeFilterConfig();
      setTime(null, null);
    }

    //@Override
    public String getMenuItemLabel() {
      return "by Time";
    }

    //@Override
    public String getSettingsTitle() {
      return "Time Filter Settings";
    }

    //@Override
    public String getDescription() {
      return "Filters events based on at what time an event is raised. Blank means unbounded.";
    }

    //@Override
    public Config getConfig() {
      return config;
    }

    //@Override
    public boolean include(DebugStatisticsValue value) {
      if (start == null && end == null) {
        return true;
      }
      double startTime = value.getStartTime(), endTime = value.getEndTime();
      return (start == null || startTime >= start.getTime()) &&
          (end == null || endTime <= end.getTime());
    }

    //@Override
    public boolean processChildren() {
      return false;
    }

    public Date getStart() {
      return start;
    }

    public Date getEnd() {
      return end;
    }

    protected void setTime(Date start, Date end) {
      this.start = start;
      this.end = end;
      ValueChangeEvent.fire(config, config);
    }

    /**
     * The {@link TimeFilter time filter's} config.
     */
    protected class TimeFilterConfig extends Config implements Config.View {
      private Grid grid;
      protected TextBox startDate, endDate;

      public TimeFilterConfig() {
        grid = new Grid(2, 4);
        grid.setWidget(0, 0, createFormLabel("Start"));
        grid.setWidget(0, 1, startDate = createTextBox(12));
        grid.setWidget(0, 2, createComment("hh:mm:ss.SSS"));
        grid.setWidget(0, 3, createNowLink(startDate));
        grid.setWidget(1, 0, createFormLabel("End"));
        grid.setWidget(1, 1, endDate = createTextBox(12));
        grid.setWidget(1, 2, createComment("hh:mm:ss.SSS"));
        grid.setWidget(1, 3, createNowLink(endDate));
        addValueChangeHandler(new ValueChangeHandler<Config>() {
          //@Override
          public void onValueChange(ValueChangeEvent<Config> event) {
            Date date = getStart();
            startDate.setText(date == null ? "" : FORMAT.format(date));
            date = getEnd();
            endDate.setText(date == null ? "" : FORMAT.format(date));
          }
        });
      }

      private Widget createComment(String string) {
        return new HTML("(<i>" + string + "</i>)");
      }

      private Widget createNowLink(final TextBox textbox) {
        return new CommandLink("Now", new Command() {
          //@Override
          public void execute() {
            textbox.setText(FORMAT.format(new Date()));
          }
        });
      }

      @Override
      public View getView() {
        return this;
      }

      //@Override
      public Widget getWidget() {
        return grid;
      }

      private Date parseDate(String s) {
        // Add the partial seconds if not specified.
        if (!s.matches("\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1,3}")) {
          s = s + ".000";
        }
        try {
          return FORMAT.parse(s);
        } catch (Exception e) {
          return null;
        }
      }

      //@Override
      public boolean onApply() {
        setTime(parseDate(startDate.getText()), parseDate(endDate.getText()));
        return getStart() != null || getEnd() != null;
      }

      //@Override
      public void onRemove() {
        setTime(null, null);
      }
    }
  }

  /**
   * Filters events that have a duration within certain bounds.
   */
  public static class DurationFilter implements DebugPanelFilter {
    private Config config;
    private int minDuration, maxDuration;

    public DurationFilter() {
      config = new DurationFilterConfig();
      setDuration(0, 0);
    }

    //@Override
    public String getMenuItemLabel() {
      return "by Duration";
    }

    //@Override
    public String getSettingsTitle() {
      return "Duration Filter Settings";
    }

    //@Override
    public String getDescription() {
      return "Filters events based on the duration the action took. Blank or 0 means unbounded.";
    }

    //@Override
    public Config getConfig() {
      return config;
    }

    //@Override
    public boolean include(DebugStatisticsValue value) {
      if (minDuration <= 0 && maxDuration <= 0) {
        return true;
      }
      double duration = value.getEndTime() - value.getStartTime();
      return (minDuration <= 0 || duration >= minDuration) &&
          (maxDuration <= 0 || duration <= maxDuration); 
      
    }

    //@Override
    public boolean processChildren() {
      return true;
    }

    public int getMinDuration() {
      return minDuration;
    }

    public int getMaxDuration() {
      return maxDuration;
    }

    public void setDuration(int minDuration, int maxDuration) {
      this.minDuration = Math.max(0, minDuration);
      this.maxDuration = Math.max(0, maxDuration);
      ValueChangeEvent.fire(config, config);
    }

    /**
     * The {@link DurationFilter duration filter's} config.
     */
    protected class DurationFilterConfig extends Config implements Config.View {
      private Grid grid;
      protected TextBox min, max;

      public DurationFilterConfig() {
        grid = new Grid(2, 2);
        grid.setWidget(0, 0, createFormLabel("Minimum"));
        grid.setWidget(0, 1, min = createTextBox(5));
        grid.setWidget(1, 0, createFormLabel("Maximum"));
        grid.setWidget(1, 1, max = createTextBox(5));
        addValueChangeHandler(new ValueChangeHandler<Config>() {
          //@Override
          public void onValueChange(ValueChangeEvent<Config> event) {
            min.setText(Integer.toString(getMinDuration()));
            max.setText(Integer.toString(getMaxDuration()));
          }
        });
      }

      @Override
      public View getView() {
        return this;
      }

      //@Override
      public Widget getWidget() {
        return grid;
      }

      private int parse(String s) {
        try {
          return Integer.parseInt(s);
        } catch (NumberFormatException e) {
          return 0;
        }
      }

      //@Override
      public boolean onApply() {
        setDuration(parse(min.getText()), parse(max.getText()));
        return getMinDuration() > 0 || getMaxDuration() > 0;
      }

      //@Override
      public void onRemove() {
        setDuration(0, 0);
      }
    }
  }

  /**
   * Filters events that belong to an RPC that matches a specific pattern.
   */
  public static class RpcFilter implements DebugPanelFilter {
    private Config config;
    private String pattern;

    public RpcFilter() {
      config = new RpcFilterConfig();
      pattern = null;
    }

    //@Override
    public String getMenuItemLabel() {
      return "by RPC Type";
    }

    //@Override
    public String getSettingsTitle() {
      return "RPC Filter Settings";
    }

    //@Override
    public String getDescription() {
      return "Filters events based on the RPC the belongs to.";
    }

    //@Override
    public Config getConfig() {
      return config;
    }

    //@Override
    public boolean include(DebugStatisticsValue value) {
      if (pattern == null || pattern.length() == 0) {
        return true;
      } else if (value instanceof GwtDebugStatisticsValue) {
        GwtDebugStatisticsValue v = (GwtDebugStatisticsValue) value;
        return v.hasRpcMethod() && v.getRpcMethod().matches(pattern);
      }
      return false;
    }

    //@Override
    public boolean processChildren() {
      return false;
    }

    public String getPattern() {
      return (pattern == null) ? "" : pattern;
    }

    public void setPattern(String pattern) {
      this.pattern = pattern;
      ValueChangeEvent.fire(config, config);
    }

    /**
     * The {@link RpcFilter RPC filter's} config.
     */
    protected class RpcFilterConfig extends Config implements Config.View {
      private HorizontalPanel panel;
      protected TextBox textbox;

      public RpcFilterConfig() {
        panel = new HorizontalPanel();
        panel.add(createFormLabel("RPC Pattern"));
        panel.add(textbox = createTextBox(20));
        addValueChangeHandler(new ValueChangeHandler<Config>() {
          //@Override
          public void onValueChange(ValueChangeEvent<Config> event) {
            textbox.setText(getPattern());
          }
        });
      }

      @Override
      public View getView() {
        return this;
      }

      //@Override
      public Widget getWidget() {
        return panel;
      }

      //@Override
      public boolean onApply() {
        setPattern(textbox.getText().trim());
        return getPattern().length() > 0;
      }

      //@Override
      public void onRemove() {
        setPattern(null);
      }
    }
  }
}
