package org.jboss.as.console.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.PropertyResourceBundle;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 2/22/12
 */
public class BootstrapI18N {

    public static void main(String[] args) throws Exception{

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
            match(sourceBundle, targetBundle);
            System.out.println("\n\n");

        }
    }

    private static void match(PropertyResourceBundle sourceBundle, PropertyResourceBundle targetBundle) {

        Set<String> toBeRemoved = new HashSet<String>();
        Set<String> remaining = new HashSet<String>();

        Enumeration<String> targetKeys = targetBundle.getKeys();
        while(targetKeys.hasMoreElements())
        {
            String targetKey = targetKeys.nextElement();
            Enumeration<String> sourceKeys = sourceBundle.getKeys();
            while(sourceKeys.hasMoreElements())
            {
                if(sourceKeys.nextElement().equals(targetKey))
                {
                    toBeRemoved.add(targetKey);
                    break;
                }
            }
        }

        System.out.println("Source keys: "+sourceBundle.keySet().size());
        System.out.println("Target keys: "+targetBundle.keySet().size());
        System.out.println("Remove: "+toBeRemoved.size());

        //for(String remove : toBeRemoved)
        //    System.out.println(remove);
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
            return s.startsWith(prefix) && s.endsWith("_en.properties") && s.contains("_");
        }
    }
}

