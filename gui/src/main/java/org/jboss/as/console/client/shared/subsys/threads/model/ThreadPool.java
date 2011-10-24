/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.as.console.client.shared.subsys.threads.model;

import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * Interface common to all Thread Pools
 *
 * @author ssilvert
 */
public interface ThreadPool extends NamedEntity {

    Integer getMaxThreadsCount();

    Integer getMaxThreadsPerCPU();
    
}
