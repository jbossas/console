package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.Console;
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
import org.jboss.ballroom.client.widgets.window.Feedback;

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
    private JcaPresenter presenter;

    public JcaBootstrapEditor(JcaPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        final Form<JcaBootstrapContext> form = new Form<JcaBootstrapContext>(JcaBootstrapContext.class);
        form.setEnabled(false);

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
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewContextDialogue();
            }
        }));

        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("Bootstrap Context"),
                        Console.MESSAGES.deleteConfirm("Bootstrap Context"),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if(isConfirmed)
                                    presenter.onDeleteBootstrapContext(form.getEditedEntity());
                            }
                        });

            }
        }));


        TextItem contextName = new TextItem("name", "Name");
        workmanager = new ComboBoxItem("workmanager", "Work Manager");

        form.setFields(contextName, workmanager);
        form.setNumColumns(2);

        form.bind(table);

        SafeHtmlBuilder description = new SafeHtmlBuilder();
        description.appendHtmlConstant(Console.CONSTANTS.subsys_jca_boostrap_config_desc());

        FormToolStrip<JcaBootstrapContext> formTools = new FormToolStrip<JcaBootstrapContext>(
                form,
                new FormToolStrip.FormCallback<JcaBootstrapContext>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveBootstrapContext(form.getEditedEntity(), changeset);
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
                .setTitle("Bootstrap")
                .setHeadline("JCA Bootstrap Contexts")
                .setDescription(description.toSafeHtml())
                .setMaster(Console.MESSAGES.available("Bootstrap Context"), table)
                .setTopLevelTools(topLevelTools.asWidget())
                .setDetail(Console.CONSTANTS.common_label_selection(), formPanel)
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
