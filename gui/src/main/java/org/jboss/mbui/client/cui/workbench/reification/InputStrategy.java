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
package org.jboss.mbui.client.cui.workbench.reification;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.mbui.client.aui.aim.Input;
import org.jboss.mbui.client.aui.aim.InteractionUnit;
import org.jboss.mbui.client.aui.mapping.ResourceAttribute;
import org.jboss.mbui.client.aui.mapping.ResourceMapping;
import org.jboss.mbui.client.cui.Context;
import org.jboss.mbui.client.cui.ReificationStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Harald Pehl
 * @date 11/01/2012
 */
public class InputStrategy implements ReificationStrategy<ReificationWidget>
{
    @Override
    public ReificationWidget reify(final InteractionUnit interactionUnit, final Context context)
    {
        FormAdapter adapter = null;
        if (interactionUnit != null)
        {
            adapter = new FormAdapter(interactionUnit);
        }
        return adapter;
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return interactionUnit instanceof Input;
    }


    class FormAdapter implements ReificationWidget
    {
        final Form<?> form; // TODO Find the right type argument
        final InteractionUnit interactionUnit;

        FormAdapter(final InteractionUnit interactionUnit)
        {
            this.interactionUnit = interactionUnit;
            this.form = new Form<Object>(Object.class);
            this.form.setNumColumns(2);
            this.form.setEnabled(false);

            // TODO There can be many mappings. How do we know the ID for the resource mapping?
            ResourceMapping resourceMapping = (ResourceMapping) this.interactionUnit.getEntityContext()
                    .getMapping(interactionUnit.getId()); // Assumption: mapping id == id of interaction unit
            List<ResourceAttribute> attributes = resourceMapping.getAttributes();
            List<FormItem> items = new ArrayList<FormItem>(attributes.size());
            for (ResourceAttribute attribute : attributes)
            {
                String label = attribute.getLabel() != null ? attribute.getLabel() : attribute.getName();
                TextItem ti = new TextItem(attribute.getName(), label);
                items.add(ti);
            }
            form.setFields(items.toArray(new FormItem[]{}));
        }

        @Override
        public void add(final ReificationWidget widget, final InteractionUnit interactionUnit,
                final InteractionUnit parent)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public Widget asWidget()
        {
            return form.asWidget();
        }
    }
}
