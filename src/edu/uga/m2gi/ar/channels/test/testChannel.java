package edu.uga.m2gi.ar.channels.test;

import edu.uga.m2gi.ar.channels.BrokerImpl;
import edu.uga.m2gi.ar.channels.Channel;
import edu.uga.m2gi.ar.channels.Task;

import java.nio.charset.StandardCharsets;

public class testChannel {
    private static final String data1 = "data1";
    private static final String data2 = "data2";


    public static void main(String[] args) {
        //Creation des deux brokers et des deux taches
        BrokerImpl broker1 = new BrokerImpl("broker1");
        BrokerImpl broker2 = new BrokerImpl("broker2");

        //Lancement des Threads
        new Task(broker1) {
            @Override
            public void run() {
                Channel channel1 = broker1.connect(broker2.name(), 666);
                write(channel1, data1);
                write(channel1, data1);
                read(channel1, data2.length());
                channel1.disconnect();
            }
        }.start();

        new Task(broker2) {
            @Override
            public void run() {
                Channel channel2 = broker2.accept(666);
                read(channel2, data1.length());
                read(channel2, data1.length());
                write(channel2, data2);
                channel2.disconnect();
            }
        }.start();
    }

    private static void write(Channel channel, String message) {
        System.out.println("Write called for values: " + message);
        System.out.println("Result of write : " + channel.write(message.getBytes(), 0, message.length()));
    }

    private static void read(Channel channel, int length) {
        System.out.println("Read called");
        byte[] readBuffer = new byte[length];
        channel.read(readBuffer, 0, length);
        System.out.println("Values read: " + new String(readBuffer, StandardCharsets.UTF_8));
    }
}
