package org.jboss.as.console.client.widgets.tables;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class ButtonCell<T> extends ActionCell<T> {

    private String title;

    public ButtonCell(String title, Delegate<T> delegate) {
        super(title, delegate);
        this.title = title;
    }

    @Override
    public void render(Context context, T value, SafeHtmlBuilder sb) {
        SafeHtml html = new SafeHtmlBuilder().appendHtmlConstant("<button class='celltable-button' type=\"button\" tabindex=\"-1\">")
                .appendEscaped(title)
                .appendHtmlConstant("</button>")
                .toSafeHtml();

        sb.append(html);
    }

}

