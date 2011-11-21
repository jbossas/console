package org.jboss.as.console.client.shared.viewframework;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Heiko Braun
 * @date 11/21/11
 */
public interface SingleEntityView<T> extends IsWidget {

    void updatedEntity(T entity);

    String getTitle();
}
