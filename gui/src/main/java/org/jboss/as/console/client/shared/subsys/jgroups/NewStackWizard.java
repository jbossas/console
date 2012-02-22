package org.jboss.as.console.client.shared.subsys.jgroups;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/16/12
 */
public class NewStackWizard {


    private JGroupsPresenter presenter;
    private DeckPanel deck;
    private StackStep2 step2;
    private JGroupsStack step1Entity;

    public NewStackWizard(JGroupsPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        deck = new DeckPanel();

        deck.add(new StackStep1(this).asWidget());

        step2 = new StackStep2(this);
        deck.add(step2.asWidget());

        deck.showWidget(0);

        return deck;

    }

    public void cancel() {
        presenter.closeDialoge();
    }

    public void onFinishStep1(JGroupsStack step1) {
        this.step1Entity = step1;
        deck.showWidget(1);
    }

    public void onFinishStep2(List<JGroupsProtocol> protocols) {

        // merge two entities

        step1Entity.setProtocols(protocols);

        presenter.onCreateStack(step1Entity);
    }
}
