package org.jboss.as.console.client.shared.deployment;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import org.jboss.as.console.client.shared.model.DeploymentRecord;

public class TitleColumn extends Column<DeploymentRecord, SafeHtml> {

    public TitleColumn() {
        super(new SafeHtmlCell());
    }

    @Override
    public SafeHtml getValue(DeploymentRecord record) {
        SafeHtmlBuilder html = new SafeHtmlBuilder();

        String title = null;
        if(record.getRuntimeName().length()>27)
            title = record.getName().substring(0,26)+"...";
        else
            title = record.getName();
        //html.appendHtmlConstant("<a href='javascript:void(0)' style='outline:none'>");
        html.appendEscaped(title);
        //html.appendHtmlConstant("</a>");

        if(record.getPath()!=null)
            html.appendHtmlConstant("<br/><span style='font-size:10px;color:#A7ABB4'>File System Deployment</span>");

        return html.toSafeHtml();
    }
}
