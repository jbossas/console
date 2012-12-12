package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.core.settings.ModelVersions;

import java.util.Iterator;
import java.util.Set;

public class LoadCompatMatrix extends BoostrapStep {

    private ModelVersions modelVersions;

    public LoadCompatMatrix(ModelVersions modelVersions) {
        this.modelVersions = modelVersions;
    }

    @Override
    public void execute(final Iterator<BoostrapStep> iterator, final AsyncCallback<Boolean> outcome) {

        TextResource compat = TextResources.INSTANCE.compat();

        JSONValue root = JSONParser.parseLenient(compat.getText());
        JSONObject versionList = root.isObject();
        Set<String> keys = versionList.keySet();
        for(String key : keys)
        {
            modelVersions.put(key, versionList.get(key).isString().stringValue());
        }

        System.out.println("Build against Core Model Version: " + modelVersions.get("core-version"));
        outcome.onSuccess(Boolean.TRUE);
        next(iterator, outcome);

    }

}
