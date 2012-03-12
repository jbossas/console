package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.ConnectionDefinition;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.as.console.client.widgets.tables.TextLinkCell;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 12/12/11
 */
public class AdapterList implements PropertyManagement {

    private ResourceAdapterPresenter presenter;
    private DefaultCellTable<ResourceAdapter> table;
    private ListDataProvider<ResourceAdapter> dataProvider;

    private Form<ResourceAdapter> form;
    private PropertyEditor propertyEditor;
    private DefaultWindow window;

    public AdapterList(ResourceAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {


        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewAdapterWizard();
            }
        }));

        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final ResourceAdapter selection = getCurrentSelection();

                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("Resource Adapter"),
                        Console.MESSAGES.deleteConfirm("Resource Adapter " + selection.getArchive()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onDelete(selection);
                                }
                            }
                        });
            }
        };
        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.addClickHandler(clickHandler);
        topLevelTools.addToolButtonRight(deleteBtn);

        // -------


        table = new DefaultCellTable<ResourceAdapter>(5,
                new ProvidesKey<ResourceAdapter>() {
                    @Override
                    public Object getKey(ResourceAdapter item) {
                        return item.getArchive();
                    }
                });

        dataProvider = new ListDataProvider<ResourceAdapter>();
        dataProvider.addDataDisplay(table);

        TextColumn<ResourceAdapter> nameColumn = new TextColumn<ResourceAdapter>() {
            @Override
            public String getValue(ResourceAdapter record) {
                return record.getArchive();
            }
        };

        TextColumn<ResourceAdapter> numberConnections = new TextColumn<ResourceAdapter>() {
            @Override
            public String getValue(ResourceAdapter record) {
                return String.valueOf(record.getConnectionDefinitions().size());
            }
        };

        Column<ResourceAdapter, ResourceAdapter> option = new Column<ResourceAdapter, ResourceAdapter>(
                new TextLinkCell<ResourceAdapter>(Console.CONSTANTS.common_label_view(), new ActionCell.Delegate<ResourceAdapter>() {
                    @Override
                    public void execute(ResourceAdapter selection) {
                        presenter.getPlaceManager().revealPlace(
                                new PlaceRequest(NameTokens.ResourceAdapterPresenter).with("name", selection.getArchive())
                        );
                    }
                })
        ) {
            @Override
            public ResourceAdapter getValue(ResourceAdapter manager) {
                return manager;
            }
        };

        table.addColumn(nameColumn, "Archive");
        table.addColumn(numberConnections, "Connection Def.");
        table.addColumn(option, "Option");


        // -------

        VerticalPanel formpanel = new VerticalPanel();
        formpanel.setStyleName("fill-layout-width");

        form = new Form<ResourceAdapter>(ResourceAdapter.class);
        form.setNumColumns(2);

        FormToolStrip<ResourceAdapter> toolStrip = new FormToolStrip<ResourceAdapter>(
                form,
                new FormToolStrip.FormCallback<ResourceAdapter>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSave(form.getEditedEntity(), form.getChangedValues());
                    }

                    @Override
                    public void onDelete(ResourceAdapter entity) {

                    }
                });

        toolStrip.providesDeleteOp(false);


        formpanel.add(toolStrip.asWidget());

        // ----

        TextItem nameItem = new TextItem("archive", "Archive");

        ComboBoxItem txItem = new ComboBoxItem("transactionSupport", "TX");
        txItem.setDefaultToFirstOption(true);
        txItem.setValueMap(new String[]{"NoTransaction", "LocalTransaction", "XATransaction"});


        form.setFields(nameItem, txItem);

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "resource-adapters");
                        address.add("resource-adapter", "*");
                        return address;
                    }
                }, form
        );
        formpanel.add(helpPanel.asWidget());
        form.bind(table);

        formpanel.add(form.asWidget());

        form.setEnabled(false);

        table.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ResourceAdapter ra = getCurrentSelection();
                propertyEditor.setProperties("", ra.getProperties());
            }
        });

        // ----

        propertyEditor = new PropertyEditor(this, true);

        // ----
        MultipleToOneLayout layoutBuilder = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle("Resource Adapter")
                .setHeadline("JCA Resource Adapters")
                .setDescription(Console.CONSTANTS.subsys_jca_resource_adapter_desc())
                .setMaster(Console.MESSAGES.available("Resource Adapter"), table)
                .setMasterTools(topLevelTools.asWidget())
                .addDetail("Attributes", formpanel)
                .addDetail("Properties", propertyEditor.asWidget());

        propertyEditor.setAllowEditProps(false);

        return layoutBuilder.build();
    }


    private ResourceAdapter getCurrentSelection() {
        ResourceAdapter selection = ((SingleSelectionModel<ResourceAdapter>) table.getSelectionModel()).getSelectedObject();
        return selection;
    }

    public void setAdapters(List<ResourceAdapter> adapters) {
        dataProvider.setList(adapters);

        propertyEditor.clearValues();
        table.selectDefaultEntity();

    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        closePropertyDialoge();

        presenter.onCreateAdapterProperty(getCurrentSelection(), prop);
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        presenter.onRemoveAdapterProperty(getCurrentSelection(), prop);
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        // not used
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Config Property"));
        window.setWidth(480);
        window.setHeight(360);

        window.trapWidget(
                new NewPropertyWizard(this, "").asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    @Override
    public void closePropertyDialoge() {
        window.hide();
    }
}
