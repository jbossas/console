/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.gwt.flow.client;

import com.google.gwt.core.client.Scheduler;

/**
 * Flow control functions for GWT.
 * Integrates with the default GWT scheduling mechanism.
 *
 * @author Heiko Braun
 * @date 3/8/13
 */
public class Async<C>
{
    private final static Object EMPTY_CONTEXT = new Object();

    /**
     * Run an array of functions in series, each one running once the previous function has completed.
     * If any functions in the series pass an error to its callback,
     * no more functions are run and outcome for the series is immediately called with the value of the error.
     *
     * @param outcome
     * @param functions
     */
    public void series(final Outcome outcome, final Function... functions)
    {
        _series(null, outcome, functions);  // generic signature problem, hence null
    }

    /**
     * Runs an array of functions in series, working on a shared context.
     * However, if any of the functions pass an error to the callback,
     * the next function is not executed and the outcome is immediately called with the error.
     *
     * @param context
     * @param outcome
     * @param functions
     */
    public void waterfall(final C context, final Outcome<C> outcome, final Function<C>... functions)
    {
        _series(context, outcome, functions);
    }

    private void _series(C context, final Outcome<C> outcome, final Function<C>... functions)
    {
        final C finalContext = context != null ? context : (C) EMPTY_CONTEXT;
        final SequentialControl<C> ctrl = new SequentialControl<C>(finalContext, functions);

        // select first function anf start
        ctrl.proceed();
        Scheduler.get().scheduleIncremental(new Scheduler.RepeatingCommand()
        {
            @Override
            public boolean execute()
            {
                if (ctrl.isDrained())
                {
                    // schedule deferred so that 'return false' executes first!
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand()
                    {
                        @Override
                        public void execute()
                        {
                            outcome.onSuccess(finalContext);
                        }
                    });
                    return false;
                }
                else if (ctrl.isAborted())
                {
                    // schedule deferred so that 'return false' executes first!
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand()
                    {
                        @Override
                        public void execute()
                        {
                            outcome.onFailure(finalContext);
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

    /**
     * Run an array of functions in parallel, without waiting until the previous function has completed.
     * If any of the functions pass an error to its callback, the outcome is immediately called with the value of the
     * error.
     *
     * @param outcome
     * @param functions
     */
    public void parallel(final Outcome outcome, final Function... functions)
    {
        final CountingControl ctrl = new CountingControl(functions);
        Scheduler.get().scheduleIncremental(new Scheduler.RepeatingCommand()
        {
            @Override
            public boolean execute()
            {
                if (ctrl.isAborted() || ctrl.allFinished())
                {
                    // schedule deferred so that 'return false' executes first!
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand()
                    {
                        @Override
                        public void execute()
                        {
                            if (ctrl.isAborted())
                            { outcome.onFailure(EMPTY_CONTEXT); }
                            else
                            { outcome.onSuccess(EMPTY_CONTEXT); }

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

    /**
     * Repeatedly call function, while condition is met. Calls the callback when stopped, or an error occurs.
     *
     * @param condition
     * @param outcome
     * @param function
     */
    public void whilst(Precondition condition, final Outcome outcome, final Function function)
    {
        final GuardedControl ctrl = new GuardedControl(condition);
        Scheduler.get().scheduleIncremental(new Scheduler.RepeatingCommand()
        {
            @Override
            public boolean execute()
            {
                if (!ctrl.shouldProceed())
                {
                    // schedule deferred so that 'return false' executes first!
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand()
                    {
                        @Override
                        public void execute()
                        {
                            if (ctrl.isAborted())
                            { outcome.onFailure(EMPTY_CONTEXT); }
                            else
                            { outcome.onSuccess(EMPTY_CONTEXT); }
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


    private class SequentialControl<C> implements Control<C>
    {
        private final C context;
        private final Function<C>[] functions;
        private Function<C> next;
        private int index;
        private boolean drained;
        private boolean aborted;
        private boolean pending;

        SequentialControl(final C context, final Function<C>... functions)
        {
            this.context = context;
            this.functions = functions;
        }

        @Override
        public C getContext()
        {
            return context;
        }

        @Override
        public void proceed()
        {
            if (index >= functions.length)
            {
                next = null;
                drained = true;
            }
            else
            {
                next = functions[index];
                index++;
            }
            this.pending = false;
        }

        @Override
        public void abort()
        {
            this.aborted = true;
            this.pending = false;
        }

        public boolean isAborted()
        {
            return aborted;
        }

        public boolean isDrained()
        {
            return drained;
        }

        public void nextUnlessPending()
        {
            if (!pending)
            {
                pending = true;
                next.execute(this);
            }
        }
    }


    private class CountingControl implements Control
    {
        private final Function[] functions;
        private int index;
        private int finished;
        protected boolean aborted;

        CountingControl(Function... functions)
        {
            this.functions = functions;
        }

        @Override
        public Object getContext()
        {
            return EMPTY_CONTEXT;
        }

        public void next()
        {
            if (index < functions.length)
            {
                functions[index].execute(this);
                index++;
            }
        }

        @Override
        public void proceed()
        {
            increment();
        }

        private void increment()
        {
            ++finished;
        }

        @Override
        public void abort()
        {
            increment();
            aborted = true;
        }

        public boolean isAborted()
        {
            return aborted;
        }

        public boolean allFinished()
        {
            return finished >= functions.length;
        }
    }


    private class GuardedControl implements Control
    {
        private final Precondition condition;
        private boolean aborted;

        GuardedControl(Precondition condition)
        {
            this.condition = condition;
        }

        @Override
        public void proceed()
        {
            // ignore
        }

        public boolean shouldProceed()
        {
            return condition.isMet() && !aborted;
        }

        @Override
        public void abort()
        {
            this.aborted = true;
        }

        public boolean isAborted()
        {
            return aborted;
        }

        @Override
        public Object getContext()
        {
            return EMPTY_CONTEXT;
        }
    }
}
