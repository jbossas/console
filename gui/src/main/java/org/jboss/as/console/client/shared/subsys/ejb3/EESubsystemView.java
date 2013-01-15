package org.jboss.as.console.client.shared.subsys.ejb3;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.ejb3.model.EESubsystem;
import org.jboss.as.console.client.shared.subsys.ejb3.model.Module;
import org.jboss.as.console.client.layout.FormLayout;
import org.jboss.as.console.client.layout.OneToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class EESubsystemView extends DisposableViewImpl implements EEPresenter.MyView {

    private EEPresenter presenter;
    private Form<EESubsystem> form;
    private ListDataProvider<Module> dataProvider;
    private DefaultCellTable<Module> moduleList;

    @Override
    public Widget createWidget() {
        form = new Form<EESubsystem>(EESubsystem.class);

        CheckBoxItem isolation = new CheckBoxItem("isolatedSubdeployments", "Isolated Subdeployments?");

        form.setFields(isolation);
        form.setEnabled(false);

        FormToolStrip<EESubsystem> formToolStrip = new FormToolStrip<EESubsystem>(
                form, new FormToolStrip.FormCallback<EESubsystem>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSave(form.getEditedEntity(), changeset);
            }

            @Override
            public void onDelete(EESubsystem entity) {
                // cannot be removed
            }
        });
        formToolStrip.providesDeleteOp(false);

        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "ee");
                return address;
            }
        }, form);

        Widget master = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel).build();


        // -----
        // module list

        moduleList = new DefaultCellTable<Module>(5, new ProvidesKey<Module>() {
            @Override
            public Object getKey(Module item) {
                return item.getName()+"_"+item.getSlot();
            }
        });

        dataProvider = new ListDataProvider<Module>();
        dataProvider.addDataDisplay(moduleList);
        moduleList.setSelectionModel(new SingleSelectionModel());

        TextColumn<Module> name = new TextColumn<Module>() {
            @Override
            public String getValue(Module record) {
                return record.getName();
            }
        };

        TextColumn<Module> slot = new TextColumn<Module>() {
            @Override
            public String getValue(Module record) {
                return record.getSlot();
            }
        };

        moduleList.addColumn(name, "Name");
        moduleList.addColumn(slot, "Slot");

        ToolStrip moduleTools = new ToolStrip();
        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewModuleDialogue();
            }
        });
        addBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_eESubsystemView());
        moduleTools.addToolButtonRight(addBtn);

        ToolButton button = new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final Module module = ((SingleSelectionModel<Module>) moduleList.getSelectionModel()).getSelectedObject();
                if(null==module) return;

                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("Module"),
                        Console.MESSAGES.deleteConfirm("Module"),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if(isConfirmed)
                                {
                                    presenter.onRemoveModule(form.getEditedEntity(), module);
                                }
                            }
                        });
            }
        });
        button.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_remove_eESubsystemView());
        moduleTools.addToolButtonRight(button);

        VerticalPanel modulePanel = new VerticalPanel();
        modulePanel.setStyleName("fill-layout-width");

        modulePanel.add(moduleTools.asWidget());
        modulePanel.add(moduleList.asWidget());

        // ----

        Widget panel = new OneToOneLayout()
                .setTitle("EE")
                .setHeadline("EE Subsystem")
                .setDescription(Console.CONSTANTS.subsys_ee_desc())
                .setMaster("Subsystem Defaults", master)
                .setMasterTools(formToolStrip.asWidget())
                .setDetail("Global Modules", modulePanel).build();



        modulePanel.getElement().setAttribute("style", "padding-top:5px");

        return panel;
    }

    @Override
    public void setPresenter(EEPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateFrom(EESubsystem eeSubsystem) {
        form.edit(eeSubsystem);
        dataProvider.setList(eeSubsystem.getModules());

        moduleList.selectDefaultEntity();
    }
}
