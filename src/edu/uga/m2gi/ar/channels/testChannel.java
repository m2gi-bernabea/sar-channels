package edu.uga.m2gi.ar.channels;

import java.util.Arrays;

//TODO : Regler le problème de current thread is not owner à l'éxécution ... La Task2 n'arrive pas à trouver le RDV
public class testChannel {
    private static final byte[] data = "hello world".getBytes();

    public static void main(String[] args) {
        //Creation des deux brokers et des deux taches
        BrokerImpl broker1 = new BrokerImpl("broker1");
        BrokerImpl broker2 = new BrokerImpl("broker2");

        //Lancement des Threads
        new Task(broker1) {
            @Override
            public void run() {
                Channel channel1 = m_broker.connect(broker2.m_name, 666);
                System.out.println("Write in channel 1 values : " + Arrays.toString(data));
                System.out.println("Result of write : " + channel1.write(data, 0, 11));
            }
        }.start();

        new Task(broker2) {
            @Override
            public void run() {
                Channel channel2 = m_broker.accept(666);
                System.out.println("Read in channel2 : wait while buffer is empty");
                byte[] readBuffer = new byte[11];
                channel2.read(readBuffer, 0, 11);
                System.out.println("Values read " + Arrays.toString(readBuffer));
            }
        }.start();
    }
}
