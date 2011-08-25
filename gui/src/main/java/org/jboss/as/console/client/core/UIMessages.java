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

import com.google.gwt.i18n.client.Messages;

/**
 * @author Heiko Braun
 * @author David Bosschaert
 * @date 5/2/11
 */
public interface UIMessages extends Messages {
    String changeServerStatus(String state, String name);

    String deleteServerConfig();

    String deleteServerConfigConfirm(String name);

    String common_validation_portOffsetUndefined(String errMessage);

    String common_validation_notEmptyNoSpace();

    String deleteServerGroupConfirm(String groupName);

    String deleteServerGroup();

    String deleteJVM();

    String deleteJVMConfirm();

    String removeProperty();

    String removePropertyConfirm(String key);

    String common_validation_requiredField();

    String mustBeDeployableArchive(String fieldName);

    String alreadyExists(String fieldName);

    String commmon_description_newServerGroup();

    String savedSettings();

    String restartRequired();

    String restartRequiredConfirm();

    String removeFromConfirm(String entity, String target);

    String failedToRemoveFrom(String entity, String target);

    String removedFrom(String entity, String target);

    String enableConfirm(String entity);

    String disableConfirm(String entity);

    String failedToEnable(String entity);

    String failedToDisable(String entity);

    String successEnabled(String entity);

    String successDisabled(String entity);

    String addConfirm(String entity, String target);

    String failedToAdd(String entity, String target);

    String successAdd(String entity, String target);

    String alreadyAssignedTo(String deploymentName, String serverGroup);

    String subsys_naming_failedToLoadJNDIView();

    String subsys_osgi_activationWarning();
    String subsys_osgi_frameworkPropertiesHelp();
    String subsys_osgi_removeConfigAdmin();
    String subsys_osgi_removeConfigAdminConfirm(String pid);
    String subsys_osgi_removePreloadedModule();
    String subsys_osgi_removePreloadedModuleConfirm(String id);
}
