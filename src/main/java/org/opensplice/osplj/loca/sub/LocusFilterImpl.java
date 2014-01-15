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
import org.opensplice.osplj.loca.core.LocationData;
import org.opensplice.osplj.loca.core.LocationProvider;

import java.lang.reflect.Field;

public class LocusFilterImpl<Object> implements DataReader.Filter<Object> {

    private final LocationProvider lp;
    private final double radius;

    public LocusFilterImpl(LocationProvider lp, Double radius){
         this.lp = lp;
         this.radius = radius;

    }

    @Override
    public boolean match(java.lang.Object data, java.lang.Object... params) {


        try {

            Field locaf = data.getClass().getField("l");
            java.lang.Object l1= locaf.get(data);
            Field lat = l1.getClass().getField("latitude");
            Field lon = l1.getClass().getField("longitude");

            Double latitude = (Double) lat.get(l1);
            Double longitude = (Double) lon.get(l1);

            Double long1 = Math.PI * (Double)params[1] / 180;
            Double lat1 = Math.PI * (Double)params[0] / 180;
            Double long2 = Math.PI * longitude / 180;
            Double lat2 = Math.PI * latitude / 180;

            Double phi = Math.abs(long1 - long2);
            Double EarthRadius = 6372.795477598;
            Double p = Math.acos( (Math.sin(lat1) * Math.sin(lat2)) + (Math.cos(lat1) * Math.cos(lat2) * Math.cos(phi)) );

            Double distanza = p * EarthRadius;

            return (distanza < this.radius) ;


        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

}
