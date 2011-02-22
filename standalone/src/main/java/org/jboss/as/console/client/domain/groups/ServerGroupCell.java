package org.jboss.as.console.client.domain.groups;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;

public class ServerGroupCell extends AbstractCell<ServerGroupRecord> {

    interface Template extends SafeHtmlTemplates {
        @Template("<div class=\"{0}\" style=\"outline:none;\" ><h3>{1}</h3>(Profile: {2})</div>")
        SafeHtml message(String cssClass, String name, String profile);
    }

    private static final Template TEMPLATE = GWT.create(Template.class);


    @Override
    public void render(
            Context context,
            ServerGroupRecord groupRecord,
            SafeHtmlBuilder safeHtmlBuilder)
    {

        safeHtmlBuilder.append(
                    TEMPLATE.message("cross-reference",
                        groupRecord.getGroupName(),
                        groupRecord.getProfileName()
                )
        );

    }

}