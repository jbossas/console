package org.jboss.mbui.gui.reification.pipeline;

import static org.jboss.mbui.model.structure.TemporalOperator.Choice;
import static org.jboss.mbui.model.structure.TemporalOperator.Concurrency;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ReificationException;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.QName;
import org.jboss.mbui.model.structure.Select;
import org.jboss.mbui.model.structure.impl.Builder;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Harald Pehl
 * @date 03/14/2013
 */
public class UniqueIdCheckStepTest
{
    ReificationPipeline pipeline;

    @Before
    public void setUp()
    {
        pipeline = new ReificationPipeline(new UniqueIdCheckStep());
    }

    @Test
    public void uniqueIds()
    {
        String namespace = "org.jboss.sample";
        InteractionUnit root = new Builder()
                .start(new Container(namespace, "sample", "Sample", Choice))
                    .start(new Container(namespace, "tab1", "Fooo", Concurrency))
                        .add(new Select(namespace, "list", "List"))
                    .end()
                    .start(new Container(namespace, "tab2", "Bar", Concurrency))
                    .end()
                .end()
                .build();
        Dialog dialog = new Dialog(QName.valueOf(namespace + ":sample"), root);
        pipeline.execute(dialog, new Context());
    }

    @Test
    public void noneUniqueIds()
    {
        String namespace = "org.jboss.sample";
        InteractionUnit root = new Builder()
                .start(new Container(namespace, "sample", "Sample", Choice))
                    .start(new Container(namespace, "tab1", "Fooo", Concurrency))
                        .add(new Select(namespace, "list", "List1"))
                    .end()
                    .start(new Container(namespace, "tab2", "Bar", Concurrency))
                        .add(new Select(namespace, "list", "List2"))
                    .end()
                .end()
                .build();
        Dialog dialog = new Dialog(QName.valueOf(namespace + ":sample"), root);

        try
        {
            pipeline.execute(dialog, new Context());
            fail("ReificationException expected");
        }
        catch (ReificationException e)
        {
            String message = e.getMessage();
            assertTrue(message.contains("list: 2"));
        }
    }
}
