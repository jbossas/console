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

package org.jboss.as.console.client.core.settings;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupViewImpl;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.widgets.window.DialogueOptions;
import org.jboss.as.console.client.widgets.window.Feedback;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.icons.Icons;


/**
 * @author Heiko Braun
 * @date 5/3/11
 */
public class SettingsView extends PopupViewImpl implements SettingsPresenterWidget.MyView{

    private PopupPanel popup;
    private SettingsPresenterWidget presenter;
    private Form<CommonSettings> form ;

    @Inject
    public SettingsView(EventBus eventBus) {
        super(eventBus);

        popup = new PopupPanel() {
            @Override
            public void center() {
                super.center();
                onCenter();
            }
        };
        popup.setStyleName("default-window");
        popup.setWidth("640px");
        popup.setHeight("480px");
        popup.setGlassEnabled(true);
        popup.setAutoHideEnabled(true);

        DockLayoutPanel layout = new DockLayoutPanel(Style.Unit.PX);

        HorizontalPanel header = new HorizontalPanel();
        header.setStyleName("default-window-header");
        header.getElement().setAttribute("cellpadding", "4");

        HTML titleText = new HTML(Console.CONSTANTS.common_label_settings());

        Image closeIcon = new Image(Icons.INSTANCE.close());
        closeIcon.setAltText("Close");
        closeIcon.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.hideView();
            }
        });

        header.add(titleText);
        header.add(closeIcon);

        // it's just a table ...
        titleText.getElement().getParentElement().setAttribute("width", "100%");
        closeIcon.getElement().getParentElement().setAttribute("width", "16px");

        //header.setWidgetRightWidth(closeIcon, 5, Style.Unit.PX, 16, Style.Unit.PX);
        //header.setWidgetRightWidth(titleText, 21, Style.Unit.PX, 95, Style.Unit.PCT);

        layout.addNorth(header, 25);


        createContents(layout);

        popup.setWidget(layout);
    }

    private void onCenter() {
        form.edit(presenter.getCommonSettings());
    }

    private void createContents(DockLayoutPanel content) {
        form = new Form<CommonSettings>(CommonSettings.class);

        ComboBoxItem localeItem = new ComboBoxItem("locale", "Locale");
        localeItem.setValueMap(new String[] {"en", "de"});
        localeItem.setDefaultToFirstOption(true);

        form.setFields(localeItem);

        Widget formWidget = form.asWidget();
        formWidget.getElement().setAttribute("style", "margin:15px");

        DialogueOptions options = new DialogueOptions(
                Console.CONSTANTS.common_label_save(),
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.onSaveDialogue(form.getUpdatedEntity());

                        presenter.hideView();

                        Feedback.confirm(Console.MESSAGES.restartRequired(), Console.MESSAGES.restartRequiredConfirm(),
                                new Feedback.ConfirmationHandler()
                                {
                                    @Override
                                    public void onConfirmation(boolean isConfirmed) {

                                        // Ignore: it crashes the browser..

                                        /*if(isConfirmed){
                                            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                                                @Override
                                                public void execute() {
                                                    reload();
                                                }
                                            });

                                        } */
                                    }
                                });
                    }
                },
                Console.CONSTANTS.common_label_cancel(),
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.onCancelDialogue();
                    }
                }
        );

        options.getElement().setAttribute("style", "padding:10px");

        content.addSouth(options, 50);
        content.add(formWidget);
    }


    @Override
    public Widget asWidget() {
        return popup;
    }

    @Override
    public void setPresenter(SettingsPresenterWidget presenter) {
        this.presenter = presenter;
    }

    public static native JavaScriptObject reload() /*-{
        window.location.reload();
    }-*/;
}
