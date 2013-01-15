package org.jboss.as.console.client.shared.general;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.general.model.Path;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.layout.FormLayout;
import org.jboss.as.console.client.layout.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 10/15/12
 */
public class PathManagementView extends SuspendableViewImpl implements PathManagementPresenter.MyView {

    private PathManagementPresenter presenter;
    private Form<Path> form;
    private DefaultCellTable<Path> table;
    private ListDataProvider<Path> dataProvider;

    @Override
    public Widget createWidget() {
        ToolStrip toolstrip = new ToolStrip();

        ToolButton addBtn = new ToolButton("Add", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewPathDialogue();
            }
        });

        toolstrip.addToolButtonRight(addBtn);

        ToolButton removeBtn = new ToolButton("Remove", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final Path editedEntity = form.getEditedEntity();
                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("Path"),
                        Console.MESSAGES.deleteConfirm("Path " + editedEntity.getName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed)
                                    presenter.onDeletePath(editedEntity);
                            }
                        });
            }
        });

        toolstrip.addToolButtonRight(removeBtn);

        // -----------

        table = new DefaultCellTable<Path>(6, new ProvidesKey<Path>() {
            @Override
            public Object getKey(Path path) {
                return path.getName();
            }
        });

        dataProvider = new ListDataProvider<Path>();
        dataProvider.addDataDisplay(table);

        TextColumn<Path> nameCol = new TextColumn<Path>() {
            @Override
            public String getValue(Path record) {
                return record.getName();
            }
        };


        table.addColumn(nameCol, "Name");

        // -----------

        form = new Form<Path>(Path.class);
        form.setNumColumns(2);

        FormToolStrip<Path> detailToolStrip = new FormToolStrip<Path>(
                form,
                new FormToolStrip.FormCallback<Path>()
                {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        Path updatedEntity = form.getUpdatedEntity();
                        presenter.onSavePath(
                                updatedEntity.getName(),
                                form.getChangedValues()
                        );
                    }

                    @Override
                    public void onDelete(Path entity) {

                    }
                }
        );

        detailToolStrip.providesDeleteOp(false);

        // ---


        TextItem nameItem = new TextItem("name", "Name");
        TextAreaItem path = new TextAreaItem("path", "Path");
        TextBoxItem relativeTo = new TextBoxItem("relativeTo", "Relative To", false);

        form.setFields(nameItem, path, relativeTo);
        form.bind(table);
        form.setEnabled(false);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = new ModelNode();
                        address.add("path", "*");
                        return address;
                    }
                }, form
        );


        FormLayout formLayout = new FormLayout()
                .setTools(detailToolStrip)
                .setHelp(helpPanel)
                .setForm(form);


        // ------------------------------------------


        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setTitle("Paths")
                .setHeadline("Path References")
                .setDescription("A named filesystem path, but without a requirement to specify the actual path. If no actual path is specified, acts as a placeholder in the model (e.g. at the domain level) until a fully specified path definition is applied at a lower level (e.g. at the host level, where available addresses are known.)")
                .setMaster(Console.MESSAGES.available("Paths"), table)
                .setMasterTools(toolstrip)
                .addDetail(Console.CONSTANTS.common_label_attributes(), formLayout.build());


        return layout.build();
    }

    @Override
    public void setPresenter(PathManagementPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setPaths(List<Path> paths) {
        List<Path> list = dataProvider.getList();
        list.clear(); // cannot call setList() as that breaks the sort handler
        list.addAll(paths);
        dataProvider.flush();

        table.selectDefaultEntity();
    }
}
