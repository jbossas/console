package org.jboss.gwt.flow.client;

import com.google.gwt.core.client.Scheduler;

/**
 * Flow control functions for GWT.
 * Integrates with the default GWT scheduling mechanism.
 *
 * @author Heiko Braun
 * @date 3/8/13
 */
public class FlowControl {

    private final static Ctx<Object> EMPTY_CONTEXT = new Ctx<Object>(new Object()) {};

    protected FlowControl() {
    }

    // -----------  API  -----------

    /**
     * Run an array of functions in series, each one running once the previous function has completed.
     * If any functions in the series pass an error to its callback,
     * no more functions are run and outcome for the series is immediately called with the value of the error.
     *
     * @param outcome
     * @param functions
     */
    public static void series(final Outcome outcome, final Function... functions)
    {
        new FlowControl()._series(outcome, EMPTY_CONTEXT, functions);
    }

    /**
     * Runs an array of functions in series, working on a shared context.
     * However, if any of the functions pass an error to the callback,
     * the next function is not executed and the outcome is immediately called with the error.
     *
     * @param outcome
     * @param context
     * @param functions
     */
    public static void waterfall(final Outcome outcome, Ctx context, final Function... functions)
    {
        new FlowControl()._series(outcome, context, functions);
    }

    /**
     * Run an array of functions in parallel, without waiting until the previous function has completed.
     * If any of the functions pass an error to its callback, the outcome is immediately called with the value of the error.
     *
     * @param outcome
     * @param functions
     */
    public static void parallel(final Outcome outcome, final Function... functions)
    {
        new FlowControl()._parallel(outcome, EMPTY_CONTEXT, functions);
    }

    /**
     * Repeatedly call function, while condition is met. Calls the callback when stopped, or an error occurs.
     *
     * @param condition
     * @param outcome
     * @param function
     */
    public static void whilst(Precondition condition, final Outcome outcome, final Function function)
    {
        new FlowControl()._whilst(condition, outcome, EMPTY_CONTEXT, function);
    }


    // -----------  Implementation -----------

    private void _series(final Outcome outcome, final Ctx context, final Function... functions) {

        final SequentialControl<Object> ctrl = new SequentialControl<Object>(context.get(), functions);

        // select first task
        ctrl.proceed();

        Scheduler.get().scheduleIncremental(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {

                if(ctrl.isDrained())
                {
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            outcome.onSuccess(context.get());
                        }
                    });

                    return false;
                }
                else if(ctrl.isAborted())
                {
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            outcome.onFailure();
                        }
                    });
                    return false;
                }
                else
                {
                    ctrl.nextUnlessPending();
                    return true;
                }
            }
        });
    }

    private void _parallel(final Outcome outcome, final Ctx context, final Function... functions) {
        final CountingControl<Object> ctrl = new CountingControl<Object>(context.get(), functions);

        Scheduler.get().scheduleIncremental(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {

                if(ctrl.hasFinishedAll())
                {
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            if (ctrl.isAborted())
                                outcome.onFailure();
                            else
                                outcome.onSuccess(context.get());

                        }
                    });

                    return false;
                }
                else
                {
                    // one after the other until all are active
                    ctrl.next();
                    return true;
                }
            }
        });
    }

    private void _whilst(Precondition condition, final Outcome outcome, final Ctx context, final Function function) {

        final GuardedControl ctrl = new GuardedControl(condition, context.get());

        Scheduler.get().scheduleIncremental(new Scheduler.RepeatingCommand() {
                    @Override
                    public boolean execute() {

                        if(!ctrl.shouldProceed())
                        {
                            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                                @Override
                                public void execute() {
                                    if (ctrl.isAborted())
                                        outcome.onFailure();
                                    else
                                        outcome.onSuccess(context.get());

                                }
                            });

                            return false;
                        }
                        else
                        {
                            function.execute(ctrl);
                            return true;
                        }
                    }
                });
    }


    private class SequentialControl<C> implements Control {
        protected Function[] functions;
        protected Function next = null;
        protected boolean isDrained;
        protected boolean isAborted;
        protected int i = 0;
        protected boolean pending = false;
        protected C context;

        SequentialControl(C context, Function[] functions) {
            this.functions = functions;
            this.context = context;
        }

        @Override
        public C getContext() {
            return context;
        }

        @Override
        public void proceed() {
            if(i>=functions.length)
            {
                next = null;
                isDrained = true;
            }
            else
            {
                next = functions[i];
                i++;
            }

            this.pending = false;
        }

        @Override
        public void abort() {
            this.isAborted = true;
            this.pending = false;
        }

        public boolean isAborted() {
            return isAborted;
        }

        public boolean isDrained() {
            return isDrained;
        }

        public void nextUnlessPending() {

            if(!pending)
            {
                pending = true;
                next.execute(this);
            }
        }
    }

    class CountingControl<C> implements Control
    {
        protected Function[] functions;
        protected Function next = null;
        protected boolean isAborted;

        protected C context;

        private int index = 0;
        private int finished = 0;

        CountingControl(C context, Function... functions) {
            this.functions = functions;
            this.context = context;
        }

        @Override
        public C getContext() {
            return context;
        }

        public void next() {
            if(index<functions.length)
            {
                functions[index].execute(this);
                index++;
            }
        }

        @Override
        public void proceed() {
            increment();
        }

        private void increment() {
            ++finished;
        }

        @Override
        public void abort() {
            increment();
            isAborted = true;
        }

        public boolean isAborted() {
            return isAborted;
        }

        public boolean hasFinishedAll()
        {
            return isAborted || finished>=functions.length;
        }


    }

    class GuardedControl<C> implements Control
    {
        private Precondition condition;
        private boolean isAborted;
        private C context;

        GuardedControl(Precondition condition, C context) {
            this.condition = condition;
            this.context = context;
        }

        @Override
        public void proceed() {
            // ignore
        }

        public boolean shouldProceed()
        {
            return condition.isMet() && !isAborted;
        }
        @Override
        public void abort() {
            this.isAborted = true;
        }

        public boolean isAborted() {
            return isAborted;
        }

        @Override
        public C getContext() {
            return context;
        }
    }
}
