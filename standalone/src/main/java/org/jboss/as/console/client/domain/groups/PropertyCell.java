package org.jboss.as.console.client.domain.groups;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;


public class PropertyCell extends AbstractCell<PropertyRecord> {

    interface Template extends SafeHtmlTemplates {
        @Template("<div class=\"{0}\"><b>{1}</b> {2}</div>")
        SafeHtml message(String cssClass, String from, String message);
    }

    private static final Template TEMPLATE = GWT.create(Template.class);

    @Override
    public void render(Context context, PropertyRecord value, SafeHtmlBuilder sb) {
        sb.append(TEMPLATE.message(
                "cell-record",
                value.getKey(),
                value.getValue()
        )
        );
    }
}
