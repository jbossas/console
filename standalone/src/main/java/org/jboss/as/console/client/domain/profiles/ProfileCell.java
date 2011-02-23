package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.jboss.as.console.client.domain.model.ProfileRecord;

public class ProfileCell extends AbstractCell<ProfileRecord> {

    interface Template extends SafeHtmlTemplates {
        @Template("<div class=\"{0}\" style=\"outline:none;\" >- <b>{1}</b></div>")
        SafeHtml message(String cssClass, String name);
    }

    private static final Template TEMPLATE = GWT.create(Template.class);


    @Override
    public void render(
            Context context,
            ProfileRecord record,
            SafeHtmlBuilder safeHtmlBuilder)
    {

        safeHtmlBuilder.append(
                    TEMPLATE.message("none", record.getName())
        );

    }

}
