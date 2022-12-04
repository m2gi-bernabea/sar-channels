package edu.uga.m2gi.ar.channels;

public class ChannelImpl extends Channel {

    private final CircularBuffer circularBufferIn;
    private final CircularBuffer circularBufferOut;
    private final BrokerImpl broker;
    private final Integer port;
    private final String rdvKey;

    public ChannelImpl(CircularBuffer circularBufferIn, CircularBuffer circularBufferOut, BrokerImpl broker, Integer port, String key) {
        this.circularBufferIn = circularBufferIn;
        this.circularBufferOut = circularBufferOut;
        this.broker = broker;
        this.port = port;
        this.rdvKey = key;
    }

    @Override
    public int read(byte[] bytes, int offset, int length) {
        if (disconnected()) {
            return -1;
        }
        if (offset + length > bytes.length) {
            throw new ArrayIndexOutOfBoundsException("Read buffer too small for " + offset + length + " byte.");
        }
        synchronized (circularBufferIn) {
            while (circularBufferIn.empty()) {
                try {
                    circularBufferIn.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            int i;
            for (i = 0; i < length; i++) {
                bytes[i + offset] = circularBufferIn.pull();
                if (circularBufferIn.empty()) {
                    i++;
                    break;
                }
            }
            if (circularBufferIn.full()) {
                circularBufferIn.notify();
            }
            return i;
        }
    }

    @Override
    public int write(byte[] bytes, int offset, int length) {
        if (disconnected()) {
            return -1;
        }
        if (offset + length > bytes.length) {
            throw new ArrayIndexOutOfBoundsException("Write buffer too small for " + offset + length + " byte.");
        }
        synchronized (circularBufferOut) {
            while (circularBufferOut.full()) {
                try {
                    circularBufferOut.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            int i;
            for (i = 0; i < length; i++) {
                circularBufferOut.push(bytes[i + offset]);
                if (circularBufferOut.full()) {
                    i++;
                    break;
                }
            }
            //if (circularBufferOut.empty()) {
                circularBufferOut.notify();
            //}
            return i;
        }
    }

    @Override
    public void disconnect() {
        RDV.deleteInstance(rdvKey);
        this.broker.releasePort(this.port);
    }

    @Override
    public boolean disconnected() {
        return !RDV.instanceExist(rdvKey);
    }
}
