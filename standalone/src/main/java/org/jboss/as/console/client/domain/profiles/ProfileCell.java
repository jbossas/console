/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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

package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.jboss.as.console.client.domain.model.ProfileRecord;

public class ProfileCell extends AbstractCell<ProfileRecord> {

    interface Template extends SafeHtmlTemplates {
        @Template("<div class=\"{0}\" style=\"outline:none;\" >- <b>{1}</b></div>")
        SafeHtml message(String cssClass, String name);
    }

    private static final Template TEMPLATE = GWT.create(Template.class);


    @Override
    public void render(
            Context context,
            ProfileRecord record,
            SafeHtmlBuilder safeHtmlBuilder)
    {

        safeHtmlBuilder.append(
                    TEMPLATE.message("none", record.getName())
        );

    }

}
