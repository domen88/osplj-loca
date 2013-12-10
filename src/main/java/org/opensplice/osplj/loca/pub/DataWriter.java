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

package org.opensplice.osplj.loca.pub;

import org.omg.dds.core.*;
import org.opensplice.osplj.loca.core.LocationData;
import org.omg.dds.core.status.*;
import org.omg.dds.pub.DataWriterListener;
import org.omg.dds.pub.DataWriterQos;
import org.omg.dds.pub.Publisher;
import org.omg.dds.topic.SubscriptionBuiltinTopicData;
import org.omg.dds.topic.Topic;
import org.opensplice.osplj.loca.core.LocationProvider;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DataWriter<T> implements org.omg.dds.pub.DataWriter<T>{

    private final static String TYPE_SUFFIX = "__locationAware";
    private final org.omg.dds.pub.DataWriter<Object> delegate;
    private Class<?> delegateClass;
    private Field loc;
    private Field val;
    private final LocationProvider lp;

    public DataWriter(Publisher pub, Topic<T> topic, DataWriterQos qos) {
        this(pub, topic, qos, LocationProvider.create());
    }

    public DataWriter(Publisher pub, Topic<T> topic, DataWriterQos qos, LocationProvider lp){

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

        Topic<?> tp = pub.getParent().createTopic(topic.getName() + TYPE_SUFFIX, delegateClass); //, topic.getQos(), null,
              //null);


        this.delegate = (org.omg.dds.pub.DataWriter<Object>)pub.createDataWriter(tp, qos);

    }

    public void write(T sample, LocationData l) throws TimeoutException{

        try {

            Class loca = Class.forName("LocationAware");
            Object locaObject = loca.newInstance();

            Field lat = loca.getField("latitude");
            Field lon = loca.getField("longitude");

            lat.set(locaObject, l.getLatitude());
            lon.set(locaObject, l.getLongitude());

            Object o = delegateClass.newInstance();

            loc.set(o, locaObject);
            val.set(o, sample);

            this.delegate.write(o);

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    @Override
    public <OTHER> org.omg.dds.pub.DataWriter<OTHER> cast() {
        return this.delegate.cast();
    }

    @Override
    public Topic<T> getTopic() {
        return (Topic<T>)this.delegate.getTopic();
    }

    @Override
    public void waitForAcknowledgments(Duration duration) throws TimeoutException {
        this.delegate.waitForAcknowledgments(duration);
    }

    @Override
    public void waitForAcknowledgments(long l, TimeUnit timeUnit) throws TimeoutException {
        this.delegate.waitForAcknowledgments(l, timeUnit);
    }

    @Override
    public LivelinessLostStatus getLivelinessLostStatus() {
        return this.delegate.getLivelinessLostStatus();
    }

    @Override
    public OfferedDeadlineMissedStatus getOfferedDeadlineMissedStatus() {
        return this.delegate.getOfferedDeadlineMissedStatus();
    }

    @Override
    public OfferedIncompatibleQosStatus getOfferedIncompatibleQosStatus() {
        return this.delegate.getOfferedIncompatibleQosStatus();
    }

    @Override
    public PublicationMatchedStatus getPublicationMatchedStatus() {
        return this.delegate.getPublicationMatchedStatus();
    }

    @Override
    public void assertLiveliness() {
        this.delegate.assertLiveliness();
    }

    @Override
    public Set<InstanceHandle> getMatchedSubscriptions() {
        return this.delegate.getMatchedSubscriptions();
    }

    @Override
    public SubscriptionBuiltinTopicData getMatchedSubscriptionData(InstanceHandle instanceHandle) {
        return this.delegate.getMatchedSubscriptionData(instanceHandle);
    }

    @Override
    public InstanceHandle registerInstance(T t) throws TimeoutException {
        return this.delegate.registerInstance(t);
    }

    @Override
    public InstanceHandle registerInstance(T t, Time time) throws TimeoutException {
        return this.delegate.registerInstance(t, time);
    }

    @Override
    public InstanceHandle registerInstance(T t, long l, TimeUnit timeUnit) throws TimeoutException {
        return this.delegate.registerInstance(t, l, timeUnit);
    }

    @Override
    public void unregisterInstance(InstanceHandle instanceHandle) throws TimeoutException {
        this.delegate.unregisterInstance(instanceHandle);
    }

    @Override
    public void unregisterInstance(InstanceHandle instanceHandle, T t) throws TimeoutException {
       this.delegate.unregisterInstance(instanceHandle, t);
    }

    @Override
    public void unregisterInstance(InstanceHandle instanceHandle, T t, Time time) throws TimeoutException {
        this.delegate.unregisterInstance(instanceHandle, t, time);
    }

    @Override
    public void unregisterInstance(InstanceHandle instanceHandle, T t, long l, TimeUnit timeUnit) throws TimeoutException {
        this.delegate.unregisterInstance(instanceHandle, t, l, timeUnit);
    }

    @Override
    public void write(T t) throws TimeoutException {

        LocationData l = lp.getLocation();

        try {

            Class loca = Class.forName("LocationAware");
            Object locaObject = loca.newInstance();

            Field lat = loca.getField("latitude");
            Field lon = loca.getField("longitude");

            lat.set(locaObject, l.getLatitude());
            lon.set(locaObject, l.getLongitude());

            Object o = delegateClass.newInstance();

            loc.set(o, locaObject);
            val.set(o, t);

            this.delegate.write(o);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        /*try {

            Object o = delegateClass.newInstance();

            loc.set(o, l);
            val.set(o, t);

            this.delegate.write(o);

        } catch (InstantiationException e) {
            e.printStackTrace();
            Log.e("BBB","ERR: " + e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.e("CCC","ERR: " + e);
        }
        */

    }

    @Override
    public void write(T t, Time time) throws TimeoutException {
        this.delegate.write(t, time);
    }

    @Override
    public void write(T t, long l, TimeUnit timeUnit) throws TimeoutException {
        this.delegate.write(t, l, timeUnit);
    }

    @Override
    public void write(T t, InstanceHandle instanceHandle) throws TimeoutException {
        this.delegate.write(t, instanceHandle);
    }

    @Override
    public void write(T t, InstanceHandle instanceHandle, Time time) throws TimeoutException {
        this.delegate.write(t, instanceHandle,time);
    }

    @Override
    public void write(T t, InstanceHandle instanceHandle, long l, TimeUnit timeUnit) throws TimeoutException {
        this.delegate.write(t, instanceHandle, l, timeUnit);
    }

    @Override
    public void dispose(InstanceHandle instanceHandle) throws TimeoutException {
        this.delegate.dispose(instanceHandle);
    }

    @Override
    public void dispose(InstanceHandle instanceHandle, T t) throws TimeoutException {
        this.delegate.dispose(instanceHandle, t);
    }

    @Override
    public void dispose(InstanceHandle instanceHandle, T t, Time time) throws TimeoutException {
        this.delegate.dispose(instanceHandle, t, time);
    }

    @Override
    public void dispose(InstanceHandle instanceHandle, T t, long l, TimeUnit timeUnit) throws TimeoutException {
        this.delegate.dispose(instanceHandle, t, l, timeUnit);
    }

    @Override
    public T getKeyValue(T t, InstanceHandle instanceHandle) {
        return (T)this.delegate.getKeyValue(t, instanceHandle);
    }

    @Override
    public T getKeyValue(InstanceHandle instanceHandle) {
        return (T)this.delegate.getKeyValue(instanceHandle);
    }

    @Override
    public InstanceHandle lookupInstance(T t) {
        return this.delegate.lookupInstance(t);
    }

    @Override
    public DataWriterListener<T> getListener() {
        return (DataWriterListener<T>)this.delegate.getListener();
    }

    @Override
    public void setListener(DataWriterListener<T> tDataWriterListener) {
        this.delegate.setListener((DataWriterListener<Object>)tDataWriterListener);
    }

    @Override
    public void setListener(DataWriterListener<T> tDataWriterListener, Collection<Class<? extends Status>> classes) {
        this.delegate.setListener((DataWriterListener<Object>) tDataWriterListener, classes);
    }

    @Override
    public void setListener(DataWriterListener<T> tDataWriterListener, Class<? extends Status>... classes) {
        this.delegate.setListener((DataWriterListener<Object>) tDataWriterListener, classes);
    }

    @Override
    public DataWriterQos getQos() {
        return this.delegate.getQos();
    }

    @Override
    public void setQos(DataWriterQos dataWriterQos) {
        this.delegate.setQos(dataWriterQos);
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
    public StatusCondition<org.omg.dds.pub.DataWriter<T>> getStatusCondition() {
        return (StatusCondition<org.omg.dds.pub.DataWriter<T>>)(StatusCondition)this.delegate.getStatusCondition();
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
    public Publisher getParent() {
        return this.delegate.getParent();
    }

    @Override
    public ServiceEnvironment getEnvironment() {
        return this.delegate.getEnvironment();
    }
}
