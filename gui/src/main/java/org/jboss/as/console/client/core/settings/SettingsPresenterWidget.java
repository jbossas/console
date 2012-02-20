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

import com.google.gwt.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.Preferences;

import javax.inject.Inject;

/**
 * Maintains the settings dialogue
 *
 * @author Heiko Braun
 * @date 5/3/11
 */
public class SettingsPresenterWidget
        extends PresenterWidget<SettingsPresenterWidget.MyView> {

    private BeanFactory factory;


    public interface MyView extends PopupView {
        void setPresenter(SettingsPresenterWidget presenter);
    }

    @Inject
    public SettingsPresenterWidget(
            final EventBus eventBus, final MyView view,
            BeanFactory factory) {
        super(eventBus, view);
        view.setPresenter(this);
        this.factory = factory;
    }

    public void hideView() {
        getView().hide();
    }

    public void onSaveDialogue(CommonSettings settings) {

        // see also App.gwt.xml
        if(settings.getLocale()!=null && !settings.getLocale().equals(""))
            Preferences.set("as7_ui_locale", settings.getLocale());

        Console.info(Console.MESSAGES.savedSettings());

    }

    public void onCancelDialogue() {
        getView().hide();
    }

    public CommonSettings getCommonSettings() {
        CommonSettings settings = factory.settings().as();
        settings.setLocale(Preferences.get("as7_ui_locale"));
        return settings;
    }


}
