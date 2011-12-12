package org.jboss.as.console.client.shared.subsys.jca.wizard;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.jca.ResourceAdapterPresenter;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 7/20/11
 */
public class NewAdapterWizard {


    private ResourceAdapterPresenter presenter;
    private DeckPanel deck;
    private AdapterStep2 step2;
    private ResourceAdapter step1Model;

    public NewAdapterWizard(ResourceAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {

        deck = new DeckPanel();

        deck.add(new AdapterStep1(this).asWidget());

        step2 = new AdapterStep2(this);
        deck.add(step2.asWidget());

        deck.showWidget(0);

        return deck;
    }

    ResourceAdapterPresenter getPresenter() {
        return presenter;
    }

    public void onCompleteStep1(ResourceAdapter step1) {
        this.step1Model = step1;
        deck.showWidget(1);
    }

    public void onCompleteStep2(List<PropertyRecord> properties) {

        // merge step1 and 2
        step1Model.setProperties(properties);

        // default pool name
        //step1Model.setPoolName(step1Model.getArchive().replace(".", "_")+"-Pool");
        presenter.onCreateAdapter(step1Model);
    }
}
