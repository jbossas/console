package org.jboss.as.console.spi;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/27/12
 */
public class TemplateProcessor {

    public void process(String name, Map<String, Object> model, OutputStream output) {

        try
        {
            Configuration config = new Configuration();
            config.setClassForTemplateLoading(getClass(), "");
            config.setObjectWrapper(new DefaultObjectWrapper());

            Template templateEngine = config.getTemplate(name);
            templateEngine.process(model, new PrintWriter(output));

        }
        catch(Throwable t)
        {
            throw new RuntimeException("Error processing template: "+t.getClass().getName(), t);
        }


    }
}
