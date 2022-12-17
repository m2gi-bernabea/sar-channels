package edu.uga.m2gi.ar.message;

import edu.uga.m2gi.ar.channels.RDV;

import java.util.ArrayList;

public class QueueBroker {

    ArrayList<Integer> busyPort = new ArrayList<>();
    String m_name;

    public QueueBroker(String name) {
        this.m_name = name;
    }

    public synchronized MessageQueue accept(int port) {
        if (busyPort.contains(port)) {
            throw new IllegalArgumentException("Port " + port + " currently used.");
        }
        busyPort.add(port);
        RDV rdv = RDV.getRendezvous(m_name, port);
        return new MessageQueue(rdv.getBuffer2In(), rdv.getBuffer2Out(), this, port, m_name + port);
    }

    public synchronized MessageQueue connect(String name, int port) {
        RDV rdv = RDV.getRendezvous(name, port);
        return new MessageQueue(rdv.getBufferIn(), rdv.getBufferOut(), this, port, name + port);
    }

    public void releasePort(Integer port) {
        busyPort.remove(port);
    }

}
