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

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanVisitor;

/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public class AutoBeanStub<T> implements AutoBean<T> {

    private T delegate;

    @Override
    public void accept(AutoBeanVisitor visitor) {
        
    }

    public AutoBeanStub(T delegate) {
        this.delegate = delegate;
    }

    @Override
    public T as() {
        return delegate;
    }

    @Override
    public AutoBean<T> clone(boolean deep) {
        return null;  
    }

    @Override
    public AutoBeanFactory getFactory() {
        return null;  
    }

    @Override
    public <Q> Q getTag(String tagName) {
        return null;  
    }

    @Override
    public Class<T> getType() {
        return null;  
    }

    @Override
    public boolean isFrozen() {
        return false;  
    }

    @Override
    public boolean isWrapper() {
        return false;  
    }

    @Override
    public void setFrozen(boolean frozen) {
        
    }

    @Override
    public void setTag(String tagName, Object value) {
        
    }

    @Override
    public T unwrap() {
        return null;  
    }
}
