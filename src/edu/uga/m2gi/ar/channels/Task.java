package edu.uga.m2gi.ar.channels;

public abstract class Task extends Thread {
	Broker m_broker;
	public Task(Broker broker) {
		super(broker.name());
		m_broker = broker;
	}
	public abstract void run();
}
