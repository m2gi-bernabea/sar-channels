package edu.uga.m2gi.ar.message;

public abstract class MessageTask extends Thread {
    QueueBroker m_broker;

    public MessageTask(QueueBroker broker) {
        super(broker.m_name);
        m_broker = broker;
    }

    public abstract void run();
}
