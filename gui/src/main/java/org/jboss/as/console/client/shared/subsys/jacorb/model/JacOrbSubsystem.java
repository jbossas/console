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
package org.jboss.as.console.client.shared.subsys.jacorb.model;

import java.util.List;

import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.viewframework.HasProperties;
import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * @author David Bosschaert
 */
@Address("/subsystem=jacorb")
public interface JacOrbSubsystem extends NamedEntity, HasProperties {
    @Override
    @Binding(detypedName="name", key=true)
    @FormItem(defaultValue="",
              localLabel="common_label_name",
              required=true,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="TEXT_BOX")
    public String getName();
    @Override
    public void setName(String name);

    @Binding(detypedName="print-version")
    @FormItem(defaultValue="off",
              label="Print Version")
    public String getPrintVersion();
    public void setPrintVersion(String value);

    @Binding(detypedName="use-imr")
    @FormItem(defaultValue="off",
              label="Use IMR")
    public String getUseIMR();
    public void setUseIMR(String value);

    @Binding(detypedName="use-bom")
    @FormItem(defaultValue="off",
              label="Use GIOP 1.2 BOMs",
              subgroup="Encoding")
    public String getUseBOM();
    public void setUseBOM(String value);

    @Binding(detypedName="cache-typecodes")
    @FormItem(defaultValue="off",
              label="Cache Typecodes",
              subgroup="Caching")
    public String getCacheTypecodes();
    public void setCacheTypecodes(String value);

    @Binding(detypedName="cache-poa-names")
    @FormItem(defaultValue="off",
              label="Cache POA Names",
              subgroup="Caching")
    public String getCachePOANames();
    public void setCachePOANames(String value);

