package org.jboss.as.console.client.widgets.popups;

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import org.jboss.ballroom.client.widgets.icons.Icons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/24/11
 */
public class ComboPicker implements HasValueChangeHandlers<String> {

    private static final int ESCAPE = 27;

    private List<String> values = new ArrayList<String>();
    private int numCharsClip = 20;

    interface Template extends SafeHtmlTemplates {
        @Template("<div class=\"{0}\">{1}</div>")
        SafeHtml item(String cssClass, String title);
    }

    private static final Template TEMPLATE = GWT.create(Template.class);

    private PopupPanel popup;

    private CellList<String> cellList;

    private HorizontalPanel header;
    private Display displayed;

    private boolean isEnabled = true;

    private List<ValueChangeHandler<String>> changeHandlers = new ArrayList<ValueChangeHandler<String>>();

    class Display extends HTML {
        private String actual;

        Display(String html) {
            super(html);
            setStyleName("table-picker-value");
        }

        public String getActual() {
            return actual;
        }

        public void setActual(String actual) {
            this.actual = actual;
        }
    }

    public ComboPicker(String cssSuffix) {

        cellList = new CellList<String>(new TextCell()
        {
            @Override
            public void render(Context context, String data, SafeHtmlBuilder sb) {
                String cssName = (context.getIndex() %2 > 0) ? "combobox-item combobox-item-odd" : "combobox-item";

                if(data.equals(displayed.getActual()))
                    cssName+=" combobox-item-selected";

                sb.append(TEMPLATE.item(cssName, data));
            }

        });

        final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
        cellList.setSelectionModel(selectionModel);

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                String selectedValue = selectionModel.getSelectedObject();

                setDisplayedValue(selectedValue);
                popup.hide();

                onSelection(selectedValue);
            }
        });

        final String panelId = "popup_"+ HTMLPanel.createUniqueId();
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

        popup.addStyleName("triangle-border");
        popup.addStyleName("top-left");

        popup.setWidget(cellList);

        displayed = new Display("");

        header = new HorizontalPanel();
        header.setStyleName("combobox"+cssSuffix);
        header.add(displayed);

        HTML icon = new HTML("<span style='font-size:18px;cursor:pointer'><i class='icon-caret-down'></i></span>");
        header.add(icon);

        displayed.getElement().getParentElement().setAttribute("width", "100%");

        icon.getParent().getElement().setAttribute("width", "18");

        //header.getElement().setAttribute("width", "95%");
        header.getElement().setAttribute("cellspacing", "0");
        header.getElement().setAttribute("cellpadding", "0");
        header.getElement().setAttribute("border", "0");


        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                openPanel();
            }
        };

        displayed.addClickHandler(clickHandler);
        icon.addClickHandler(clickHandler);

    }

    public ComboPicker() {
        this("");
    }

    public void setClipping(int clipAt) {
        numCharsClip = clipAt;
    }

    public String getSelectedValue() {
        return displayed.getActual();
    }

    private void onSelection(String selectedValue) {
        ValueChangeEvent.fire(this, selectedValue);
    }

    public Widget asWidget() {
        return header;
    }

    private void openPanel() {

        if(isEnabled)
        {
            popup.setWidth((header.getOffsetWidth()+60)+"px");
            popup.setHeight((cellList.getRowCount()*36)+"px");

            popup.setPopupPosition(
                    header.getAbsoluteLeft()-5,
                    header.getAbsoluteTop()+35
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

        String selection = "";

        if(isSelected && !values.isEmpty())
        {
            selection = values.get(i);
            setDisplayedValue(selection);
            onSelection(selection);
        }
        else if(!isSelected)
        {
            setDisplayedValue(selection);
            onSelection(selection);
        }

        cellList.getSelectionModel().setSelected(selection, isSelected);
    }

    private void setDisplayedValue(String display)
    {
        displayed.setActual(display);
        displayed.setText(clip(display, numCharsClip));
    }

    private static String clip(String value, int clipping)
    {
        String result = value;
        if(value!=null && value.length()>clipping)
            result = value.substring(0, clipping)+"...";
        return result;
    }

    public void setValues(Collection<String> values)
    {
        clearSelection();
        clearValues();
        this.values.addAll(values);

        refeshCellList();
    }

    private void refeshCellList() {
        cellList.setRowCount(this.values.size(), true);
        cellList.setRowData(0, this.values);
    }

    public void clearValues() {
        this.values.clear();
        clearSelection();
        refeshCellList();
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
            displayed.removeStyleName("combobox-value-disabled");
        }
        else
        {
            displayed.addStyleName("combobox-value-disabled");
        }
    }

    public void clearSelection() {
        setDisplayedValue("");
        for(int i=0; i< getItemCount(); i++)
        {
            setItemSelected(i, false);
        }
    }
}
