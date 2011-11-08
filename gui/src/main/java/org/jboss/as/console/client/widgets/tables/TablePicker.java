package org.jboss.as.console.client.widgets.tables;

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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.widgets.icons.ConsoleIcons;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/24/11
 */
public class TablePicker<T> { // implements HasValueChangeHandlers<T> {

    private static final int ESCAPE = 27;
    public final static double GOLDEN_RATIO = 1.618;
    private ValueRenderer<T> renderer;


    interface Template extends SafeHtmlTemplates {
        @Template("<div class=\"{0}\">{1}</div>")
        SafeHtml item(String cssClass, String title);
    }

    private static final Template TEMPLATE = GWT.create(Template.class);

    private PopupPanel popup;

    private HorizontalPanel header;
    private HTML currentDisplayedValue;

    private List<ValueChangeHandler<T>> changeHandlers = new ArrayList<ValueChangeHandler<T>>();
    int popupWidth = -1;

    private CellTable<T> cellTable;
    private String description = null;

    public interface ValueRenderer<T> {
        String render(T selection);
    };

    public TablePicker(final CellTable<T> table, final ValueRenderer<T> renderer) {

        this.cellTable = table;
        this.renderer = renderer;
    }

    public void setPopupWidth(int popupWidth) {
        this.popupWidth = popupWidth;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Widget asWidget() {

        if(null==cellTable.getSelectionModel())
            cellTable.setSelectionModel(new SingleSelectionModel<T>());

        cellTable.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                T selection = ((SingleSelectionModel<T>) cellTable.getSelectionModel()).getSelectedObject();
                String displayValue = renderer.render(selection);
                currentDisplayedValue.setText(displayValue);
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


        /*popup.addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });*/

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.addStyleName("tablepicker-popup");

        if(description!=null)
            layout.add(new Label(description));

        layout.add(cellTable);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(cellTable);
        layout.add(pager);

        popup.setWidget(layout);

        currentDisplayedValue = new HTML("&nbsp;");
        currentDisplayedValue.setStyleName("table-picker-value");

        header = new HorizontalPanel();
        header.setStyleName("table-picker");
        header.add(currentDisplayedValue);

        Image img = new Image(ConsoleIcons.INSTANCE.tablePicker());
        header.add(img);

        currentDisplayedValue.getElement().getParentElement().setAttribute("width", "100%");

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

        currentDisplayedValue.addClickHandler(clickHandler);
        img.addClickHandler(clickHandler);

        return header;
    }

    private void openPanel() {

        int winWidth = popupWidth!=-1 ? popupWidth : header.getOffsetWidth() * 2;
        int winHeight = (int) ( winWidth / GOLDEN_RATIO );

        popup.setWidth(winWidth +"px");
        popup.setHeight(winHeight + "px");

        // right to left
        int popupLeft = header.getAbsoluteLeft() - (winWidth - header.getOffsetWidth());
        popup.setPopupPosition(
                popupLeft-15,
                header.getAbsoluteTop()+21
        );

        popup.show();

    }

    /*@Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<T> handler) {

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
        for(ValueChangeHandler<T> handler : changeHandlers)
            handler.onValueChange((ValueChangeEvent<T>)gwtEvent);
    }   */

    public void clearSelection() {
        currentDisplayedValue.setText("");
    }

}

