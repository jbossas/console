package org.jboss.as.console.client.shared.viewframework;

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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.ballroom.client.widgets.forms.ListEditor;
import org.jboss.ballroom.client.widgets.forms.ListManagement;
import org.jboss.ballroom.client.widgets.forms.NewListItemWizard;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmbeddedListView implements ListManagement<String>, IsWidget {

    protected ListEditor listEditor;
    protected List<String> value = Collections.EMPTY_LIST;

    protected DefaultWindow addItemDialog;
    protected String addDialogTitle = Console.CONSTANTS.common_label_addItem();

    protected NewListItemWizard newListItemWizard;
    protected List<String> availableChoices = Collections.EMPTY_LIST;
    private String title;
    private ListManagement<String> delegate;

    /**
     * Create a new ListEditorFormItem.
     *
     * @param title The label that will be displayed with the editor.
     * @param title The title shown when the Add button is pressed.
     * @param rows The max number of rows in the PropertyEditor.
     * @param limitChoices If <code>true</code> choices for new items will be limited to values provided
     *                     in the setAvailableChoices() method.  If <code>false</code> the user may add any String value
     *                     to the list.
     */
    public EmbeddedListView(String title, int rows, boolean limitChoices, ListManagement<String> delegate) {

        this.title = title;
        this.listEditor = new ListEditor(this, rows);
        this.newListItemWizard = new NewListItemWizard(this, limitChoices);
        this.delegate = delegate;
    }

    public void setValueColumnHeader(String headerLabel) {
        this.listEditor.setValueColumnHeader(headerLabel);
        this.newListItemWizard.setLabel(headerLabel);
        this.addDialogTitle = Console.CONSTANTS.common_label_add() + " " + headerLabel;
    }

    /**
     * This is the full list of available choices.  The list may be
     */
    public void setAvailableChoices(List<String> availableChoices) {
        this.availableChoices = availableChoices;
    }

    @Override
    public Widget asWidget() {
        return this.listEditor.asWidget();
    }

    public List<String> getValue() {
        return this.value;
    }

    public void setValue(List<String> items) {
        // clone the item so that you can cancel the edit
        List<String> itemsClone = new ArrayList<String>(items.size());
        itemsClone.addAll(items);

        this.value = itemsClone;
        this.listEditor.setList(itemsClone);
    }

    @Override
    public void closeNewItemDialoge() {
        addItemDialog.hide();
    }

    @Override
    public void launchNewItemDialoge() {
        addItemDialog = new DefaultWindow(addDialogTitle);
        addItemDialog.setWidth(320);
        addItemDialog.setHeight(240);
        addItemDialog.trapWidget(newListItemWizard.asWidget());
        addItemDialog.setGlassEnabled(true);
        addItemDialog.center();

        if (!newListItemWizard.isChoiceLimited()) return;
        
        // create list containing only choices not yet in the list
        List<String> choicesSubset = new ArrayList<String>(this.availableChoices.size());
        choicesSubset.addAll(this.availableChoices);
        choicesSubset.removeAll(value);
        newListItemWizard.setChoices(choicesSubset);
    }

    @Override
    public void onCreateItem(String item) {
        //this.value.add(item);
        //this.listEditor.setList(value);
        closeNewItemDialoge();

        delegate.onCreateItem(item);
    }

    @Override
    public void onDeleteItem(String item) {
        //this.value.remove(item);
        //this.listEditor.setList(value);

        delegate.onDeleteItem(item);
    }
}
