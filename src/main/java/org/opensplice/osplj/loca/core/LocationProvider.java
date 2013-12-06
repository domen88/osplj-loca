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

package org.opensplice.osplj.loca.core;

public abstract class LocationProvider {

    private static final String PROPERTY = "provider";

    public static LocationProvider create() {

        String p = "org.opensplice.osplj.loca.pub." + System.getProperty(PROPERTY);

        try {

           return (LocationProvider)Class.forName(p).newInstance();  //new AndroidLocationProvider();

        } catch (IllegalArgumentException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (InstantiationException e){
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }

        return null;

    }

    public abstract LocationData getLocation();


}
