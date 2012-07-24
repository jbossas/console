package org.jboss.as.console.client.tools;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.ListItem;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 7/23/12
 */
public class FXModelsView {

    private FXTemplatesPresenter presenter;
    private DefaultCellTable<FXModel> table;
    private ListDataProvider<FXModel> dataProvider;
    private FXTemplate currentTemplate  ;
    private ContentHeaderLabel headline;

    public void setPresenter(FXTemplatesPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        headline = new ContentHeaderLabel();

        table = new DefaultCellTable<FXModel>(8,
                new ProvidesKey<FXModel>() {
                    @Override
                    public Object getKey(FXModel FXModel) {
                        return FXModel.getId();
                    }
                });

        dataProvider = new ListDataProvider<FXModel>();
        dataProvider.addDataDisplay(table);

        final SingleSelectionModel<FXModel> selectionModel = new SingleSelectionModel<FXModel>();
        table.setSelectionModel(selectionModel);


        TextColumn<FXModel> descCol = new TextColumn<FXModel>() {
            @Override
            public String getValue(FXModel FXModel) {
                return FXModel.getDescription();
            }

        };
        TextColumn<FXModel> typeCol = new TextColumn<FXModel>() {
            @Override
            public String getValue(FXModel FXModel) {
                return FXModel.getType().name();
            }
        };

        table.addColumn(typeCol, "Execution Type");
        table.addColumn(descCol, "Description");

        ToolStrip toolstrip = new ToolStrip();
        ToolButton addBtn = new ToolButton("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.launchNewModelStepWizard(getCurrentTemplate());
            }
        });

        ToolButton removeBtn = new ToolButton("Remove", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                final FXModel selectedObject = selectionModel.getSelectedObject();
                presenter.onRemoveModelStep(getCurrentTemplate(), selectedObject.getId());

            }
        });

        toolstrip.addToolButtonRight(addBtn);
        toolstrip.addToolButtonRight(removeBtn);

        final SimpleForm form = new SimpleForm();
        form.setNumColumns(2);
        final TextItem id = new TextItem("id", "ID");
        final TextBoxItem desc = new TextBoxItem("description", "Description", true);
        final TextAreaItem address = new TextAreaItem("address", "Address", true);
        final ComboBoxItem type = new ComboBoxItem("execType", "ExecType")
        {
            @Override
            public boolean isRequired() {
                return true;
            }
        };

        type.setValueMap(new String[] {
                FXModel.ExecutionType.CREATE.name(),
                FXModel.ExecutionType.UPDATE.name(),
                FXModel.ExecutionType.DELETE.name()
        });

        final ListItem fieldNames = new ListItem("fieldNames", "FieldNames")
        {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        form.setFields(id, desc, type, address, fieldNames);
        //form.setEnabled(false);

        table.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                form.clearValues();
                final FXModel modelStep = selectionModel.getSelectedObject();

                if(modelStep!=null)
                {
                    form.edit(modelStep.asModelNode());
                }
            }
        });


        SimpleFormToolStrip formTools = new SimpleFormToolStrip(form, new SimpleFormToolStrip.FormCallback() {
            @Override
            public void onSave(Map<String, Object> changeset) {

                final FXModel modelStep = selectionModel.getSelectedObject();
                final ModelNode modelNode = modelStep.asModelNode();
                for(String key : changeset.keySet())
                {
                    for(String attribute : modelNode.keys())
                    {
                        if(key.equals(attribute))
                        {
                            final Object o = changeset.get(key);
                            final ModelNode node = Types.toDMR(o);
                            modelNode.get(attribute).set(node);
                            break;
                        }
                    }
                }

                getCurrentTemplate().removeModel(modelStep.getId());
                getCurrentTemplate().getModels().add(FXModel.fromModelNode(modelNode));
                presenter.onUpdateTemplate(getCurrentTemplate());
            }

            @Override
            public void onDelete(Object entity) {

            }
        }) ;

        VerticalPanel formLayout = new VerticalPanel();
        formLayout.setStyleName("fill-layout-width");
        formLayout.add(formTools.asWidget());
        formLayout.add(form.asWidget());

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setHeadlineWidget(headline)
                .setPlain(true)
                .setDescription("The actual model steps involved when working with a template.")
                .setMaster("Available Model Steps", table)
                .setDetail("Detail", formLayout);

        return layout.build();
    }

    private FXTemplate getCurrentTemplate()
    {
        return this.currentTemplate;
    }

    public void setTemplate(FXTemplate template) {
        this.currentTemplate = template;

        this.headline.setText("Models: Template '"+template.getName()+"'");
        dataProvider.getList().clear();
        dataProvider.getList().addAll(template.getModels());
        dataProvider.flush();
        table.selectDefaultEntity();
    }
}