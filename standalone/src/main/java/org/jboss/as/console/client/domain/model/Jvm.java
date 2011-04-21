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

package org.jboss.as.console.client.domain.model;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 4/20/11
 */
public interface Jvm {

    @Binding(detypedName = "none", ignore = true)
    String getName();
    void setName(String name);

    @Binding(detypedName = "debug-enabled")
    boolean isDebugEnabled();
    void setDebugEnabled(boolean b);

    @Binding(detypedName = "debug-options")
    String getDebugOptions();
    void setDebugOptions(String options);

    @Binding(detypedName = "heap-size")
    String getHeapSize();
    void setHeapSize(String heap);

    @Binding(detypedName = "max-heap-size")
    String getMaxHeapSize();
    void setMaxHeapSize(String maxHeap);
}
