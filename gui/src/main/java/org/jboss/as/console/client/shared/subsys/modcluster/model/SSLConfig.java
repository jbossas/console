package org.jboss.as.console.client.shared.subsys.modcluster.model;

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 2/28/12
 */
public interface SSLConfig {

    @Binding(detypedName="key-alias")
    public String getKeyAlias();
    public void setKeyAlias(String alias);

    public String getPassword();
    public void setPassword(String pw);

    @Binding(detypedName="ca-certificate-file")
    public String getCertFile();
    public void setCertFile(String certFile);

    @Binding(detypedName="certificate-key-file")
    public String getKeyFile();
    public void setKeyFile(String keyFile);

    @Binding(detypedName="cipher-suite")
    public String getCipherSuite();
    public void setCipherSuite(String cipherSuite);

    @Binding(detypedName="ca-revocation-url")
    public String getRevocationUrl();
    public void setRevocationUrl(String url);

    public String getProtocol();
    public void setProtocol(String protocol);



}
