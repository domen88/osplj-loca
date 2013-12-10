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
import org.opensplice.osplj.sub.ReaderHistoryCache;
import org.opensplice.osplj.sub.SampleData;
import org.opensplice.osplj.utils.history.HistoryMap;

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

    private SampleData<T> delegatedata;
    private SampleInfo delegateinfo;
    private final Object loc;
    private final Object val;
    private final SampleState ss;
    private final ReaderHistoryCache.ReaderInstance ri;
    private final String topicType;

    public LocationAwareSample(Object loc, Object val, SampleState ss,
                               ReaderHistoryCache.ReaderInstance ri, String topicType)
    {

        this.loc = loc;
        this.val = val;
        this.ss = ss;
        this.ri = ri;
        this.topicType = topicType;

        try {

            Object object = Class.forName(topicType).newInstance();

            Field[] fields = object.getClass().getFields();

            for (Field field: fields){

                field.set(object,val.getClass().getField(field.getName()));

            }

            this.delegatedata = (SampleData<T>)object;
            this.delegateinfo = new SampleInfo(ss, ri);

            this.getData();


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }


    @Override
    public T getData() {
        return this.delegatedata.getData();
    }

    @Override
    public SampleState getSampleState() {
        return this.delegateinfo.sampleState;
    }

    @Override
    public ViewState getViewState() {
        return this.delegateinfo.viewState;
    }

    @Override
    public InstanceState getInstanceState() {
        return this.delegateinfo.instanceState;
    }

    @Override
    public Time getSourceTimestamp() {
        return this.delegatedata.getSourceTimestamp();
    }

    @Override
    public InstanceHandle getInstanceHandle() {
        return this.delegateinfo.instanceHandle;
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
        return new LocationAwareSample<T>(loc, val, ss, ri, topicType);
    }

    @Override
    public ServiceEnvironment getEnvironment() {
        return DomainParticipantFactoryImpl.getServiceEnvironment();
    }
}
