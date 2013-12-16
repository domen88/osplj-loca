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
import org.omg.dds.sub.InstanceState;
import org.omg.dds.sub.SampleState;
import org.omg.dds.sub.ViewState;
import org.opensplice.osplj.sub.ReaderHistoryCache;

import java.io.Serializable;


public class SampleInfo implements Serializable
{
    private static final long serialVersionUID = -80270764719817525L;

    protected SampleState sampleState;
    protected ViewState viewState;
    protected InstanceState instanceState;
    protected InstanceHandle instanceHandle;

    public SampleInfo(SampleState sampleState, ReaderHistoryCache.ReaderInstance<?> context)
    {

        this.sampleState = sampleState;
        if(context != null)
        {
            this.viewState = context.viewState;
            this.instanceState = context.instanceState;
            this.instanceHandle = context.getHandle();
        }
        else
        {
            this.viewState = ViewState.NEW;
            this.instanceState = InstanceState.ALIVE;
            this.instanceHandle = null;
        }
    }

}