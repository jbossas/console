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

package org.jboss.as.console.client.auth;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.NameTokens;


public class SignInPagePresenter extends
        Presenter<SignInPagePresenter.MyView, SignInPagePresenter.MyProxy> implements
        SignInPageUIHandlers {

    private final PlaceManager placeManager;
    private CurrentUser user;
    private BootstrapContext bootstrap;

    // private final ErrorDialogPresenterWidget errorDialog;

    @ProxyStandard
    @NameToken(NameTokens.signInPage)
    @NoGatekeeper
    public interface MyProxy extends Proxy<SignInPagePresenter>, Place {
    }

    public interface MyView extends View, HasUiHandlers<SignInPageUIHandlers> {
        HasClickHandlers getSignInButton();
        TextBox getUserName();
        TextBox getPassword();
        void resetAndFocus();

        void setPresenter(SignInPagePresenter signInPagePresenter);

        //void setStandalone(String property);
    }

    @Inject
    public SignInPagePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, CurrentUser user,
            BootstrapContext bootstrap) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.user = user;
        this.bootstrap = bootstrap;
    }

    @Override
    protected void onBind() {
        super.onBind();
        registerHandler(getView().getSignInButton().addClickHandler(
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        doLogin();
                    }
                }));

        final KeyDownHandler handler = new KeyDownHandler()
        {
            public void onKeyDown(KeyDownEvent event)
            {
                if (KeyCodes.KEY_ENTER == event.getNativeKeyCode())
                    doLogin();
            }
        };

        registerHandler(getView().getPassword().addKeyDownHandler(handler));
        registerHandler(getView().getUserName().addKeyDownHandler(handler));

        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        getView().resetAndFocus();
        //getView().setStandalone(bootstrap.getProperty(BootstrapContext.STANDALONE));
    }

    @Override
    protected void revealInParent() {
        RevealRootContentEvent.fire(this, this);
    }

    void doLogin() {

        String userName = getView().getUserName().getText();
        String password = getView().getPassword().getText();

        if (isValidUserName(userName) && (isValidPassword(password))) {

            // TODO: actual login needs to be implemented

            Log.info("Log in as: " + userName);
            user.setUserName(userName);
            user.setLoggedIn(true);

            /*if(bootstrap.hasProperty(BootstrapContext.INITIAL_TOKEN))
            {
                History.newItem(bootstrap.getProperty(BootstrapContext.INITIAL_TOKEN));
            }
            else
            {
                PlaceRequest myRequest = new PlaceRequest(NameTokens.mainLayout);
                placeManager.revealPlace(myRequest);
            }                                       */

            //LoadingOverlay.on(getView().asWidget(), true);
            getView().asWidget().setVisible(false);

            PlaceRequest myRequest = new PlaceRequest(NameTokens.mainLayout);
            placeManager.revealPlace(myRequest);
        }
        else {
            showErrorDialog();
        }
    }

    public CurrentUser getUser() {
        return user;
    }

    private static boolean isValidUserName(String username)
    {
        return true;
    }

    private static boolean isValidPassword(String password)
    {
        return true;
    }

    @Override
    public void showErrorDialog() {
        // TODO: implement authentication error handling
        Log.error("Login failed!");

    }

    public void setBootStandalone(Boolean b) {
        if(b)
            bootstrap.setProperty(BootstrapContext.STANDALONE, b.toString());
        else
            bootstrap.removeProperty(BootstrapContext.STANDALONE);
    }
}
