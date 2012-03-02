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

package org.jboss.as.console.client.shared.subsys.jca.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
@Address("/subsystem=datasources/data-source={0}")
public interface DataSource {

    @Binding(key = true)
    String getName();
    void setName(String name);

    @Binding(detypedName = "jndi-name")
    String getJndiName();
    void setJndiName(String name);

    boolean isEnabled();
    void setEnabled(boolean isEnabled);

    @Binding(detypedName = "pool-name")
    String getPoolName();
    void setPoolName(String name);

    // --

    @Binding(detypedName = "transaction-isolation")
    String getTransactionIsolation();
    void setTransactionIsolation(String isolationLevel);

    @Binding(detypedName = "new-connection-sql")
    String getConnectionSql();
    void setConnectionSql(String sql);

    // --

    @Binding(detypedName = "security-domain")
    String getSecurityDomain();
    void setSecurityDomain(String domain);

    @Binding(detypedName = "user-name")
    String getUsername();
    void setUsername(String user);

    String getPassword();
    void setPassword(String password);

    // regular DS attributes below

    @Binding(detypedName = "connection-url")
    String getConnectionUrl();
    void setConnectionUrl(String url);

    @Binding(detypedName = "driver-class")
    String getDriverClass();
    void setDriverClass(String driverClass);

    @Binding(detypedName = "driver-name")
    String getDriverName();
    void setDriverName(String driver);

    // --

    @Binding(detypedName = "valid-connection-checker-class-name")
    String getValidConnectionChecker();
    void setValidConnectionChecker(String name);

    @Binding(detypedName = "check-valid-connection-sql")
    String getCheckValidSql();
    void setCheckValidSql(String sql);

    @Binding(detypedName = "background-validation")
    boolean isBackgroundValidation();
    void setBackgroundValidation(boolean b);

    @Binding(detypedName = "background-validation-millis")
    Long getBackgroundValidationMillis();
    void setBackgroundValidationMillis(Long millis);

    @Binding(detypedName = "validate-on-match")
    boolean isValidateOnMatch();
    void setValidateOnMatch(boolean b);

    @Binding(detypedName = "stale-connection-checker-class-name")
    String getStaleConnectionChecker();
    void setStaleConnectionChecker(String name);

    @Binding(detypedName = "exception-sorter-class-name")
    String getExceptionSorter();
    void setExceptionSorter(String name);

    // --

    @Binding(detypedName = "prepared-statements-cache-size")
    Long getPrepareStatementCacheSize();
    void setPrepareStatementCacheSize(Long size);

    @Binding(detypedName = "share-prepared-statements")
    boolean isSharePreparedStatements();
    void setSharePreparedStatements(boolean b);

    // --

    boolean isJta();
    void setJta(boolean b);

    @Binding(detypedName = "use-ccm")
    boolean isCcm();
    void setCcm(boolean b);

    @Binding(skip = true)
    int getMajorVersion();
    void setMajorVersion(int major);

    @Binding(skip = true)
    int getMinorVersion();
    void setMinorVersion(int minor);

}
