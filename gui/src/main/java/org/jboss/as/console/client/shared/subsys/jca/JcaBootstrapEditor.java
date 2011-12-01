package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaBootstrapContext;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
public class JcaBootstrapEditor {

    private ListDataProvider<JcaBootstrapContext> dataProvider;
    private DefaultCellTable<JcaBootstrapContext> table ;
    private ComboBoxItem workmanager;

    Widget asWidget() {

        table = new DefaultCellTable<JcaBootstrapContext>(10);
        dataProvider = new ListDataProvider<JcaBootstrapContext>();
        dataProvider.addDataDisplay(table);

        TextColumn<JcaBootstrapContext> name = new TextColumn<JcaBootstrapContext>() {
            @Override
            public String getValue(JcaBootstrapContext record) {
                return record.getName();
            }
        };

        table.addColumn(name, "Name");


        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
               // TODO
            }
        }));

        topLevelTools.addToolButtonRight(new ToolButton("Remove", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
               // TODO
            }
        }));


        Form<JcaBootstrapContext> form = new Form<JcaBootstrapContext>(JcaBootstrapContext.class);
        form.setEnabled(false);

        TextItem contextName = new TextItem("name", "Name");
        workmanager = new ComboBoxItem("workmanager", "Work Manager");

        form.setFields(contextName, workmanager);
        form.setNumColumns(2);

        form.bind(table);

        SafeHtmlBuilder description = new SafeHtmlBuilder();
        description.appendEscaped("Bootstrap context for resource adapters. Each context does reference a workmanager. ");

        FormToolStrip<JcaBootstrapContext> formTools = new FormToolStrip<JcaBootstrapContext>(
                form,
                new FormToolStrip.FormCallback<JcaBootstrapContext>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {

                    }

                    @Override
                    public void onDelete(JcaBootstrapContext entity) {

                    }
                }
        );
        formTools.providesDeleteOp(false);

        VerticalPanel formPanel = new VerticalPanel();
        formPanel.setStyleName("fill-layout-width");
        formPanel.add(formTools.asWidget());
        formPanel.add(form.asWidget());

        Widget panel = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle("Boostrap")
                .setHeadline("JCA Boostrap Contexts")
                .setDescription(description.toSafeHtml())
                .setMaster("Configured Contexts", table)
                .setTopLevelTools(topLevelTools.asWidget())
                .setDetail("Bootstrap Context", formPanel)
                .build();

        return panel;
    }

    public void setManagers(List<JcaWorkmanager> managers) {
        List<String> names = new ArrayList<String>(managers.size());

        for(JcaWorkmanager manager : managers)
            names.add(manager.getName());

        workmanager.setValueMap(names);
    }

    public void setContexts(List<JcaBootstrapContext> contexts) {
        dataProvider.setList(contexts);

        if(!contexts.isEmpty())
            table.getSelectionModel().setSelected(contexts.get(0), true);

    }
}
