package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.TextResource;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.settings.ModelVersions;
import org.jboss.gwt.flow.client.Control;
import org.jboss.gwt.flow.client.Function;

import java.util.Set;

public class LoadCompatMatrix implements Function<BootstrapContext> {

    private ModelVersions modelVersions;

    public LoadCompatMatrix(ModelVersions modelVersions) {
        this.modelVersions = modelVersions;
    }

    @Override
    public void execute(Control<BootstrapContext> control) {

        TextResource compat = TextResources.INSTANCE.compat();

        JSONValue root = JSONParser.parseLenient(compat.getText());
        JSONObject versionList = root.isObject();
        Set<String> keys = versionList.keySet();
        for(String key : keys)
        {
            modelVersions.put(key, versionList.get(key).isString().stringValue());
        }

        System.out.println("Build against Core Model Version: " + modelVersions.get("core-version"));

        control.proceed();
    }

}