    @Binding(detypedName="giop-minor-version")
    @FormItem(defaultValue="2",
              label="GIOP Minor Version",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Encoding")
    public int getGiopMinorVersion();
    public void setGiopMinorVersion(int value);

    @Binding(detypedName="retries")
    @FormItem(defaultValue="5",
              label="Retries",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Connections and Sockets")
    public int getRetries();
    public void setRetries(int value);

    @Binding(detypedName="retry-interval")
    @FormItem(defaultValue="500",
              label="Retry Interval",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Connections and Sockets")
    public int getRetryInterval();
    public void setRetryInterval(int value);

    @Binding(detypedName="client-timeout")
    @FormItem(defaultValue="0",
              label="Client Timeout",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Connections and Sockets")
    public int getClientTimeout();
    public void setClientTimeout(int value);

    @Binding(detypedName="server-timeout")
    @FormItem(defaultValue="0",
              label="Server Timeout",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Connections and Sockets")
    public int getServerTimeout();
    public void setServerTimeout(int value);

    @Binding(detypedName="max-server-connections")
    @FormItem(defaultValue="2147483647",
              label="Maximum Server Connections",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Connections and Sockets")
    public int getMaxServerConnections();
    public void setMaxServerConnections(int value);

    @Binding(detypedName="max-managed-buf-size")
    @FormItem(defaultValue="24",
              label="Maximum Buffer Size",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Buffers")
    public int getMaxManagedBufSize();
    public void setMaxManagedBufSize(int value);

    @Binding(detypedName="outbuf-size")
    @FormItem(defaultValue="2048",
              label="Outgoing Buffer Size",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Buffers")
    public int getOutbufSize();
    public void setOutbufSize(int value);

    @Binding(detypedName="outbuf-cache-timeout")
    @FormItem(defaultValue="-1",
              label="Outgoing Buffer Cache Timeout",
              formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE",
              formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE",
              subgroup="Buffers")
    public int getOutbufCacheTimeout();
    public void setOutbufCacheTimeout(int value);

    @Binding(detypedName="codebase")
    @FormItem(defaultValue="on",
    		  label="Codebase Interceptor",
              subgroup="Interceptors")
    public String getCodebase();
    public void setCodebase(String value);

    @Binding(detypedName="security")
    @FormItem(defaultValue="off",
              label="Security Interceptors",
              subgroup="Interceptors")
    public String getSecurity();
    public void setSecurity(String value);

    @Binding(detypedName="transactions")
    @FormItem(defaultValue="off",
              label="Transaction Interceptors",
              subgroup="Interceptors")
    public String getTransactions();
    public void setTransactions(String value);

    @Binding(detypedName="monitoring")
    @FormItem(defaultValue="off",
              label="Monitoring GUI")
    public String getMonitoring();
    public void setMonitoring(String value);

    @Binding(detypedName="queue-wait")
    @FormItem(defaultValue="off",
              label="Queue Waiting",
              subgroup="Queue")
    public String getQueueWait();
    public void setQueueWait(String value);

    @Binding(detypedName="queue-min")
    @FormItem(defaultValue="10",
              label="Queue Minimum",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Queue")
    public int getQueueMin();
    public void setQueueMin(int value);

    @Binding(detypedName="queue-max")
    @FormItem(defaultValue="100",
              label="Queue Maximum",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Queue")
    public int getQueueMax();
    public void setQueueMax(int value);

    @Binding(detypedName="pool-size")
    @FormItem(defaultValue="5",
              label="Thread Pool Size",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Thread Pool")
    public int getPoolSize();
    public void setPoolSize(int value);

    @Binding(detypedName="max-threads")
    @FormItem(defaultValue="32",
    		  label="Max Pool Threads",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Thread Pool")
    public int getMaxThreads();
    public void setMaxThreads(int value);

    @Binding(detypedName="root-context")
    @FormItem(defaultValue="JBoss/Naming/root",
              label="Naming Service Root Context")
    public String getRootContext();
    public void setRootContext(String value);

    @Binding(detypedName="export-corbaloc")
    @FormItem(defaultValue="on",
              label="Export Root Context Corbaloc")
    public String getExportCorbaloc();
    public void setExportCorbaloc(String value);

    @Binding(detypedName="sun")
    @FormItem(defaultValue="on",
              label="Sun ORB Interoperability",
              subgroup="Interoperability")
    public String getSun();
    public void setSun(String value);

    @Binding(detypedName="comet")
    @FormItem(defaultValue="off",
              label="Comet ORB Interoperability",
              subgroup="Interoperability")
    public String getComet();
    public void setComet(String value);

    @Binding(detypedName="iona")
    @FormItem(defaultValue="off",
              label="IONA ORB Interoperability",
              subgroup="Interoperability")
    public String getIona();
    public void setIona(String value);

    @Binding(detypedName="chunk-custom-rmi-valuetypes")
    @FormItem(defaultValue="on",
              label="Chunk Custom RMI Value Types",
              subgroup="Encoding")
    public String getChunkCustomRMIValueTypes();
    public void setChunkCustomRMIValueTypes(String value);

    @Binding(detypedName="lax-boolean-encoding")
    @FormItem(defaultValue="off",
              label="Lax Boolean Encoding",
              subgroup="Encoding")
    public String getLaxBooleanEncoding();
    public void setLaxBooleanEncoding(String value);

    @Binding(detypedName="indirection-encoding-disable")
    @FormItem(defaultValue="off",
              label="Indirection Encoding Disabled",
              subgroup="Encoding")
    public String getIndirectionEncodingDisable();
    public void setIndirectionEncodingDisable(String value);

    @Binding(detypedName="strict-check-on-tc-creation")
    @FormItem(defaultValue="off",
              label="Strict TC Creation Check",
              subgroup="Encoding")
    public String getStrictCheckOnTCCreation();
    public void setStrictCheckOnTCCreation(String value);

    @Binding(detypedName="support-ssl")
    @FormItem(defaultValue="off",
              label="Support SSL",
              subgroup="SSL")
    public String getSupportSSL();
    public void setSupportSSL(String value);

    @Binding(detypedName="add-component-via-interceptor")
    @FormItem(defaultValue="on",
              label="SSL Components via Interceptor",
              subgroup="SSL")
    public String getAddComponentViaInterceptor();
    public void setAddComponentViaInterceptor(String value);

    @Binding(detypedName="client-supports")
    @FormItem(defaultValue="60",
              label="SSL Client Support",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="SSL")
    public int getClientSupports();
    public void setClientSupports(int value);

    @Binding(detypedName="client-requires")
    @FormItem(defaultValue="0",
              label="SSL Client Requirement",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="SSL")
    public int getClientRequires();
    public void setClientRequires(int value);

    @Binding(detypedName="server-supports")
    @FormItem(defaultValue="60",
              label="SSL Server Support",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="SSL")
    public int getServerSupports();
    public void setServerSupports(int value);

    @Binding(detypedName="server-requires")
    @FormItem(defaultValue="0",
              label="SSL Server Requirement",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="SSL")
    public int getServerRequires();
    public void setServerRequires(int value);

    @Binding(detypedName="use-domain-socket-factory")
    @FormItem(defaultValue="off",
              label="Use Domain Socket Factory",
              subgroup="Connections and Sockets")
    public String getUseDomainSocketFactory();
    public void setUseDomainSocketFactory(String value);

    @Binding(detypedName="use-domain-server-socket-factory")
    @FormItem(defaultValue="off",
              label="Use Domain Server Socket Factory",
              subgroup="Connections and Sockets")
    public String getUseDomainServerSocketFactory();
    public void setUseDomainServerSocketFactory(String value);

    @Override
    @Binding(detypedName="properties",
             listType="org.jboss.as.console.client.shared.properties.PropertyRecord")
    @FormItem(label="Properties",
              formItemTypeForEdit="PROPERTY_EDITOR",
              formItemTypeForAdd="PROPERTY_EDITOR",
              tabName="CUSTOM")
    List<PropertyRecord> getProperties();
    void setProperties(List<PropertyRecord> properties);
}
