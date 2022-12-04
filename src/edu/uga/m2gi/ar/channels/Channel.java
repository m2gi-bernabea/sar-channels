package edu.uga.m2gi.ar.channels;

public abstract class Channel {
	public int read(byte[] bytes, int offset, int length) {
		throw new RuntimeException("Not Implemented Yet");
	}
	public int write(byte[] bytes, int offset, int length) {
		throw new RuntimeException("Not Implemented Yet");
	}
	public void disconnect() {
		throw new RuntimeException("Not Implemented Yet");
	}
	public boolean disconnected() { 
		throw new RuntimeException("Not Implemented Yet");
	}
}
