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
package org.jboss.as.console.client.core;

import com.google.gwt.i18n.client.ConstantsWithLookup;

/**
 * @author Harald Pehl
 * @date 11/29/2012
 */
public interface EnumLabels extends ConstantsWithLookup
{
    String DeploymentData_deployment();
    String DeploymentData_subdeployment();
    String DeploymentData_ejb3();
    String DeploymentData_jpa();
    String DeploymentData_web();
    String DeploymentData_webservices();
    String DeploymentData_entityBean();
    String DeploymentData_messageDrivenBean();
    String DeploymentData_singletonBean();
    String DeploymentData_statefulSessionBean();
    String DeploymentData_statelessSessionBean();
    String DeploymentData_persistenceUnit();
    String DeploymentData_servlet();
    String DeploymentData_webserviceEndpoint();
}
