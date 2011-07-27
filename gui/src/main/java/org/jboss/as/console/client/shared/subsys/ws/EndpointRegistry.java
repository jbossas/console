package org.jboss.as.console.client.shared.subsys.ws;

import org.jboss.as.console.client.core.ApplicationProperties;

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