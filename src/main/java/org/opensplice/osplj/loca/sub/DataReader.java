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

import org.omg.dds.core.Duration;
import org.omg.dds.core.InstanceHandle;
import org.omg.dds.core.ServiceEnvironment;
import org.omg.dds.core.StatusCondition;
import org.omg.dds.core.policy.PolicyFactory;
import org.omg.dds.core.status.*;
import org.omg.dds.sub.*;
import org.omg.dds.topic.PublicationBuiltinTopicData;
import org.omg.dds.topic.Topic;
import org.omg.dds.topic.TopicDescription;
import org.opensplice.osplj.loca.core.LocationProvider;
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
    private Class<?> delegateClass;
    private Field loc;
    private Field val;
    private final LocationProvider lp;
    private final PolicyFactory pf = PolicyFactory.getPolicyFactory(getEnvironment());
    private final DataReaderQos drqos;

    public DataReader(Subscriber sub, Topic<T> topic, DataReaderQos qos, LocationProvider lp){

        DataReaderQos drqos1;

        this.lp = lp;

        String delegateType = topic.getTypeName() + TYPE_SUFFIX;

        try {
            delegateClass = Class.forName(delegateType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            loc = delegateClass.getField("l");
            val = delegateClass.getField("v");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        Topic<?> tp = sub.getParent().createTopic(topic.getName() + TYPE_SUFFIX, delegateClass);//, topic.getQos(), null,
        //null);

        try {

            drqos1 = qos.withPolicies(
                    pf.ContentFilter()
                            .withFilter(
                                    new JavaScriptFilter<Object>("MY_CONDITION")
                            )
                    );

        } catch (ScriptException e){
            e.printStackTrace();
            drqos1 = qos;
        }


        drqos = drqos1;
        this.delegate = (org.omg.dds.sub.DataReader<Object>)sub.createDataReader(topic,drqos);

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
        Sample<Object> sample;

        while (it.hasNext()) {

            sample = (Sample<Object>) it;
            l.add(new LocationAwareSample<T>((SampleData<Object>) sample));
            it.next();


        }


        return null;
    }

    @Override
    public Sample.Iterator<T> read(Selector<T> tSelector) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Sample.Iterator<T> read(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Sample<T>> read(List<Sample<T>> samples) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Sample<T>> read(List<Sample<T>> samples, Selector<T> tSelector) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Sample.Iterator<T> take() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Sample.Iterator<T> take(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Sample.Iterator<T> take(Selector<T> tSelector) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Sample<T>> take(List<Sample<T>> samples) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Sample<T>> take(List<Sample<T>> samples, Selector<T> tSelector) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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

    @Override
    public void setListener(DataReaderListener<T> tDataReaderListener) {
        this.delegate.setListener((DataReaderListener<Object>)tDataReaderListener);
    }

    @Override
    public void setListener(DataReaderListener<T> tDataReaderListener, Collection<Class<? extends Status>> classes) {
        this.delegate.setListener((DataReaderListener<Object>)tDataReaderListener, classes);
    }

    @Override
    public void setListener(DataReaderListener<T> tDataReaderListener, Class<? extends Status>... classes) {
        this.delegate.setListener((DataReaderListener<Object>)tDataReaderListener, classes);
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
