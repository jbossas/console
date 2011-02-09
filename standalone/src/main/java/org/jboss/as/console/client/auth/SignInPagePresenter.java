package org.jboss.as.console.client.auth;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.*;
import org.jboss.as.console.client.NameTokens;

public class SignInPagePresenter extends
        Presenter<SignInPagePresenter.MyView, SignInPagePresenter.MyProxy> implements
        SignInPageUIHandlers {

    private final PlaceManager placeManager;
    private CurrentUser user;

    // private final ErrorDialogPresenterWidget errorDialog;

    @ProxyStandard
    @NameToken(NameTokens.signInPage)
    @NoGatekeeper
    public interface MyProxy extends Proxy<SignInPagePresenter>, Place {
    }

    public interface MyView extends View, HasUiHandlers<SignInPageUIHandlers> {
        HasClickHandlers getSignInButton();
        String getUserName();
        String getPassword();
        void resetAndFocus();
    }

    @Inject
    public SignInPagePresenter(EventBus eventBus, MyView view, MyProxy proxy,
                               PlaceManager placeManager, CurrentUser user) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.user = user;
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
    }


    @Override
    protected void onReset() {
        super.onReset();
        getView().resetAndFocus();
    }

    @Override
    protected void revealInParent() {
        RevealRootContentEvent.fire(this, this);
    }

    void doLogin() {

        String userName = getView().getUserName();
        String password = getView().getPassword();

        if (isValidUserName(userName) && (isValidPassword(password))) {

            // TODO: actual login needs to be implemented

            Log.info("Log in as: " + userName);
            user.setUserName(userName);
            user.setLoggedIn(true);

            PlaceRequest myRequest = new PlaceRequest(NameTokens.mainLayout);
            placeManager.revealPlace(myRequest);

            // notify listeners
            getEventBus().fireEvent(new AuthenticationEvent(user));
        }
        else {
            showErrorDialog();
        }
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
        //addToPopupSlot(errorDialog);
        // TODO: implement authentication error handling
        Log.error("Login failed!");

    }
}
