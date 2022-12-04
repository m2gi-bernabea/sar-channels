package edu.uga.m2gi.ar.channels;

import java.util.ArrayList;

public class BrokerImpl extends Broker {

    ArrayList<Integer> busyPort = new ArrayList<>();

    public BrokerImpl(String name) {
        super(name);
    }

    @Override
    public synchronized Channel accept(int port) {
        if (busyPort.contains(port)) {
            throw new IllegalArgumentException("Port " + port + " currently used.");
        }
        busyPort.add(port);
        RDV rdv = RDV.getRendezvous(m_name, port);
        return new ChannelImpl(rdv.getBuffer2In(), rdv.getBuffer2Out(), this, port, m_name + port);
    }

    @Override
    public synchronized Channel connect(String name, int port) {
        RDV rdv = RDV.getRendezvous(name, port);
        return new ChannelImpl(rdv.getBufferIn(), rdv.getBufferOut(), this, port, name + port);
    }

    public void releasePort(Integer port) {
        busyPort.remove(port);
    }
}
