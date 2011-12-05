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
              formItemTypeForAdd="TEXT_BOX",
              tabName="common_label_attributes")
    public String getName();
    @Override
    public void setName(String name);

    @Binding(detypedName="print-version")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_printVersion",
              tabName="common_label_attributes")
    public String getPrintVersion();
    public void setPrintVersion(String value);

    @Binding(detypedName="use-imr")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_useIMR",
              tabName="common_label_attributes")
    public String getUseIMR();
    public void setUseIMR(String value);

    @Binding(detypedName="use-bom")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_useBOM",
              subgroup="Encoding",
              tabName="subsys_jacorb_protocol")
    public String getUseBOM();
    public void setUseBOM(String value);

    @Binding(detypedName="cache-typecodes")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_cacheTypecodes",
              subgroup="Caching",
              tabName="subsys_jacorb_protocol")
    public String getCacheTypecodes();
    public void setCacheTypecodes(String value);

    @Binding(detypedName="cache-poa-names")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_cachePOANames",
              subgroup="Caching",
              tabName="subsys_jacorb_protocol")
    public String getCachePOANames();
    public void setCachePOANames(String value);

    @Binding(detypedName="giop-minor-version")
    @FormItem(defaultValue="2",
              localLabel="subsys_jacorb_GIOPMinorVersion",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Encoding",
              tabName="subsys_jacorb_protocol")
    public int getGiopMinorVersion();
    public void setGiopMinorVersion(int value);

    @Binding(detypedName="retries")
    @FormItem(defaultValue="5",
              localLabel="subsys_jacorb_retries",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Connections and Sockets",
              tabName="subsys_jacorb_network")
    public int getRetries();
    public void setRetries(int value);

    @Binding(detypedName="retry-interval")
    @FormItem(defaultValue="500",
              localLabel="subsys_jacorb_retryInterval",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Connections and Sockets",
              tabName="subsys_jacorb_network")
    public int getRetryInterval();
    public void setRetryInterval(int value);

    @Binding(detypedName="client-timeout")
    @FormItem(defaultValue="0",
              localLabel="subsys_jacorb_clientTimeout",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Connections and Sockets",
              tabName="subsys_jacorb_network")
    public int getClientTimeout();
    public void setClientTimeout(int value);

    @Binding(detypedName="server-timeout")
    @FormItem(defaultValue="0",
              localLabel="subsys_jacorb_serverTimeout",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Connections and Sockets",
              tabName="subsys_jacorb_network")
    public int getServerTimeout();
    public void setServerTimeout(int value);

    @Binding(detypedName="max-server-connections")
    @FormItem(defaultValue="2147483647",
              localLabel="subsys_jacorb_maxServerConnections",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Connections and Sockets",
              tabName="subsys_jacorb_network")
    public int getMaxServerConnections();
    public void setMaxServerConnections(int value);

    @Binding(detypedName="max-managed-buf-size")
    @FormItem(defaultValue="24",
              localLabel="subsys_jacorb_maxManagedBufSize",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Buffers",
              tabName="subsys_jacorb_network")
    public int getMaxManagedBufSize();
    public void setMaxManagedBufSize(int value);

    @Binding(detypedName="outbuf-size")
    @FormItem(defaultValue="2048",
              localLabel="subsys_jacorb_outbufSize",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              subgroup="Buffers",
              tabName="subsys_jacorb_network")
    public int getOutbufSize();
    public void setOutbufSize(int value);

    @Binding(detypedName="outbuf-cache-timeout")
    @FormItem(defaultValue="-1",
              localLabel="subsys_jacorb_outbufCacheTimeout",
              formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE",
              formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE",
              subgroup="Buffers",
              tabName="subsys_jacorb_network")
    public int getOutbufCacheTimeout();
    public void setOutbufCacheTimeout(int value);

    @Binding(detypedName="codebase")
    @FormItem(defaultValue="on",
    		  localLabel="subsys_jacorb_codebase",
              tabName="subsys_jacorb_protocol")
    public String getCodebase();
    public void setCodebase(String value);

    @Binding(detypedName="security")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_security",
              tabName="subsys_jacorb_security")
    public String getSecurity();
    public void setSecurity(String value);

    @Binding(detypedName="transactions")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_transactions",
              tabName="subsys_jacorb_protocol")
    public String getTransactions();
    public void setTransactions(String value);

    @Binding(detypedName="monitoring")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_monitoring",
              tabName="common_label_attributes")
    public String getMonitoring();
    public void setMonitoring(String value);

    @Binding(detypedName="queue-wait")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_queueWait",
              tabName="subsys_jacorb_queues")
    public String getQueueWait();
    public void setQueueWait(String value);

    @Binding(detypedName="queue-min")
    @FormItem(defaultValue="10",
              localLabel="subsys_jacorb_queueMin",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              tabName="subsys_jacorb_queues")
    public int getQueueMin();
    public void setQueueMin(int value);

    @Binding(detypedName="queue-max")
    @FormItem(defaultValue="100",
              localLabel="subsys_jacorb_queueMax",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              tabName="subsys_jacorb_queues")
    public int getQueueMax();
    public void setQueueMax(int value);

    @Binding(detypedName="pool-size")
    @FormItem(defaultValue="5",
              localLabel="subsys_jacorb_poolSize",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              tabName="subsys_jacorb_threadPools")
    public int getPoolSize();
    public void setPoolSize(int value);

    @Binding(detypedName="max-threads")
    @FormItem(defaultValue="32",
    		  localLabel="subsys_jacorb_maxThreads",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              tabName="subsys_jacorb_threadPools")
    public int getMaxThreads();
    public void setMaxThreads(int value);

    @Binding(detypedName="root-context")
    @FormItem(defaultValue="JBoss/Naming/root",
              localLabel="subsys_jacorb_rootContext",
              tabName="common_label_attributes")
    public String getRootContext();
    public void setRootContext(String value);

    @Binding(detypedName="export-corbaloc")
    @FormItem(defaultValue="on",
              localLabel="subsys_jacorb_exportCorbaloc",
              tabName="common_label_attributes")
    public String getExportCorbaloc();
    public void setExportCorbaloc(String value);

    @Binding(detypedName="sun")
    @FormItem(defaultValue="on",
              localLabel="subsys_jacorb_sun",
              tabName="subsys_jacorb_interoperability")
    public String getSun();
    public void setSun(String value);

    @Binding(detypedName="comet")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_comet",
              tabName="subsys_jacorb_interoperability")
    public String getComet();
    public void setComet(String value);

    @Binding(detypedName="iona")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_iona",
              tabName="subsys_jacorb_interoperability")
    public String getIona();
    public void setIona(String value);

    @Binding(detypedName="chunk-custom-rmi-valuetypes")
    @FormItem(defaultValue="on",
              localLabel="subsys_jacorb_chunkCustomRMIValuetypes",
              subgroup="Encoding",
              tabName="subsys_jacorb_protocol")
    public String getChunkCustomRMIValueTypes();
    public void setChunkCustomRMIValueTypes(String value);

    @Binding(detypedName="lax-boolean-encoding")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_laxBooleanEncoding",
              subgroup="Encoding",
              tabName="subsys_jacorb_protocol")
    public String getLaxBooleanEncoding();
    public void setLaxBooleanEncoding(String value);

    @Binding(detypedName="indirection-encoding-disable")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_indirectionEncodingDisable",
              subgroup="Encoding",
              tabName="subsys_jacorb_protocol")
    public String getIndirectionEncodingDisable();
    public void setIndirectionEncodingDisable(String value);

    @Binding(detypedName="strict-check-on-tc-creation")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_strictCheckOnTCCreation",
              subgroup="Encoding",
              tabName="subsys_jacorb_protocol")
    public String getStrictCheckOnTCCreation();
    public void setStrictCheckOnTCCreation(String value);

    @Binding(detypedName="support-ssl")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_supportSSL",
              tabName="subsys_jacorb_security")
    public String getSupportSSL();
    public void setSupportSSL(String value);

    @Binding(detypedName="add-component-via-interceptor")
    @FormItem(defaultValue="on",
              localLabel="subsys_jacorb_addComponentViaInterceptor",
              tabName="subsys_jacorb_security")
    public String getAddComponentViaInterceptor();
    public void setAddComponentViaInterceptor(String value);

    @Binding(detypedName="client-supports")
    @FormItem(defaultValue="60",
              localLabel="subsys_jacorb_clientSupports",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              tabName="subsys_jacorb_security")
    public int getClientSupports();
    public void setClientSupports(int value);

    @Binding(detypedName="client-requires")
    @FormItem(defaultValue="0",
              localLabel="subsys_jacorb_clientRequires",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              tabName="subsys_jacorb_security")
    public int getClientRequires();
    public void setClientRequires(int value);

    @Binding(detypedName="server-supports")
    @FormItem(defaultValue="60",
              localLabel="subsys_jacorb_serverSupports",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              tabName="subsys_jacorb_security")
    public int getServerSupports();
    public void setServerSupports(int value);

    @Binding(detypedName="server-requires")
    @FormItem(defaultValue="0",
              localLabel="subsys_jacorb_serverRequires",
              formItemTypeForAdd="NUMBER_BOX",
              formItemTypeForEdit="NUMBER_BOX",
              tabName="subsys_jacorb_security")
    public int getServerRequires();
    public void setServerRequires(int value);

    @Binding(detypedName="use-domain-socket-factory")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_useDomainSocketFactory",
              subgroup="Connections and Sockets",
              tabName="subsys_jacorb_network")
    public String getUseDomainSocketFactory();
    public void setUseDomainSocketFactory(String value);

    @Binding(detypedName="use-domain-server-socket-factory")
    @FormItem(defaultValue="off",
              localLabel="subsys_jacorb_useDomainServerSocketFactory",
              subgroup="Connections and Sockets",
              tabName="subsys_jacorb_network")
    public String getUseDomainServerSocketFactory();
    public void setUseDomainServerSocketFactory(String value);

    @Override
    @Binding(detypedName="properties",
             listType="org.jboss.as.console.client.shared.properties.PropertyRecord")
    @FormItem(localLabel="common_label_properties",
              formItemTypeForEdit="PROPERTY_EDITOR",
              formItemTypeForAdd="PROPERTY_EDITOR",
              tabName="CUSTOM")
    List<PropertyRecord> getProperties();
    void setProperties(List<PropertyRecord> properties);
}
