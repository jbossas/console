package org.jboss.as.console.client.shared.subsys.ws;

import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.shared.subsys.jca.model.DomainDriverStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.DriverStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.StandaloneDriverStrategy;

import javax.inject.Inject;

/**
 * @author Heiko Braun
 * @date 6/10/11
 */
public class EndpointRegistry {

    private ApplicationProperties bootstrap;
    private EndpointStrategy chosenStrategy;

    @Inject
    public EndpointRegistry(
            ApplicationProperties bootstrap,
            DomainEndpointStrategy domainStrategy,
            StandaloneEndpointStrategy standaloneStrategy) {
        this.bootstrap = bootstrap;
        this.chosenStrategy = bootstrap.isStandalone() ?  standaloneStrategy : domainStrategy;
    }

    public EndpointStrategy create() {
       return chosenStrategy;
    }
}