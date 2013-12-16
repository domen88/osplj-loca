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

import org.omg.dds.core.ServiceEnvironment;
import org.omg.dds.core.status.DataAvailableStatus;
import org.omg.dds.sub.DataReader;
import org.omg.dds.core.event.DataAvailableEvent;
import org.opensplice.osplj.core.status.DataAvailableStatusImpl;
import org.opensplice.osplj.domain.DomainParticipantFactoryImpl;


public class DataAvailableEventLoc<TYPE> extends DataAvailableEvent<TYPE> {

    DataAvailableStatus status;

    public DataAvailableEventLoc(DataReader<TYPE> source, DataAvailableStatus status) {
        super(source);
        this.status = status;

    }

    @Override
    public DataAvailableStatus getStatus() {
        return status;
    }

    @SuppressWarnings ("unchecked")
    @Override
    public DataReader<TYPE> getSource() {
        return (DataReader<TYPE>)source;
    }

    @Override
    public DataAvailableEvent<TYPE> clone() {
        return new DataAvailableEventLoc<TYPE>(getSource(), new DataAvailableStatusImpl());
    }

    @Override
    public ServiceEnvironment getEnvironment() {
        return DomainParticipantFactoryImpl.getServiceEnvironment();
    }
}
