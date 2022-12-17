package edu.uga.m2gi.ar.message;

import java.nio.charset.StandardCharsets;

public class testMessage {
    private static final String data1 = "data1";
    private static final String data2 = "data2";


    public static void main(String[] args) {
        //Creation des deux brokers et des deux taches
        QueueBroker broker1 = new QueueBroker("broker1");
        QueueBroker broker2 = new QueueBroker("broker2");

        //Lancement des Threads
        new MessageTask(broker1) {
            @Override
            public void run() {
                MessageQueue messageQueue = broker1.connect(broker2.m_name, 666);
                write(messageQueue, data1);
                write(messageQueue, data1);
                read(messageQueue, data2.length());
                messageQueue.close();
            }
        }.start();

        new MessageTask(broker2) {
            @Override
            public void run() {
                MessageQueue messageQueue = broker2.accept(666);
                read(messageQueue, data1.length());
                read(messageQueue, data1.length());
                write(messageQueue, data2);
                messageQueue.close();
            }
        }.start();
    }

    private static void write(MessageQueue channel, String message) {
        System.out.println("Send called for message: " + message);
        channel.send(message.getBytes(), 0, message.length());
    }

    private static void read(MessageQueue channel, int length) {
        System.out.println("Receive called");
        System.out.println("Message received: " + new String(channel.receive(), StandardCharsets.UTF_8));
    }

}
