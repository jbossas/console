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

package org.jboss.as.console.client.shared.subsys.messaging.model;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public interface SecurityPattern {

    public String getPattern() ;
    public void setPattern(String pattern);

    public String getRole() ;
    public void setRole(String role);

    public boolean isSend() ;
    public void setSend(boolean send);

    public boolean isConsume() ;
    public void setConsume(boolean consume);

    public boolean isCreateDurableQueue() ;
    public void setCreateDurableQueue(boolean createDurableQueue) ;

    public boolean isDeleteDurableQueue() ;
    public void setDeleteDurableQueue(boolean deleteDurableQueue) ;

    public boolean isCreateNonDurableQueue() ;
    public void setCreateNonDurableQueue(boolean createNonDurableQueue) ;

    public boolean isDeleteNonDurableQueue() ;
    public void setDeleteNonDurableQueue(boolean deleteNonDurableQueue) ;

    public boolean isManage() ;
    public void setManage(boolean manage);
}
