package org.jboss.as.console.client.widgets;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
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

    private PopupPanel popup;

    private CellList<String> cellList;

    private HorizontalPanel header;
    private HTML currentValue;

    private String cssSuffix = "";
    private boolean isEnabled = true;


    private List<ValueChangeHandler<String>> changeHandlers = new ArrayList<ValueChangeHandler<String>>();

    public ComboBox(String cssSuffix) {

        this.cssSuffix = cssSuffix;

        cellList = new CellList<String>(new TextCell()
        {
            @Override
            public void render(Context context, String data, SafeHtmlBuilder sb) {
                String cssName = (context.getIndex() %2 > 0) ? "combobox-item combobox-item-odd" : "combobox-item";

                if(data.equals(currentValue.getText()))
                    cssName+=" combobox-item-selected";

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
                popup.hide();

                onSelection(selectedValue);
            }
        });

        final String panelId = "popup_"+HTMLPanel.createUniqueId();
        popup = new PopupPanel(true, true) {

            @Override
            protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                if (Event.ONKEYUP == event.getTypeInt()) {
                    if (event.getNativeEvent().getKeyCode() == ESCAPE) {
                        // Dismiss when escape is pressed
                        popup.hide();
                    }
                }
            }

            public void onBrowserEvent(Event event) {
                super.onBrowserEvent(event);
            }
        };

        popup.getElement().setId(panelId);

        popup.setStyleName("default-popup");

        popup.addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        popup.setWidget(cellList);

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

    private void onSelection(String selectedValue) {
        ValueChangeEvent.fire(this, selectedValue);
    }

    public Widget asWidget() {
        return header;
    };

    private void openPanel() {

        if(isEnabled)
        {
            popup.setWidth(header.getOffsetWidth()+"px");
            popup.setHeight((cellList.getRowCount()*25)+"px");

            popup.setPopupPosition(
                    header.getAbsoluteLeft(),
                    header.getAbsoluteTop()+20
            );

            popup.show();
        }
    }

    public int getItemCount() {
        return cellList.getRowCount();
    }

    public String getValue(int i) {
        return values.get(i);
    }

    public void setItemSelected(int i, boolean isSelected) {

        if(!isSelected){
            currentValue.setText("");
            this.selectedItemIndex = -1;
        }
        else
        {
            this.selectedItemIndex =  i;
            currentValue.setText(values.get(selectedItemIndex));
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

        changeHandlers.add(handler);

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                changeHandlers.remove(handler);
            }
        };
    }

    @Override
    public void fireEvent(GwtEvent<?> gwtEvent) {
        for(ValueChangeHandler<String> handler : changeHandlers)
            handler.onValueChange((ValueChangeEvent<String>)gwtEvent);
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

    public void clearSelection() {
         for(int i=0; i< getItemCount(); i++)
        {
            setItemSelected(i, false);
        }
    }
}
