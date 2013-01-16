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
package org.jboss.as.console.client.tools.mbui.workbench.gin;

import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import org.jboss.mbui.gui.reification.BuildUserInterfaceStep;
import org.jboss.mbui.gui.reification.ReadResourceDescriptionStep;
import org.jboss.mbui.gui.reification.ReificationPipeline;
import org.jboss.as.console.client.tools.mbui.workbench.ApplicationPresenter;
import org.jboss.as.console.client.tools.mbui.workbench.ApplicationView;
import org.jboss.as.console.client.tools.mbui.workbench.FooterPresenter;
import org.jboss.as.console.client.tools.mbui.workbench.FooterView;
import org.jboss.as.console.client.tools.mbui.workbench.HeaderPresenter;
import org.jboss.as.console.client.tools.mbui.workbench.HeaderView;
import org.jboss.as.console.client.tools.mbui.workbench.context.ContextPresenter;
import org.jboss.as.console.client.tools.mbui.workbench.context.ContextView;
import org.jboss.as.console.client.tools.mbui.workbench.editor.PreviewPresenter;
import org.jboss.as.console.client.tools.mbui.workbench.editor.PreviewView;
import org.jboss.as.console.client.tools.mbui.workbench.repository.RepositoryPresenter;
import org.jboss.as.console.client.tools.mbui.workbench.repository.RepositoryView;
import org.jboss.as.console.client.tools.mbui.workbench.repository.SampleRepository;
import org.jboss.as.console.spi.GinExtensionBinding;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
@GinExtensionBinding
public class WorkbenchModule extends AbstractPresenterModule
{
    @Override
    protected void configure()
    {
        // Presenters (a-z)
        bindPresenter(ApplicationPresenter.class, ApplicationPresenter.MyView.class, ApplicationView.class,
                ApplicationPresenter.MyProxy.class);
        bindPresenterWidget(ContextPresenter.class, ContextPresenter.MyView.class, ContextView.class);
        bindPresenterWidget(FooterPresenter.class, FooterPresenter.MyView.class, FooterView.class);
        bindPresenterWidget(HeaderPresenter.class, HeaderPresenter.MyView.class, HeaderView.class);
        bindPresenter(PreviewPresenter.class, PreviewPresenter.MyView.class, PreviewView.class,
                PreviewPresenter.MyProxy.class);
        bindPresenterWidget(RepositoryPresenter.class, RepositoryPresenter.MyView.class, RepositoryView.class);

        // Application
        bind(ReificationPipeline.class).in(Singleton.class);
        bind(BuildUserInterfaceStep.class).in(Singleton.class);
        bind(ReadResourceDescriptionStep.class).in(Singleton.class);
        bind(SampleRepository.class).in(Singleton.class);
    }
}
