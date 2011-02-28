package org.jboss.as.console.client.shared;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public class MockSubsystemStore implements SubsystemStore {

    BeanFactory beanFactory = GWT.create(BeanFactory.class);

    static String[][] tuples = new String[][] {
            new String[]{"threads","Threads"},
            new String[]{"web","Web"},
            new String[]{"ejb","EJB"},
            new String[]{"jca","JCA"},
            new String[]{"messaging","Messaging"},
            new String[]{"tx","Transactions"},
            new String[]{"ws","Web Services"},
            new String[]{"ha","Clustering"}

    };

    @Override
    public List<SubsystemRecord> loadSubsystems() {

        List<SubsystemRecord> records = new ArrayList<SubsystemRecord>(tuples.length);

        for(String[] tuple : tuples)
        {

            SubsystemRecord rec = beanFactory.subsystem().as();
            rec.setToken(tuple[0]);
            rec.setTitle(tuple[1]);

            records.add(rec);
        }
        return records;
    }

    @Override
    public List<SubsystemRecord>  loadSubsystems(String profileName) {
        List<SubsystemRecord> records = loadSubsystems();
        List<SubsystemRecord> results = new ArrayList<SubsystemRecord>();


        if("Messaging".equals(profileName))
        {
            int i=0;
            for(SubsystemRecord rec : records)
            {
                if(i>5) break;
                results.add(rec);
                i++;
            }
        }
        else
        {
            results.addAll(records);
        }
        Log.debug("Loaded " + results.size() + " subsystems for profile '"+profileName+"'");
        return results;
    }
}
