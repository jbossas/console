package org.jboss.as.console.client.widgets.tables;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class OptionCell extends AbstractCell<String> {

  private final SafeHtml html;
  private final ActionCell.Delegate<String> delegate;


  public OptionCell(SafeHtml message, ActionCell.Delegate<String> delegate) {
    super("click", "keydown");
    this.delegate = delegate;
    this.html = new SafeHtmlBuilder().appendHtmlConstant(
        "<div tabindex=\"-1\" class='row-tools'>").append(message).appendHtmlConstant(
        "</div>").toSafeHtml();
  }

  public OptionCell(String text, ActionCell.Delegate<String> delegate) {
    this(SafeHtmlUtils.fromString(text), delegate);
  }

  @Override
  public void onBrowserEvent(Context context, Element parent, String value,
      NativeEvent event, ValueUpdater<String> valueUpdater) {
    super.onBrowserEvent(context, parent, value, event, valueUpdater);
    if ("click".equals(event.getType())) {
      onEnterKeyDown(context, parent, value, event, valueUpdater);
    }
  }

  @Override
  public void render(Context context, String value, SafeHtmlBuilder sb) {
    sb.append(html);
  }

  @Override
  protected void onEnterKeyDown(Context context, Element parent, String value,
      NativeEvent event, ValueUpdater<String> valueUpdater) {
        delegate.execute(String.valueOf(context.getIndex()));
  }
}
