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

import android.util.Log;
import org.omg.dds.core.Duration;
import org.omg.dds.core.InstanceHandle;
import org.omg.dds.core.ServiceEnvironment;
import org.omg.dds.core.StatusCondition;
import org.omg.dds.core.event.*;
import org.omg.dds.core.policy.ContentFilter;
import org.omg.dds.core.policy.PolicyFactory;
import org.omg.dds.core.status.*;
import org.omg.dds.sub.*;
import org.omg.dds.sub.DataReaderListener;
import org.omg.dds.topic.PublicationBuiltinTopicData;
import org.omg.dds.topic.Topic;
import org.omg.dds.topic.TopicDescription;
import org.opensplice.osplj.loca.core.DataAvailableEventLoc;
import org.opensplice.osplj.loca.core.LocationProvider;
import org.opensplice.osplj.sub.ReaderHistoryCache;
import org.opensplice.osplj.sub.SampleData;
import org.opensplice.osplj.utils.JavaScriptFilter;

import javax.script.ScriptException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class DataReader<T> implements org.omg.dds.sub.DataReader<T>{

    private final static String TYPE_SUFFIX = "__locationAware";
    private final org.omg.dds.sub.DataReader<Object> delegate;
    private final String topicType;
    private Class<?> delegateClass;
    private Field loc;
    private Field val;
    private final LocationProvider lp;
    private final DataReaderQos drqos;

    public DataReader(Subscriber sub, Topic<T> topic, DataReaderQos qos) {
        this(sub, topic, qos, LocationProvider.create());
    }

    public DataReader(Subscriber sub, Topic<T> topic, DataReaderQos qos, LocationProvider lp) {


        this.drqos = qos;

        this.lp = lp;

        String delegateType = topic.getTypeName() + TYPE_SUFFIX;

        this.topicType = topic.getTypeName();

        try {

            delegateClass = Class.forName(delegateType);
            loc = delegateClass.getField("l");
            val = delegateClass.getField("v");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        Topic<?> tp = sub.getParent().createTopic(topic.getName() + TYPE_SUFFIX, delegateClass);//, topic.getQos(), null,
        //null);

        /* -------------------------JAVASCRIPT FILTER----------------------------*/
//        try {
//
//            ContentFilter filter =
//            PolicyFactory.getPolicyFactory(getEnvironment()) .ContentFilter()
//                    .withFilter(
//                            new JavaScriptFilter<T>("data.ID==params[0]"));
//
//            drqos.withPolicies(filter);
//
//        } catch (ScriptException e) {
//           e.printStackTrace();
//        }
//

        this.delegate = (org.omg.dds.sub.DataReader<Object>)sub.createDataReader(tp,drqos);

    }

    @Override
    public <OTHER> org.omg.dds.sub.DataReader<OTHER> cast() {
        return this.delegate.cast();
    }

    @Override
    public ReadCondition<T> createReadCondition(Subscriber.DataState dataState) {
        return (ReadCondition<T>)this.delegate.createReadCondition(dataState);
    }

    @Override
    public QueryCondition<T> createQueryCondition(String s, List<String> strings) {
        return (QueryCondition<T>)this.delegate.createQueryCondition(s, strings);
    }

    @Override
    public QueryCondition<T> createQueryCondition(String s, String... strings) {
        return (QueryCondition<T>)this.delegate.createQueryCondition(s, strings);
    }

    @Override
    public QueryCondition<T> createQueryCondition(Subscriber.DataState dataState, String s, List<String> strings) {
        return (QueryCondition<T>)this.delegate.createQueryCondition(dataState, s, strings);
    }

    @Override
    public QueryCondition<T> createQueryCondition(Subscriber.DataState dataState, String s, String... strings) {
        return (QueryCondition<T>)this.delegate.createQueryCondition(dataState, s, strings);
    }

    @Override
    public void closeContainedEntities() {
        this.delegate.closeContainedEntities();
    }

    @Override
    public TopicDescription<T> getTopicDescription() {
        return (TopicDescription<T>)this.delegate.getTopicDescription();
    }

    @Override
    public SampleRejectedStatus getSampleRejectedStatus() {
        return this.delegate.getSampleRejectedStatus();
    }

    @Override
    public LivelinessChangedStatus getLivelinessChangedStatus() {
        return this.delegate.getLivelinessChangedStatus();
    }

    @Override
    public RequestedDeadlineMissedStatus getRequestedDeadlineMissedStatus() {
        return this.delegate.getRequestedDeadlineMissedStatus();
    }

    @Override
    public RequestedIncompatibleQosStatus getRequestedIncompatibleQosStatus() {
        return this.delegate.getRequestedIncompatibleQosStatus();
    }

    @Override
    public SubscriptionMatchedStatus getSubscriptionMatchedStatus() {
        return this.delegate.getSubscriptionMatchedStatus();
    }

    @Override
    public SampleLostStatus getSampleLostStatus() {
        return this.delegate.getSampleLostStatus();
    }

    @Override
    public void waitForHistoricalData(Duration duration) throws TimeoutException {
        this.delegate.waitForHistoricalData(duration);
    }

    @Override
    public void waitForHistoricalData(long l, TimeUnit timeUnit) throws TimeoutException {
        this.delegate.waitForHistoricalData(l,timeUnit);
    }

    @Override
    public Set<InstanceHandle> getMatchedPublications() {
        return this.delegate.getMatchedPublications();
    }

    @Override
    public PublicationBuiltinTopicData getMatchedPublicationData(InstanceHandle instanceHandle) {
        return this.delegate.getMatchedPublicationData(instanceHandle);
    }

    @Override
    public Sample.Iterator<T> read() {


        Iterator<Sample<Object>> it = delegate.read();
        List<LocationAwareSample<T>> l = new ArrayList<LocationAwareSample<T>>();

        while (it.hasNext()) {

            Sample<Object> sample = it.next();

            Object loc = new Object();
            Object val = new Object();

            try{

                if (sample.getData() != null) {

                    Object o = sample.getData();

                    Field location = o.getClass().getField("l");
                    Field value = o.getClass().getField("v");

                    loc = location.get(o);
                    val = value.get(o);

                    l.add(new LocationAwareSample<T>(loc, val, topicType, sample, lp));

                }

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


        }

        return new LocationAwareSample.LocationAwareIterator<T>(l);

    }

    @Override
    public Sample.Iterator<T> read(Selector<T> tSelector) {

        Iterator<Sample<Object>> it = delegate.read((Selector<Object>) tSelector);
        List<LocationAwareSample<T>> l = new ArrayList<LocationAwareSample<T>>();

        while (it.hasNext()) {

            Sample<Object> sample = it.next();

            Object loc = new Object();
            Object val = new Object();

            try{

                if (sample.getData() != null) {

                    Object o = sample.getData();

                    Field location = o.getClass().getField("l");
                    Field value = o.getClass().getField("v");

                    loc = location.get(o);
                    val = value.get(o);

                    l.add(new LocationAwareSample<T>(loc, val, topicType, sample, lp));
                }

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


        }

        return new LocationAwareSample.LocationAwareIterator<T>(l);
    }

    @Override
    public Sample.Iterator<T> read(int i) {

        Iterator<Sample<Object>> it = delegate.read(i);
        List<LocationAwareSample<T>> l = new ArrayList<LocationAwareSample<T>>();

        while (it.hasNext()) {

            Sample<Object> sample = it.next();

            Object loc = new Object();
            Object val = new Object();

            try{

                if (sample.getData() != null) {

                    Object o = sample.getData();

                    Field location = o.getClass().getField("l");
                    Field value = o.getClass().getField("v");

                    loc = location.get(o);
                    val = value.get(o);

                    l.add(new LocationAwareSample<T>(loc, val, topicType, sample, lp));
                }

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


        }

        return new LocationAwareSample.LocationAwareIterator<T>(l);
    }

    @Override
    public List<Sample<T>> read(List<Sample<T>> samples) {
        return (List<Sample<T>>) this.delegate.read((Selector<Object>) samples);
    }

    @Override
    public List<Sample<T>> read(List<Sample<T>> samples, Selector<T> tSelector) {
        return this.read(samples);    //DA FARE
    }

    @Override
    public Sample.Iterator<T> take() {

        Iterator<Sample<Object>> it = delegate.take();
        List<LocationAwareSample<T>> l = new ArrayList<LocationAwareSample<T>>();

        while (it.hasNext()) {

            Sample<Object> sample = it.next();

            Object loc = new Object();
            Object val = new Object();

            try{

                if (sample.getData() != null) {

                    Object o = sample.getData();

                    Field location = o.getClass().getField("l");
                    Field value = o.getClass().getField("v");

                    loc = location.get(o);
                    val = value.get(o);

                    l.add(new LocationAwareSample<T>(loc, val, topicType, sample, lp));
                }

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


        }

        return new LocationAwareSample.LocationAwareIterator<T>(l);
    }

    @Override
    public Sample.Iterator<T> take(int i) {
        return (Sample.Iterator<T>) this.delegate.take(i);
    }

    @Override
    public Sample.Iterator<T> take(Selector<T> tSelector) {
        return (Sample.Iterator<T>) this.delegate.take((List<Sample<Object>>) tSelector);
    }

    @Override
    public List<Sample<T>> take(List<Sample<T>> samples) {
        return (List<Sample<T>>) this.delegate.take((Selector<Object>)samples);
    }

    @Override
    public List<Sample<T>> take(List<Sample<T>> samples, Selector<T> tSelector) {
        return this.take(samples);      //DA FARE
    }

    @Override
    public boolean readNextSample(Sample<T> tSample) {
        return this.delegate.readNextSample((Sample<Object>)tSample);
    }

    @Override
    public boolean takeNextSample(Sample<T> tSample) {
        return this.delegate.takeNextSample((Sample<Object>)tSample);
    }

    @Override
    public T getKeyValue(T t, InstanceHandle instanceHandle) {
        return (T)this.delegate.getKeyValue(t,instanceHandle);
    }

    @Override
    public InstanceHandle lookupInstance(T t) {
        return this.delegate.lookupInstance(t);
    }

    @Override
    public StatusCondition<org.omg.dds.sub.DataReader<T>> getStatusCondition() {
        return (StatusCondition<org.omg.dds.sub.DataReader<T>>)(StatusCondition)this.delegate.getStatusCondition();
    }

    @Override
    public Subscriber getParent() {
        return this.delegate.getParent();
    }

    @Override
    public Selector<T> select() {
        return (Selector<T>)this.delegate.select();
    }

    @Override
    public DataReaderListener<T> getListener() {
        return (DataReaderListener<T>)this.delegate.getListener();
    }

    public DataReader<T> getReader(){
        return this;
    }

    public class LocationListener implements DataReaderListener<Object> {

        private final DataReaderListener<T> dataReaderListener;

        public LocationListener(DataReaderListener<T> tDataReaderListener){

            this.dataReaderListener = tDataReaderListener;

        }

        @Override
        public void onDataAvailable(DataAvailableEvent<Object> objectDataAvailableEvent) {

            DataAvailableEventLoc dataAvailableEventLoc = new DataAvailableEventLoc(getReader(),
                    objectDataAvailableEvent.getStatus());

            this.dataReaderListener.onDataAvailable(dataAvailableEventLoc);

        }

        @Override
        public void onRequestedDeadlineMissed(RequestedDeadlineMissedEvent<Object> objectRequestedDeadlineMissedEvent) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onRequestedIncompatibleQos(RequestedIncompatibleQosEvent<Object> objectRequestedIncompatibleQosEvent) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onSampleRejected(SampleRejectedEvent<Object> objectSampleRejectedEvent) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onLivelinessChanged(LivelinessChangedEvent<Object> objectLivelinessChangedEvent) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onSubscriptionMatched(SubscriptionMatchedEvent<Object> objectSubscriptionMatchedEvent) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onSampleLost(SampleLostEvent<Object> objectSampleLostEvent) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

    }

    @Override
    public void setListener(DataReaderListener<T> tDataReaderListener) {

        this.delegate.setListener(new LocationListener(tDataReaderListener));

    }

    @Override
    public void setListener(DataReaderListener<T> tDataReaderListener, Collection<Class<? extends Status>> classes) {
        this.delegate.setListener((DataReaderListener<Object>) tDataReaderListener, classes);
    }

    @Override
    public void setListener(DataReaderListener<T> tDataReaderListener, Class<? extends Status>... classes) {
        this.delegate.setListener((DataReaderListener<Object>) tDataReaderListener, classes);
    }


    @Override
    public DataReaderQos getQos() {
        return this.delegate.getQos();
    }

    @Override
    public void setQos(DataReaderQos dataReaderQos) {
        this.delegate.setQos(dataReaderQos);
    }

    @Override
    public void setQos(String s, String s2) {
        this.delegate.setQos(s, s2);
    }

    @Override
    public void enable() {
        this.delegate.enable();
    }

    @Override
    public Set<Class<? extends Status>> getStatusChanges() {
        return this.delegate.getStatusChanges();
    }

    @Override
    public InstanceHandle getInstanceHandle() {
        return this.delegate.getInstanceHandle();
    }

    @Override
    public void close() {
        this.delegate.close();
    }

    @Override
    public void retain() {
        this.delegate.retain();
    }

    @Override
    public ServiceEnvironment getEnvironment() {
        return this.delegate.getEnvironment();
    }
}