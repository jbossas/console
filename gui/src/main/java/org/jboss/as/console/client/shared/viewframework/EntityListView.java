package org.jboss.as.console.client.shared.viewframework;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.ListDataProvider;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/21/11
 */
public interface EntityListView<T> extends IsWidget {

    ListDataProvider<T> getDataProvider();
    void updateEntityList(List<T> entityList, T lastEdited);

}
