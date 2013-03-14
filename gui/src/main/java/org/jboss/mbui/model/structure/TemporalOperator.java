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
package org.jboss.mbui.model.structure;

/**
 *
 *
 * Operators allow for putting tasks (InteractionUnits) on one hierarchical level into certain explicitly temporal orders.
 *
 * Since the unambiguous priority of these four temporal operators is crucial
 * for the connection of the use model with a dialog model, their priorities
 * (i.e., their order of temporal execution) have been defined as follows:
 *
 * <pre>
 *  Choice > Order Independence > Concurrency > Deactivation > Sequence
   </pre>

 * @author Harald Pehl
 * @author Heiko Braun
 *
 * @date 10/31/2012
 */
public enum TemporalOperator
{
    /**
     * Exactly one of two tasks will be fulfilled.
     */
    Choice,

    /**
     * The tasks must be accomplished in the given order. The second task
     * must wait until the first one has been fulfilled.
     */
    Sequence,

    /**
     * The two tasks can be accomplished in any arbitrary
     * order. However, when the first task has been performed, the second one has to wait for
     * the first one to be finalized or aborted.
     */
    OrderIndependance,

    /**
     * The two tasks can be accomplished in any arbitrary order, even
     * in parallel at the same time (i.e., concurrently).
     */
    Concurrency,

    /**
     * The second task interrupts and deactivates the first task.
     */
    Deactivation
}
