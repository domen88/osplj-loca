package org.opensplice.osplj.loca.sub;

import org.omg.dds.core.InstanceHandle;
import org.omg.dds.core.ServiceEnvironment;
import org.omg.dds.core.Time;
import org.omg.dds.sub.InstanceState;
import org.omg.dds.sub.Sample;
import org.omg.dds.sub.SampleState;
import org.omg.dds.sub.ViewState;

/**
 * Created with IntelliJ IDEA.
 * User: domenicoscotece
 * Date: 06/12/13
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class LocationAwareSample<T> implements Sample<T> {

    //private final Sample<Object> delegate;



    @Override
    public T getData() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SampleState getSampleState() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ViewState getViewState() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InstanceState getInstanceState() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Time getSourceTimestamp() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InstanceHandle getInstanceHandle() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InstanceHandle getPublicationHandle() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getDisposedGenerationCount() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getNoWritersGenerationCount() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getSampleRank() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getGenerationRank() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getAbsoluteGenerationRank() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Sample<T> clone() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ServiceEnvironment getEnvironment() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
