package org.jboss.as.console.client.shared.subsys.modcluster;

import org.jboss.as.console.client.shared.subsys.modcluster.model.Modcluster;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 2/27/12
 */
public interface ModclusterManagement {

    void onSave(Modcluster entity, Map<String, Object> changeset);
}
