package org.jboss.as.console.client.shared.subsys.ejb3;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class NewModuleWizard {
    private EEPresenter presenter;
    private List<String> modules;

    public NewModuleWizard(EEPresenter eePresenter, List<String> modules) {
        this.presenter = eePresenter;
        this.modules = modules;
    }


    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");


        final DefaultCellTable<String> table = new DefaultCellTable<String>(5);
        table.setSelectionModel(new SingleSelectionModel<String>());
        ListDataProvider<String> dataProvider = new ListDataProvider<String>();
        dataProvider.addDataDisplay(table);

        TextColumn<String> name = new TextColumn<String>() {
            @Override
            public String getValue(String record) {
                return record;
            }
        };

        table.addColumn(name, "Name");

        layout.add(table.asWidget());

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(table);
        layout.add(pager.asWidget());

        dataProvider.setList(modules);

        DialogueOptions options = new DialogueOptions(

                // save
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {

                        String name = ((SingleSelectionModel<String>)table.getSelectionModel()).getSelectedObject();
                        presenter.onAddModule(name);

                    }
                },

                // cancel
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.closeDialoge();
                    }
                }

        );


        return new WindowContentBuilder(layout, options).build();
    }

    class Wrapper {
        String name;

        Wrapper(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
