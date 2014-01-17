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

import org.omg.dds.sub.DataReader;
import org.opensplice.osplj.loca.core.LocationProvider;

public class LocusFilter<T> implements DataReader.Filter<T> {

    private final Double r;
    private final Double garbage;

    public LocusFilter(Double radius, Double garbage){

        this.r = radius;
        this.garbage = garbage;

    }

    public DataReader.Filter setFilter(LocationProvider lp){

        return new LocusFilterImpl<Object>(lp, this.r);

    }

    public DataReader.Filter setGarbage(LocationProvider lp){

        return new LocusFilterImpl<Object>(lp, this.garbage);

    }

    @Override
    public boolean match(T data, Object... params) {


        return false;
    }
}
