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
import org.opensplice.osplj.sub.SampleData;



public class LocationAwareSample<T> implements Sample<T> {

    private SampleData<Object> delegatedata;
    private SampleInfo delegateinfo;

    public LocationAwareSample(SampleData<Object> sample){



        this.delegatedata = sample;
        this.delegateinfo =
    }


    @Override
    public T getData() {
        return (T)this.delegatedata.getData();
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
        return new LocationAwareSample<T>((SampleData<Object>)this.delegatedata, this.delegateinfo);
    }

    @Override
    public ServiceEnvironment getEnvironment() {
        return DomainParticipantFactoryImpl.getServiceEnvironment();
    }
}
