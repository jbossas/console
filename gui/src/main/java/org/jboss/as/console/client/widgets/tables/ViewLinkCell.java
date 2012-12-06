package org.jboss.as.console.client.widgets.tables;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author Heiko Braun
 * @date 12/6/12
 */
public class ViewLinkCell<T> extends ActionCell<T> {

    private String title;

    public ViewLinkCell(String title, Delegate<T> delegate) {
        super(title, delegate);
        this.title = title;
    }

    @Override
    public void render(Context context, T value, SafeHtmlBuilder sb) {
        SafeHtml html = new SafeHtmlBuilder()
                .appendHtmlConstant("<a href='javascript:void(0)' tabindex=\"-1\" class='viewlink-cell'>")
                .appendHtmlConstant(title)
                .appendHtmlConstant("&nbsp;")
                .appendHtmlConstant("<i class='icon-chevron-right'></i>")
                .appendHtmlConstant("</a>")
                .toSafeHtml();

        sb.append(html);
    }
}
