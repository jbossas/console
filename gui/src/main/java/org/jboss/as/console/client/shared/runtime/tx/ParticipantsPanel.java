package org.jboss.as.console.client.shared.runtime.tx;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/27/13
 */
public class ParticipantsPanel {

    private TXLogPresenter presenter;

    private DefaultCellTable<TXParticipant> table;
    private ListDataProvider<TXParticipant> dataProvider;


    public ParticipantsPanel() {

        table = new DefaultCellTable<TXParticipant>(
                8,
                new ProvidesKey<TXParticipant>() {
                    @Override
                    public Object getKey(TXParticipant item) {
                        return item.getId();
                    }
                });

        dataProvider = new ListDataProvider<TXParticipant>();
        dataProvider.addDataDisplay(table);

        TextColumn<TXParticipant> id = new TextColumn<TXParticipant>() {
            @Override
            public String getValue(TXParticipant record) {
                return record.getId();
            }
        };

        TextColumn<TXParticipant> status = new TextColumn<TXParticipant>() {
            @Override
            public String getValue(TXParticipant record) {
                return record.getStatus();
            }
        };

        TextColumn<TXParticipant> type = new TextColumn<TXParticipant>() {
            @Override
            public String getValue(TXParticipant record) {
                if(record.getType().length()>30)
                    return record.getType().substring(0,29)+" ...";
                else
                    return record.getType();
            }
        };


        EISColumn eis = new EISColumn(new EISCell());

        table.addColumn(id, "ID");
        table.addColumn(status, "Status");
        table.addColumn(type, "Type");
        table.addColumn(eis, "Reference");


        table.setSelectionModel(new SingleSelectionModel<TXParticipant>());
    }

    public void setPresenter(TXLogPresenter presenter) {
        this.presenter = presenter;
    }

    public void clear() {
        dataProvider.getList().clear();
        dataProvider.flush();
    }

    public void updateParticpantsFrom(List<TXParticipant> records) {
        dataProvider.setList(records);

        table.selectDefaultEntity();
    }

    public Widget asWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout");


        ToolStrip tools = new ToolStrip();
        tools.addToolButtonRight(new ToolButton("Recover", new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                final TXParticipant selection = getSelectedRecord();
                if(selection!=null) {
                    Feedback.confirm(
                            "Attempt Recovery",
                            "Really recover TX participant " + selection.getId() + "?",
                            new Feedback.ConfirmationHandler() {
                                @Override
                                public void onConfirmation(boolean confirmed) {
                                    if (confirmed)
                                        presenter.onRecoverParticipant(selection);
                                }
                            }
                    );
                }
            }
        }));

        tools.addToolButtonRight(new ToolButton("Refresh", new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                final TXParticipant selection = getSelectedRecord();
                if(selection!=null) {

                    Feedback.confirm(
                            Console.CONSTANTS.common_label_refresh()+" Transaction",
                            "Really refresh TX participant " + selection.getId() + "?",
                            new Feedback.ConfirmationHandler() {
                                @Override
                                public void onConfirmation(boolean confirmed) {
                                    if (confirmed)
                                        presenter.onRefreshParticipant(selection);
                                }
                            }
                    );

                }
            }
        }));


        DefaultPager pager = new DefaultPager();
        pager.setDisplay(table);


        panel.add(tools);
        panel.add(table);
        panel.add(pager);

        return panel;
    }

    private TXParticipant getSelectedRecord() {
        SingleSelectionModel<TXParticipant> selectionModel = (SingleSelectionModel<TXParticipant>)table.getSelectionModel();
        return selectionModel.getSelectedObject();
    }


    // EIS table cell implementation and styles

    class EISCell extends AbstractCell<TXParticipant>
    {
        @Override
        public void render(Context context, TXParticipant txParticipant, SafeHtmlBuilder html) {
            html.appendHtmlConstant("<ul style='font-size:9px; list-style-type:none'>");
            if(txParticipant.getJndiName()!=null)
                html.appendHtmlConstant("<li>JNDI: ").appendEscaped(txParticipant.getJndiName());

            if(txParticipant.getEisName()!=null)
                html.appendHtmlConstant("<li>EIS: ").appendEscaped(txParticipant.getEisName());

            if(txParticipant.getJmxName()!=null)
                html.appendHtmlConstant("<li>JMX: ").appendEscaped(txParticipant.getJmxName());

            html.appendHtmlConstant("</ul>");
        }
    }

    class EISColumn extends Column<TXParticipant,TXParticipant> {
        public EISColumn(Cell<TXParticipant> cell) {
            super(cell);
        }

        @Override
        public TXParticipant getValue(TXParticipant record) {
            return record;
        }
    };


}
