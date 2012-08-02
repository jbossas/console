package org.jboss.as.console.client.tools;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 7/23/12
 */
public class LocalFXStorage implements FXStorage {

    private Map<String,String> delegate;

    public LocalFXStorage() {
        Storage storage = Storage.getLocalStorageIfSupported();

        if(null==storage)
        {
            Log.warn("Local storage not supported");
            this.delegate = new HashMap<String,String>();
        }
        else
        {
            this.delegate = new StorageMap(storage);
        }

    }

    @Override
    public Set<FXTemplate> loadTemplates() {
        final Set<String> keys = delegate.keySet();
        HashSet<FXTemplate> templates = new HashSet<FXTemplate>();

        for(String key : keys)
        {
            final String encoded = delegate.get(key);
            final FXTemplate template = FXTemplate.fromBase64(encoded);
            templates.add(template);
        }

        return templates;
    }

    @Override
    public FXTemplate loadTemplate(String id) {
        final String encoded = delegate.get(id);
        return FXTemplate.fromBase64(encoded);
    }

    @Override
    public void storeTemplate(FXTemplate template) {
        if(null==template.getId())
            throw new IllegalArgumentException("Templates required a unique ID!");
        delegate.put(template.getId(), template.toBase64());
    }

    @Override
    public void removeTemplate(String id) {
        delegate.remove(id);
    }
}
