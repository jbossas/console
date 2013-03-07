package org.jboss.as.console.client.tools.mbui.workbench.repository;

import static org.jboss.mbui.model.structure.TemporalOperator.Choice;
import static org.jboss.mbui.model.structure.TemporalOperator.Concurrency;
import static org.jboss.mbui.model.structure.TemporalOperator.Deactivation;

import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.mapping.Mapping;
import org.jboss.mbui.model.mapping.as7.DMRMapping;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.QName;
import org.jboss.mbui.model.structure.Select;
import org.jboss.mbui.model.structure.Trigger;
import org.jboss.mbui.model.structure.as7.Form;
import org.jboss.mbui.model.structure.as7.ToolStrip;
import org.jboss.mbui.model.structure.impl.Builder;

/**
 * @author Harald Pehl
 * @date 03/07/2013
 */
public class SecurityDomainsSample implements Sample
{
    private final Dialog dialog;

    public SecurityDomainsSample()
    {
        this.dialog = build();
    }

    @Override
    public String getName()
    {
        return "Security Domains";
    }

    @Override
    public Dialog getDialog()
    {
        return this.dialog;
    }

    private Dialog build()
    {
        String namespace = "org.jboss.security.domain";

        // Mappings
        DMRMapping securityDomainsCollection = new DMRMapping(namespace)
                .setAddress("/{selected.profile}/subsystem=security/security-domain=*");

        // maps to a specific security domain
        DMRMapping singleSecurityDomain = new DMRMapping(namespace)
                .setAddress("/{selected.profile}/subsystem=security/security-domain={selected.entity}");

        Mapping tableMapping = new DMRMapping(namespace)
                .addAttributes("entity.key");

        Mapping attributesMapping = new DMRMapping(namespace)
                .addAttributes("entity.key", "cache-type");

        // Interaction units
        InteractionUnit root = new Builder()
                .start(new Container(namespace, "securityDomains", null, Deactivation))
                    .mappedBy(securityDomainsCollection)

                    // The front "page"
                    .start(new Container(namespace, "frontpage", "Security Domains", Concurrency))
                        .start(new ToolStrip(namespace, "tools", "Tools"))
                            .mappedBy(singleSecurityDomain)
                            .add(new Trigger(
                                    QName.valueOf(namespace + ":add"),
                                    QName.valueOf("org.jboss.as:resource-operation#add"),
                                    "Add"))
                                .mappedBy(securityDomainsCollection)
                            .add(new Trigger(
                                    QName.valueOf(namespace + ":remove"),
                                    QName.valueOf("org.jboss.as:resource-operation#remove"),
                                    "Remove"))
                        .end()

                        .add(new Select(namespace, "list", "List"))
                            .mappedBy(tableMapping)

                        .start(new Container(namespace, "details", "Details", Choice))
                            .mappedBy(singleSecurityDomain)
                            .add(new Form(namespace, "details#attributes", "Attributes"))
                                .mappedBy(attributesMapping)
                        .end()
                    .end()

                    // The actual pages
                    .start(new Container(namespace, "pages", "Pages", Choice))

                        // Authentication
                        .start(new Container(namespace + ".authentication", "authentication", "Authentication", Concurrency))
                            .start(new ToolStrip(namespace + ".authentication", "tools", "Tools"))
                                .add(new Trigger(
                                        QName.valueOf(namespace + ".authentication:add"),
                                        QName.valueOf("org.jboss.as:resource-operation#add"),
                                        "Add"))
                                .add(new Trigger(
                                        QName.valueOf(namespace + ".authentication:remove"),
                                        QName.valueOf("org.jboss.as:resource-operation#remove"),
                                        "Remove"))
                            .end()
                            .add(new Select(namespace + ".authentication", "loginModules", "Login Modules"))
                            .start(new Container(namespace + ".authentication", "details", "Details", Choice))
                                .add(new Form(namespace + ".authentication", "details#basicAttributers", "Attributes"))
                                .add(new Select(namespace + ".authentication", "moduleOptions", "Module Options"))
                            .end()
                        .end()
                                
                        // Authorization
                        .start(new Container(namespace + ".authorization", "authorization", "Authorization", Concurrency))
                            .start(new ToolStrip(namespace + ".authorization", "tools", "Tools"))
                                .add(new Trigger(
                                        QName.valueOf(namespace + ".authorization:add"),
                                        QName.valueOf("org.jboss.as:resource-operation#add"),
                                        "Add"))
                                .add(new Trigger(
                                        QName.valueOf(namespace + ".authorization:remove"),
                                        QName.valueOf("org.jboss.as:resource-operation#remove"),
                                        "Remove"))
                            .end()
                            .add(new Select(namespace + ".authorization", "policies", "Policies"))
                            .start(new Container(namespace + ".authorization", "details", "Details", Choice))
                                .add(new Form(namespace + ".authorization", "details#basicAttributers", "Attributes"))
                                .add(new Select(namespace + ".authorization", "moduleOptions", "Module Options"))
                            .end()
                        .end()

                        // Mapping
                        .start(new Container(namespace + ".mapping", "mapping", "Mapping", Concurrency))
                            .start(new ToolStrip(namespace + ".mapping", "tools", "Tools"))
                                .add(new Trigger(
                                        QName.valueOf(namespace + ".mapping:add"),
                                        QName.valueOf("org.jboss.as:resource-operation#add"),
                                        "Add"))
                                .add(new Trigger(
                                        QName.valueOf(namespace + ".mapping:remove"),
                                        QName.valueOf("org.jboss.as:resource-operation#remove"),
                                        "Remove"))
                            .end()
                            .add(new Select(namespace + ".mapping", "modules", "Modules"))
                            .start(new Container(namespace, "details", "Details", Choice))
                                .add(new Form(namespace, "details#basicAttributers", "Attributes"))
                                .add(new Form(namespace, "details#moduleAttributers", "Module Options"))
                            .end()
                        .end()
                    .end()
                .end()
                .build();

        Dialog dialog = new Dialog(QName.valueOf("org.jboss.as7:security"), root);
        return dialog;
    }
}
