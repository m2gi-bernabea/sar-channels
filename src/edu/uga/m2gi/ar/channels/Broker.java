package edu.uga.m2gi.ar.channels;

public abstract class Broker {
	String m_name;

	public Broker(String name) {
		m_name = name;
		//throw new RuntimeException("Not Implemented Yet");
	}

	public String name() {
		return m_name;
	}

	public Channel accept(int port) {
		throw new RuntimeException("Not Implemented Yet");
	}

	public Channel connect(String name, int port) {
		throw new RuntimeException("Not Implemented Yet");
	}
}
