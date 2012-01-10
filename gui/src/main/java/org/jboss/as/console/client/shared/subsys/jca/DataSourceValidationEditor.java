package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.forms.BlankItem;
import org.jboss.as.console.client.widgets.forms.FormEditor;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 12/13/11
 */
public class DataSourceValidationEditor extends FormEditor<DataSource>{

    public DataSourceValidationEditor(FormToolStrip.FormCallback<DataSource> callback) {

        super(DataSource.class);

        ModelNode helpAddress = Baseadress.get();
        helpAddress.add("subsystem", "datasources");
        helpAddress.add("data-source", "*");

        setCallback(callback);
        setHelpAddress(helpAddress);
    }

    @Override
    public Widget asWidget() {

        TextBoxItem connectionChecker = new TextBoxItem("validConnectionChecker", "Valid Connection Checker") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        TextBoxItem connectionSql= new TextBoxItem("checkValidSql", "Check Valid Sql") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        CheckBoxItem validateOnMatch = new CheckBoxItem("validateOnMatch", "Validate On Match") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        CheckBoxItem backgroundValidation = new CheckBoxItem("backgroundValidation", "Background Validation") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };


        NumberBoxItem backgroundValidationMillis = new NumberBoxItem("backgroundValidationMillis", "Validation Millis") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        TextBoxItem staleConnectionChecker = new TextBoxItem("staleConnectionChecker", "Stale Connection Checker") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        TextBoxItem exceptionSorter= new TextBoxItem("exceptionSorter", "Exception Sorter") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        getForm().setFields(
                connectionChecker, connectionSql,
                validateOnMatch, BlankItem.INSTANCE,
                backgroundValidation, backgroundValidationMillis,
                staleConnectionChecker, exceptionSorter);

        return super.asWidget();
    }
}
