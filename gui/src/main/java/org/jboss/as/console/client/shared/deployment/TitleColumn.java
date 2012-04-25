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
        html.appendEscaped(record.getName());
        if(!record.isPersistent())
            html.appendHtmlConstant("<br/><span style='font-size:10px;color:#A7ABB4'>File System Deployment</span>");

        return html.toSafeHtml();
    }
}
