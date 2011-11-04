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
package org.jboss.as.console.client.shared.subsys.security.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * @author David Bosschaert
 */
@Address("/subsystem=security")
public interface SecuritySubsystem {
    @Binding(detypedName="deep-copy-subject-mode")
    @FormItem(label="Deep Copy Subjects",
              required=false,
              formItemTypeForAdd="CHECK_BOX",
              formItemTypeForEdit="CHECK_BOX",
              order=10)
    boolean isDeepCopySubjects();
    void setDeepCopySubjects(boolean value);

    @Binding(detypedName="audit-manager-class-name")
    @FormItem(label="Audit Manager", required=false, order=20)
    String getAuditManagerClassName();
    void setAuditManagerClassName(String name);

    @Binding(detypedName="authentication-manager-class-name")
    @FormItem(label="Authentication Manager", required=false, order=30)
    String getAuthenticationManagerClassName();
    void setAuthenticationManagerClassName(String name);

    @Binding(detypedName="authorization-manager-class-name")
    @FormItem(label="Authorization Manager", required=false, order=40)
    String getAuthorizationManagerClassName();
    void setAuthorizationManagerClassName(String name);

    @Binding(detypedName="default-callback-handler-class-name")
    @FormItem(label="Default Callback Handler", required=false, order=50)
    String getDefaultCallbackHandlerClassName();
    void setDefaultCallbackHandlerClassName(String name);

    @Binding(detypedName="identity-trust-manager-class-name")
    @FormItem(label="Identity Trust Manager", required=false, order=60)
    String getIdentityTrustManagerClassName();
    void setIdentityTrustManagerClassName(String name);

    @Binding(detypedName="mapping-manager-class-name")
    @FormItem(label="Mapping Manager", required=false, order=70)
    String getMappingManagerClassName();
    void setMappingManagerClassName(String name);

    @Binding(detypedName="subject-factory-class-name")
    @FormItem(label="Subject Factory", required=false, order=15)
    String getSubjectFactoryClassName();
    void setSubjectFactoryClassName(String name);
}
