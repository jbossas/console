package org.jboss.gwt.flow.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Harald Pehl
 * @date 03/13/2013
 */
public class FlowDemo implements EntryPoint
{
    private TextArea output;

    @Override
    public void onModuleLoad()
    {
        output = new TextArea();
        output.setVisibleLines(5);
        output.setCharacterWidth(60);

        VerticalPanel btns = new VerticalPanel();
        btns.getElement().setAttribute("style", "vertical-align:center");
        btns.add(output);
        btns.add(new Button("Series", new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent clickEvent)
            {
                runSeries();
            }
        }));

        btns.add(new Button("Waterfall", new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent clickEvent)
            {
                runWaterfall();
            }
        }));

        btns.add(new Button("Parallel", new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent clickEvent)
            {

                runParallel();

            }
        }));

        btns.add(new Button("Whilst", new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent clickEvent)
            {

                runWhilst();

            }
        }));

        RootLayoutPanel.get().add(btns);
    }

    private void runSeries()
    {
        final Function first = new GenericFunction("s.1");
        final Function second = new GenericFunction("s.2");

        final Outcome genericOutcome = new Outcome()
        {
            @Override
            public void onFailure(Object o)
            {
                Window.alert("Outcome is failure");
            }

            @Override
            public void onSuccess(Object o)
            {
                Window.alert("Outcome is success: ");

            }
        };

        new Async().series(genericOutcome, first, second);
    }

    private void runWaterfall()
    {

        final Function third = new SpecificFunction("w.1");
        final Function fourth = new SpecificFunction("w.2");

        final Outcome<StringBuffer> specificOutcome = new Outcome<StringBuffer>()
        {
            @Override
            public void onFailure(StringBuffer sb)
            {
                Window.alert("Outcome is failure");
            }

            @Override
            public void onSuccess(StringBuffer sb)
            {
                Window.alert("Outcome is success: " + sb);

            }
        };

        new Async<StringBuffer>().waterfall(new StringBuffer(), specificOutcome, third, fourth);

    }

    private void runWhilst()
    {
        clearOutput();

        final int max = 3;
        final IncrementFunction function = new IncrementFunction();

        final Outcome outcome = new Outcome()
        {
            @Override
            public void onFailure(Object context)
            {
                append("<Whilst failed>");
            }

            @Override
            public void onSuccess(Object context)
            {
                append("<Whilst success>");
            }
        };

        Precondition condition = new
                Precondition()
                {
                    @Override
                    public boolean isMet()
                    {
                        return function.getCounter() < max;
                    }
                };

        new Async().whilst(condition, outcome, function);
    }

    private void runParallel()
    {
        clearOutput();

        Function p1 = new RandomTimedFunction("p.1", false);
        Function p2 = new RandomTimedFunction("p.2", false);
        Function p3 = new RandomTimedFunction("p.3", true);

        Outcome parallelOutcome = new Outcome()
        {
            @Override
            public void onFailure(Object context)
            {
                append("<Parallel failed>");
            }

            @Override
            public void onSuccess(Object context)
            {
                append("<Parallel success>");
            }
        };

        new Async().parallel(parallelOutcome, p1, p2, p3);

    }

    private void append(String text)
    {
        StringBuffer sb = new StringBuffer(output.getText()).append("\n");
        sb.append(text);
        output.setText(sb.toString());
    }

    private void clearOutput()
    {
        output.setText("");
    }


    class GenericFunction implements Function
    {
        final String name;

        GenericFunction(String name)
        {
            this.name = name;
        }

        @Override
        public void execute(Control control)
        {
            boolean shouldContinue = Window.confirm("Step " + name + ", continue?");

            if (!shouldContinue)
            { control.abort(); }
            else
            { control.proceed(); }
        }
    }


    class SpecificFunction implements Function<StringBuffer>
    {
        final String name;

        SpecificFunction(String name)
        {
            this.name = name;
        }

        @Override
        public void execute(Control<StringBuffer> control)
        {
            boolean shouldContinue = Window.confirm("Step " + name + ", continue?");
            control.getContext().append(",").append(name).append("-result");

            if (!shouldContinue)
            { control.abort(); }
            else
            { control.proceed(); }
        }
    }


    class IncrementFunction implements Function
    {
        private int counter;

        IncrementFunction()
        {
            this.counter = 0;
        }

        @Override
        public void execute(Control control)
        {
            append(counter + "");
            increment();

        }

        public void increment()
        {
            counter++;
        }

        public int getCounter()
        {
            return counter;
        }
    }


    class RandomTimedFunction implements Function
    {
        private final String name;
        private final int delayMillis;
        private final boolean shouldFail;

        RandomTimedFunction(String name, boolean shouldFail)
        {
            this.name = name;
            this.delayMillis = Random.nextInt(20) * 100;
            this.shouldFail = shouldFail;
        }

        @Override
        public void execute(final Control control)
        {
            Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand()
            {
                @Override
                public boolean execute()
                {

                    append("Finished " + name + " (" + delayMillis + ")");

                    if (shouldFail)
                    { control.abort(); }
                    else
                    { control.proceed(); }
                    return false;
                }
            }, delayMillis);
        }
    }
}
