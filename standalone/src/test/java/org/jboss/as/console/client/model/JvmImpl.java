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

package org.jboss.as.console.client.model;

import org.jboss.as.console.client.domain.model.Jvm;

/**
 * @author Heiko Braun
 * @date 4/27/11
 */
public class JvmImpl implements Jvm {
    
    String name;
    boolean isDebugEnabled;
    String debugOptions;
    String heapSize;
    String maxHeapSize;
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    @Override
    public void setDebugEnabled(boolean b) {
        this.isDebugEnabled = b;
    }

    @Override
    public String getDebugOptions() {
        return debugOptions;
    }

    @Override
    public void setDebugOptions(String options) {
        this.debugOptions = options;
    }

    @Override
    public String getHeapSize() {
        return heapSize;
    }

    @Override
    public void setHeapSize(String heap) {
        this.heapSize = heap;
    }

    @Override
    public String getMaxHeapSize() {
        return maxHeapSize;
    }

    @Override
    public void setMaxHeapSize(String maxHeap) {
        this.maxHeapSize = maxHeap;
    }
}
