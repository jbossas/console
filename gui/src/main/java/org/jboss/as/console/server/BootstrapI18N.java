package org.jboss.as.console.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.PropertyResourceBundle;

/**
 * @author Heiko Braun
 * @date 2/22/12
 */
public class BootstrapI18N {

    static boolean dryRun = true;

    public static void main(String[] args) throws Exception{


        String env = System.getProperty("dryRun", "true");
        dryRun = Boolean.valueOf(env);

        System.out.println("DryRun: "+dryRun);

        File currentDir = new File("");
        File baseDir = new File(currentDir.getAbsolutePath()+"/gui/src/main/java/org/jboss/as/console/client/core");

        assert baseDir.exists();

        processFiles(baseDir, new Filter("UIConstants"));

    }

    private static void processFiles(File baseDir, Filter filter) throws Exception {
        File[] propertyFiles = baseDir.listFiles(filter);

        File source = new File(baseDir.getAbsolutePath()+"/"+filter.getPrefix()+".properties");
        System.out.println("Source: "+source);
        PropertyResourceBundle sourceBundle = new PropertyResourceBundle(
                new FileInputStream(source)
        );

        for(File target : propertyFiles)
        {
            PropertyResourceBundle targetBundle = new PropertyResourceBundle(
                    new FileInputStream(target)
            );

            System.out.println("Processing: "+target);
            List<String> remaining = match(sourceBundle, targetBundle);

            // write it back

            Collections.sort(remaining);


            if(!dryRun) target.delete();

            OutputStreamWriter writer = dryRun ? null : new OutputStreamWriter(
                    new FileOutputStream(target, false), "UTF-8"
            );

            for(String remain : remaining)
            {
                if(dryRun)
                {
                    System.out.println(remain+"="+targetBundle.getString(remain));
                }
                else
                {
                    writer.write(remain+"="+targetBundle.getString(remain));
                    writer.write("\n");
                }

            }

            if (writer!=null)
            {
                writer.flush();
                writer.close();
            }

            System.out.println("\n\n");

        }
    }

    private static List<String> match(PropertyResourceBundle sourceBundle, PropertyResourceBundle targetBundle) {

        List<String> remaining = new ArrayList<String>();

        Enumeration<String> targetKeys = targetBundle.getKeys();
        while(targetKeys.hasMoreElements())
        {
            String targetKey = targetKeys.nextElement();
            Enumeration<String> sourceKeys = sourceBundle.getKeys();
            boolean matched = false;
            while(sourceKeys.hasMoreElements())
            {
                if(sourceKeys.nextElement().equals(targetKey))
                {
                    matched = true;
                    break;
                }
            }

            if(!matched && !remaining.contains(targetKey))
                remaining.add(targetKey);

        }

        System.out.println("Source keys: "+sourceBundle.keySet().size());
        System.out.println("Target keys: "+targetBundle.keySet().size());
        System.out.println("Remaining: "+remaining.size());

        return remaining;



    }

    static class Filter implements FilenameFilter {

        String prefix;

        Filter(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }

        @Override
        public boolean accept(File file, String s) {
            return s.startsWith(prefix) && s.endsWith("_fr.properties") && s.contains("_");
        }
    }
}

