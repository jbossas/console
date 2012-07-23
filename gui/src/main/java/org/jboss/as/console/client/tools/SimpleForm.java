package org.jboss.as.console.client.tools;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.forms.DefaultGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.GroupRenderer;
import org.jboss.ballroom.client.widgets.forms.PlainFormView;
import org.jboss.ballroom.client.widgets.forms.RenderMetaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 7/23/12
 */
public class SimpleForm {
    private List<FormItem> items = new LinkedList<FormItem>();
    private DeckPanel deck;
    private int numColumns = 1;
    private List<PlainFormView> plainViews = new ArrayList<PlainFormView>();
    private boolean enabled;
    private Map<String, Object> snapshot;

    public void setFields(FormItem... fields)
    {
        for(FormItem item : fields)
            items.add(item);
    }

    public void edit(Map<String, Object> values)
    {
        this.snapshot = values;

        for(String key : values.keySet())
        {
            for(FormItem item : items)
            {
                if(item.getName().equals(key))
                {
                    item.resetMetaData();
                    final Object value = values.get(key);

                    if(value!=null)
                    {
                        item.setUndefined(false);
                        item.setValue(value);
                    }
                    else
                    {
                        item.setUndefined(true);
                        item.setModified(true); // don't escape validation
                    }

                    item.setValue(value);
                    break;
                }
            }
        }

        refreshPlainView();
    }

    public Map<String,Object> getChangedValues()
    {
        Map<String, Object> values = new HashMap<String, Object>();
        for(FormItem item : items)
        {
            if(item.isModified())
            {
                values.put(item.getName(), item.getValue());
            }
        }

        return values;
    }

    public void clearValues() {
        for(FormItem item : items)
        {
            item.clearValue();
            item.resetMetaData();
        }
    }

    private Widget build() {

        deck = new DeckPanel();
        deck.setStyleName("fill-layout-width");

        // ----------------------
        // view panel

        VerticalPanel viewPanel = new VerticalPanel();
        viewPanel.setStyleName("fill-layout-width");
        viewPanel.addStyleName("form-view-panel");
        deck.add(viewPanel.asWidget());

        // ----------------------
        // edit panel

        VerticalPanel editPanel = new VerticalPanel();
        editPanel.setStyleName("fill-layout-width");
        editPanel.addStyleName("form-edit-panel");

        RenderMetaData metaData = new RenderMetaData();
        metaData.setNumColumns(numColumns);

        GroupRenderer groupRenderer = new DefaultGroupRenderer();;


        Map<String, FormItem> groupItems = new HashMap<String, FormItem>();
        for(FormItem item : items)
        {
            groupItems.put(item.getName(), item);
        }

        // edit view
        Widget widget = groupRenderer.render(metaData, "default", groupItems);
        editPanel.add(widget);

        // plain view
        PlainFormView plainView = new PlainFormView(new ArrayList<FormItem>(groupItems.values()));
        plainView.setNumColumns(numColumns);
        plainViews.add(plainView);
        viewPanel.add(groupRenderer.renderPlain(metaData, "default", plainView));

        deck.add(editPanel);

        // toggle default view
        toggleViews();
        refreshPlainView(); // make sureit's build, even empty...

        return deck;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if(deck!=null)  // might no be created yet (backwards compatibility)
            toggleViews();
    }

    private void toggleViews() {
        int index = enabled ? 1 :0;
        deck.showWidget(index);
    }

    private void refreshPlainView() {
        for(PlainFormView view : plainViews)
            view.refresh(true);
    }

    public void setNumColumns(int i) {
        this.numColumns = i;
    }


    public FormValidation validate() {
        FormValidation outcome = new FormValidation();

        for(FormItem item : items)
        {
            // two cases: empty form (create entity) and updating an existing entity
            // we basically force validation on newly created entities
            boolean requiresValidation = item.isModified();

            if(requiresValidation)
            {
                Object value = item.getValue();

                // ascii or empty string are ok. the later will be checked in each form item implentation.
                String stringValue = String.valueOf(value);
                boolean ascii = stringValue.isEmpty() ||
                        stringValue.matches("^[\\u0020-\\u007e]+$");

                if(!ascii)
                {
                    outcome.addError(item.getName());
                    item.setErroneous(true);
                }
                else
                {
                    boolean validValue = item.validate(value);
                    if(validValue)
                    {
                        item.setErroneous(false);
                    }
                    else
                    {
                        outcome.addError(item.getName());
                        item.setErroneous(true);
                    }
                }
            }
        }


        return outcome;
    }

    public void cancel() {
        if(snapshot!=null)
            edit(snapshot);
    }

    public Widget asWidget() {
        build();
        return deck;
    }
}
