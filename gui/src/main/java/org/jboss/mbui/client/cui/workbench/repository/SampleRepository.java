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
package org.jboss.mbui.client.cui.workbench.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
public class SampleRepository
{
    public List<Sample> getSamples()
    {
        List<Sample> samples = new ArrayList<Sample>();

        samples.add(new DataSourceSample());
        samples.add(new NoopSample("Lorem"));
        samples.add(new NoopSample("ipsum"));
        samples.add(new NoopSample("dolor"));
        samples.add(new NoopSample("sit"));
        samples.add(new NoopSample("amet"));
        samples.add(new NoopSample("consetetur"));
        samples.add(new NoopSample("sadipscing"));
        samples.add(new NoopSample("elitr"));

        return samples;
    }
}
