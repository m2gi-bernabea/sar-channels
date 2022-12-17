package edu.uga.m2gi.ar.channels;

import java.util.HashMap;

public class RDV {

    static final HashMap<String, RDV> rendezvous = new HashMap<>();
    private final CircularBuffer bufferIn;
    private final CircularBuffer bufferOut;
    private final CircularBuffer buffer2In;
    private final CircularBuffer buffer2Out;

    public RDV(String brokerName, Integer port) {
        bufferIn = new CircularBuffer(512);
        bufferOut = new CircularBuffer(512);
        buffer2Out = bufferIn;
        buffer2In = bufferOut;
        rendezvous.put(brokerName + port, this);
    }

    public static void deleteInstance(String key) {
        rendezvous.remove(key);
    }

    static RDV getInstance(String brokerName, Integer port) {
        return rendezvous.get(brokerName + port);
    }

    static boolean instanceExist(String rdvKey) {
        return rendezvous.containsKey(rdvKey);
    }

    static RDV createInstance(String brokerName, Integer port) {
        return new RDV(brokerName, port);
    }

    public static synchronized RDV getRendezvous(String brokerName, Integer port) {
        RDV rdv;
        synchronized (rendezvous) {
            if (!rendezvous.containsKey(brokerName + port)) {
                rdv = RDV.createInstance(brokerName, port);
            } else {
                rdv = RDV.getInstance(brokerName, port);
            }
        }

        synchronized (rdv) {
            if (rendezvous.containsKey(brokerName + port)) {
                rdv.notify();
            } else {
                try {
                    rdv.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return rdv;
    }

    public CircularBuffer getBufferIn() {
        return bufferIn;
    }

    public CircularBuffer getBuffer2In() {
        return buffer2In;
    }

    public CircularBuffer getBuffer2Out() {
        return buffer2Out;
    }

    public CircularBuffer getBufferOut() {
        return bufferOut;
    }
}
