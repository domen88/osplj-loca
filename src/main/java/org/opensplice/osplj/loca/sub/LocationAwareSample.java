/**
 *                         OpenSplice For Java
 *
 *    This software and documentation are Copyright 2010 to 2013 PrismTech
 *    Limited and its licensees. All rights reserved. See file:
 *
 *                           docs/LICENSE.html
 *
 *    for full copyright notice and license terms.
 */

package org.opensplice.osplj.loca.sub;

import org.omg.dds.core.InstanceHandle;
import org.omg.dds.core.ServiceEnvironment;
import org.omg.dds.core.Time;
import org.omg.dds.sub.InstanceState;
import org.omg.dds.sub.Sample;
import org.omg.dds.sub.SampleState;
import org.omg.dds.sub.ViewState;
import org.opensplice.osplj.domain.DomainParticipantFactoryImpl;
import org.opensplice.osplj.loca.core.LocationData;
import org.opensplice.osplj.loca.core.LocationProvider;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ListIterator;


public class LocationAwareSample<T> implements Sample<T> {

    public static class LocationAwareIterator<ITERTYPE> implements Iterator<ITERTYPE>
    {
        private ListIterator<LocationAwareSample<ITERTYPE>> iterator;

        protected LocationAwareIterator(List<LocationAwareSample<ITERTYPE>> list)
        {
            iterator = list.listIterator();
        }

        @Override
        public boolean hasNext()
        {
            return iterator.hasNext();
        }

        @Override
        public Sample<ITERTYPE> next()
        {
            return iterator.next();
        }

        @Override
        public boolean hasPrevious()
        {
            return iterator.hasPrevious();
        }

        @Override
        public Sample<ITERTYPE> previous()
        {
            return iterator.previous();
        }

        @Override
        public int nextIndex()
        {
            return iterator.nextIndex();
        }

        @Override
        public int previousIndex()
        {
            return iterator.previousIndex();
        }

        @Override
        public void close() throws IOException
        {
            // nothing to do.
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("As specified in PSM");
        }

        @Override
        public void set(Sample<ITERTYPE> o)
        {
            throw new UnsupportedOperationException("As specified in PSM");
        }

        @Override
        public void add(Sample<ITERTYPE> o)
        {
            throw new UnsupportedOperationException("As specified in PSM");
        }

    }

    private Sample<T> delegate;
    private final Object loc;
    private final Object val;
    private final LocationProvider lp;
    private final String topicType;

    public LocationAwareSample(Object loc, Object val, String topicType, Sample sample, LocationProvider lp)
    {

        this.lp = lp;
        this.loc = loc;
        this.val = val;
        this.topicType = topicType;

        this.delegate = sample;

        this.getData();

    }

    public LocationData getLocation(){

        return lp.getLocation();

    }

    @Override
    public T getData() {
        return (T)this.val;
    }

    @Override
    public SampleState getSampleState() {
        return this.delegate.getSampleState();
    }

    @Override
    public ViewState getViewState() {
        return this.delegate.getViewState();
    }

    @Override
    public InstanceState getInstanceState() {
        return this.delegate.getInstanceState();
    }

    @Override
    public Time getSourceTimestamp() {
        return this.delegate.getSourceTimestamp();
    }

    @Override
    public InstanceHandle getInstanceHandle() {
        return this.delegate.getInstanceHandle();
    }

    @Override
    public InstanceHandle getPublicationHandle() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int getDisposedGenerationCount() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int getNoWritersGenerationCount() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int getSampleRank() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int getGenerationRank() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int getAbsoluteGenerationRank() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Sample<T> clone() {
        return new LocationAwareSample<T>(loc, val, topicType, delegate, lp);
    }

    @Override
    public ServiceEnvironment getEnvironment() {
        return DomainParticipantFactoryImpl.getServiceEnvironment();
    }
}
