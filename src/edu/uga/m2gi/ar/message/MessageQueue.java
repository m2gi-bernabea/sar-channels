package edu.uga.m2gi.ar.message;

import edu.uga.m2gi.ar.channels.ChannelImpl;
import edu.uga.m2gi.ar.channels.CircularBuffer;
import edu.uga.m2gi.ar.channels.RDV;

import java.nio.ByteBuffer;

public class MessageQueue {
    private final CircularBuffer writeBuffer;
    private final CircularBuffer readBuffer;
    private final QueueBroker queueBroker;
    private final int port;
    private final String rdvKey;
    private ChannelImpl channel;
    private boolean closed;

    /**
     * Constructs a new MessageQueue with the given read and write buffer,
     * QueueBroker, port, and RDVKey.
     *
     * @param readBuffer  the read buffer
     * @param writeBuffer the write buffer
     * @param queueBroker the QueueBroker that this MessageQueue is connected to
     * @param port        the port used for communication
     * @param rdvKey      the RDVKey used to establish the connection
     */
    public MessageQueue(CircularBuffer readBuffer, CircularBuffer writeBuffer, QueueBroker queueBroker, int port, String rdvKey) {
        this.readBuffer = readBuffer;
        this.writeBuffer = writeBuffer;
        this.queueBroker = queueBroker;
        this.port = port;
        this.rdvKey = rdvKey;
        this.closed = false;
    }

    /**
     * Sends the specified bytes to the connected task.
     *
     * @param bytes  the bytes to send
     * @param offset the offset at which to start sending the bytes
     * @param length the number of bytes to send
     */
    public void send(byte[] bytes, int offset, int length) {
        if (closed) {
            return;
        }
        if (offset + length > bytes.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        synchronized (writeBuffer) {
            while (writeBuffer.full()) {
                try {
                    writeBuffer.wait(10000);
                    if (writeBuffer.full()) {
                        throw new RuntimeException("Write buffer full during more than 10 seconds");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            byte[] heading = new byte[4];
            ByteBuffer.wrap(heading).putInt(length);
            for (byte b : heading) {
                writeBuffer.push(b);
            }

            System.out.println("Message length send: " + length);


            int i;
            for (i = 0; i < length; i++) {
                writeBuffer.push(bytes[i + offset]);
                if (writeBuffer.full()) {
                    i++;
                    break;
                }
            }
            writeBuffer.notifyAll();
        }
    }

    /**
     * Receives bytes from the connected task.
     *
     * @return the received bytes, or null if the MessageQueue is closed or there
     * are no bytes to receive
     */
    public byte[] receive() {
        if (closed) {
            return null;
        }
        synchronized (readBuffer) {
            while (readBuffer.empty()) {
                try {
                    readBuffer.wait(10000);
                    if (readBuffer.empty()) {
                        throw new RuntimeException("No message was send since 10 seconds");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            byte[] heading = new byte[4];
            for (int i = 0; i < heading.length; i++) {
                heading[i] = readBuffer.pull();
            }
            int messageLength = ByteBuffer.wrap(heading).getInt();

            System.out.println("Message length receive: " + messageLength);

            int i;
            byte[] bytes = new byte[messageLength];
            for (i = 0; i < messageLength; i++) {
                bytes[i] = readBuffer.pull();
                if (readBuffer.empty()) {
                    i++;
                    break;
                }
            }
            readBuffer.notifyAll();
            return bytes;
        }
    }

    /**
     * Closes the MessageQueue.
     */
    public void close() {
        RDV.deleteInstance(rdvKey);
        this.queueBroker.releasePort(this.port);
        closed = true;
    }

    public boolean closed() {
        return closed;
    }
}

