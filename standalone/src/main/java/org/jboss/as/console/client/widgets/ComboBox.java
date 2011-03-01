package org.jboss.as.console.client.widgets;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.widgets.resource.WidgetResources;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/24/11
 */
public class ComboBox implements HasValueChangeHandlers<String> {

    private static final int ESCAPE = 27;

    private List<String> values = new ArrayList<String>();
    private int selectedItemIndex = -1;

    interface Template extends SafeHtmlTemplates {
        @Template("<div class=\"{0}\">{1}</div>")
        SafeHtml item(String cssClass, String title);
    }

    private static final Template TEMPLATE = GWT.create(Template.class);

    private PopupPanel panel;

    private CellList<String> cellList;

    private HorizontalPanel header;
    private HTML currentValue;

    private String cssSuffix = "";
    private boolean isEnabled = true;

    public ComboBox(String cssSuffix) {

        this.cssSuffix = cssSuffix;

        cellList = new CellList<String>(new TextCell()
        {
            @Override
            public void render(Context context, String data, SafeHtmlBuilder sb) {
                String cssName = (context.getIndex() %2 > 0) ? "combobox-item combobox-item-odd" : "combobox-item";

                sb.append(TEMPLATE.item(cssName, data));
            }

        });

        final SingleSelectionModel<String> selectionModel =
                new SingleSelectionModel<String>();
        cellList.setSelectionModel(selectionModel);

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                String selectedValue = selectionModel.getSelectedObject();

                currentValue.setHTML(selectedValue);
                panel.hide();

                handleSelection(selectedValue);
            }
        });

        final String panelId = "popup_"+HTMLPanel.createUniqueId();
        panel = new PopupPanel(true, true) {

            {
                //sinkEvents(Event.ONMOUSEOUT);
            }

            @Override
            protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                if (Event.ONKEYUP == event.getTypeInt()) {
                    if (event.getNativeEvent().getKeyCode() == ESCAPE) {
                        // Dismiss when escape is pressed
                        panel.hide();
                    }
                }
            }

            public void onBrowserEvent(Event event) {
                super.onBrowserEvent(event);

                /*if (DOM.eventGetType(event) == Event.ONMOUSEOUT) {
                    Element element = DOM.eventGetTarget(event);
                    if(panelId.equals(element.getId()))
                        hide();
                } */
            }
        };

        panel.getElement().setId(panelId);

        panel.setStyleName("default-popup");

        panel.addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        panel.setWidget(cellList);

        currentValue = new HTML("&nbsp;");
        currentValue.setStyleName("combobox-value"+cssSuffix);

        header = new HorizontalPanel();
        header.setStyleName("combobox"+cssSuffix);
        header.add(currentValue);

        Image img = new Image(WidgetResources.INSTANCE.comboPicker());
        header.add(img);

        currentValue.getElement().getParentElement().setAttribute("width", "100%");
        img.getParent().getElement().setAttribute("width", "18");

        header.getElement().setAttribute("width", "100%");
        header.getElement().setAttribute("cellspacing", "0");
        header.getElement().setAttribute("cellpadding", "0");
        header.getElement().setAttribute("border", "0");

        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                openPanel();
            }
        };

        currentValue.addClickHandler(clickHandler);
        img.addClickHandler(clickHandler);

    }

    public ComboBox() {
        this("");
    }

    public String getSelectedValue() {
        return currentValue.getText();
    }

    private void handleSelection(String selectedValue) {
        ValueChangeEvent.fire(this, selectedValue);
    }

    public Widget asWidget() {
        return header;
    };

    private void openPanel() {

        if(isEnabled)
        {
            panel.setWidth(header.getOffsetWidth()+"px");
            panel.setHeight((cellList.getRowCount()*25)+"px");

            panel.setPopupPosition(
                    header.getAbsoluteLeft(),
                    header.getAbsoluteTop()+20
            );

            panel.show();
        }
    }

    public int getItemCount() {
        return cellList.getRowCount();
    }

    public String getValue(int i) {
        return values.get(i);
    }

    public void setItemSelected(int i, boolean b) {
        if(b)
        {
            this.selectedItemIndex =  i;
            currentValue.setText(values.get(selectedItemIndex));
        }
        else
        {
            this.selectedItemIndex = -1;
        }
    }

    public void setValues(List<String> values)
    {
        clearValues();
        this.values.addAll(values);

        refeshCellList();
    }

    private void refeshCellList() {
        cellList.setRowData(0, this.values);
    }

    public void clearValues() {
        this.values.clear();
    }

    public void addItem(String s) {
        this.values.add(s);
        refeshCellList();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {

        return Console.MODULES.getEventBus().addHandler(
                ValueChangeEvent.getType(), handler
        );

    }

    @Override
    public void fireEvent(GwtEvent<?> gwtEvent) {
        Console.MODULES.getEventBus().fireEvent(gwtEvent);
    }

    public void setEnabled(boolean b) {
        this.isEnabled = b;
        if(isEnabled)
        {
            currentValue.removeStyleName("combobox-value-disabled");
        }
        else
        {
            currentValue.addStyleName("combobox-value-disabled");
        }
    }
}
