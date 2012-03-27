package org.jboss.as.console.client.core.gin;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import org.jboss.as.console.client.plugins.SubsystemRegistry;
import org.jboss.as.console.client.plugins.SubsystemRegistryImpl;
import org.jboss.as.console.spi.ExtensionModule;

import javax.inject.Singleton;

/**
 * @author Heiko Braun
 * @date 3/27/12
 */
@ExtensionModule
public class SPIModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bind(SubsystemRegistry.class).to(SubsystemRegistryImpl.class).in(Singleton.class);
    }
}