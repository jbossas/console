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
package org.jboss.as.console.client.shared.subsys.deploymentscanner;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.viewframework.AttributeMetadata;
import org.jboss.as.console.client.shared.viewframework.EntityAttributes;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.FormItemFactories.CheckBoxItemFactory;
import org.jboss.as.console.client.shared.viewframework.FormItemFactories.NumberBoxItemFactory;
import org.jboss.as.console.client.shared.viewframework.FormItemFactories.TextBoxItemFactory;
import org.jboss.as.console.client.shared.viewframework.FormItemFactories.TextItemFactory;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.shared.viewframework.SubsystemOpFactory;

import org.jboss.dmr.client.ModelType;


/**
 * The Presenter for Deployment Scanners
 * @author Stan Silvert
 * @date 9/15/11
 */
public class ScannerPresenter extends Presenter<ScannerPresenter.MyView, ScannerPresenter.MyProxy> {

    private SubsystemOpFactory opFactory = new SubsystemOpFactory("deployment-scanner", "scanner");
    private final PlaceManager placeManager;
    private RevealStrategy revealStrategy;
    private EntityToDmrBridge bridge;

    @ProxyCodeSplit
    @NameToken(NameTokens.ScannerPresenter)
    public interface MyProxy extends Proxy<ScannerPresenter>, Place {
    }

    public interface MyView extends FrameworkView {
    }

    @Inject
    public ScannerPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory beanFactory, RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.revealStrategy = revealStrategy;
        this.bridge = new ScannerBridge(dispatcher, getView(), makeAttributes(), opFactory, beanFactory);
        view.setEntityToDmrBridge(this.bridge);
    }

    @Override
    protected void onBind() {
        super.onBind();
    }

    @Override
    protected void onReset() {
        super.onReset();
        bridge.loadEntities(null);
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }


    private EntityAttributes makeAttributes() {
        List<AttributeMetadata> attribs = new ArrayList<AttributeMetadata>();

        attribs.add(new AttributeMetadata("name", "name", ModelType.STRING, "", new TextBoxItemFactory(), new TextItemFactory(), Console.CONSTANTS.common_label_name(), true));
        attribs.add(new AttributeMetadata("path", "path", ModelType.STRING, "deployments", new TextBoxItemFactory(), Console.CONSTANTS.common_label_path(), true));
        attribs.add(new AttributeMetadata("relativeTo", "relative-to", ModelType.STRING, "jboss.server.base.dir", new TextBoxItemFactory(), Console.CONSTANTS.subsys_deploymentscanner_relativeTo(), true));
        attribs.add(new AttributeMetadata("enabled", "scan-enabled", ModelType.BOOLEAN, Boolean.FALSE, new CheckBoxItemFactory(), Console.CONSTANTS.subsys_deploymentscanner_scanEnabled(), false));
        attribs.add(new AttributeMetadata("scanInterval", "scan-interval", ModelType.INT, new Integer(5000), new NumberBoxItemFactory(), Console.CONSTANTS.subsys_deploymentscanner_scanInterval(), false));
        attribs.add(new AttributeMetadata("autoDeployZipped", "auto-deploy-zipped", ModelType.BOOLEAN, Boolean.TRUE, new CheckBoxItemFactory(), Console.CONSTANTS.subsys_deploymentscanner_autoDeployZipped(), false));
        attribs.add(new AttributeMetadata("autoDeployExploded", "auto-deploy-exploded", ModelType.BOOLEAN, Boolean.FALSE, new CheckBoxItemFactory(), Console.CONSTANTS.subsys_deploymentscanner_autoDeployExploded(), false));
        attribs.add(new AttributeMetadata("deploymentTimeout", "deployment-timeout", ModelType.LONG, new Long(60), new NumberBoxItemFactory(), Console.CONSTANTS.subsys_deploymentscanner_deploymentTimeout(), false));

        return new EntityAttributes(attribs);
    }
    
}
