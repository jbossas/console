package org.jboss.as.console.client.shared.subsys.jms;

import com.google.gwt.user.cellview.client.TextColumn;
import org.jboss.as.console.client.shared.subsys.jms.model.JMSEndpoint;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
class EndpointTable extends DefaultCellTable<JMSEndpoint>{

    public EndpointTable() {
        super(20);

        TextColumn<JMSEndpoint> nameColumn = new TextColumn<JMSEndpoint>() {
            @Override
            public String getValue(JMSEndpoint record) {
                return record.getName();
            }
        };

        TextColumn<JMSEndpoint> jndiNameColumn = new TextColumn<JMSEndpoint>() {
            @Override
            public String getValue(JMSEndpoint record) {
                return record.getJndiName();
            }
        };


        addColumn(nameColumn, "Name");
        addColumn(jndiNameColumn, "JNDI");

    }
}
