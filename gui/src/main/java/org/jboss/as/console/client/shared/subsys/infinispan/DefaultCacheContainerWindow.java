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
package org.jboss.as.console.client.shared.subsys.infinispan;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.infinispan.model.CacheContainer;
import org.jboss.as.console.client.shared.subsys.infinispan.model.DefaultCacheContainer;
import org.jboss.as.console.client.shared.viewframework.EntityPopupWindow;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.shared.viewframework.SingleEntityToDmrBridgeImpl;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class DefaultCacheContainerWindow extends EntityPopupWindow<DefaultCacheContainer> {

    private static final ComboBoxItem valueCombo = new ComboBoxItem("name", "Default Cache Container");
    private static final Form<DefaultCacheContainer> myForm = makeForm(valueCombo);
    private static final WindowView myView = new WindowView(myForm);

    public DefaultCacheContainerWindow(ApplicationMetaData propertyMetadata, DispatchAsync dispatcher) {
        super("Set Default Cache Container", myForm, null,
                new SingleEntityToDmrBridgeImpl(propertyMetadata, DefaultCacheContainer.class, myView, dispatcher));
        myView.setBridge(bridge);
        this.bridge.loadEntities(null);
        setWidth("330");
        setHeight("180");
    }

    private static Form<DefaultCacheContainer> makeForm(ComboBoxItem valueCombo) {
        Form<DefaultCacheContainer> form = new Form<DefaultCacheContainer>(DefaultCacheContainer.class);
        form.setFields(valueCombo);
        return form;
    }

    public static void setChoices(Collection<CacheContainer> choices) {
        List<String> names = new ArrayList(choices.size());

        for (CacheContainer container : choices) names.add(container.getName());

        valueCombo.setValueMap(names);

        valueCombo.setValue(((DefaultCacheContainer)myView.getBridge().getEntityList().get(0)).getName());
    }

    @Override
    protected void doCommand(FormAdapter<DefaultCacheContainer> form) {
        this.bridge.onSaveDetails(form.getUpdatedEntity(), form.getChangedValues());
    }

    private static class WindowView implements FrameworkView {
        private Form myForm;
        private EntityToDmrBridge bridge;

        WindowView(Form myForm) {
            this.myForm = myForm;
        }

        void setBridge(EntityToDmrBridge bridge) {
            this.bridge = bridge;
        }

        EntityToDmrBridge getBridge() {
            return this.bridge;
        }

        @Override
        public Widget asWidget() {
            return null;
        }

        @Override
        public void initialLoad() {
            // do nothing
        }

        @Override
        public void refresh() {
            DefaultCacheContainer entity = (DefaultCacheContainer)bridge.getEntityList().get(0);
            myForm.edit(entity);
        }

        @Override
        public void setEditingEnabled(boolean isEnabled) {
            // do nothing
        }

    }

}
