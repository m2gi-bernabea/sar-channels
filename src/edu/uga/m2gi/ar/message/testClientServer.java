package edu.uga.m2gi.ar.message;

import java.util.List;

public class testClientServer {
    public static void main(String[] args) {
        QueueBroker broker1 = new QueueBroker("broker1");
        QueueBroker broker2 = new QueueBroker("broker2");
        QueueBroker broker3 = new QueueBroker("broker3");

        Client client1 = new Client(1, broker1, broker1.connect(broker3.m_name, 666));

        Client client2 = new Client(2, broker2, broker2.connect(broker3.m_name, 666));

        client1.start();

        new Server(broker3, broker3.accept(666), List.of(client1, client2)) {
            @Override
            public void run() {
                for (int i = 0; i < 496; i++) {
                    receiveMessage();
                    //sendMessage(receiveMessage()); //Server can resend the message to every client
                }
            }
        }.start();
    }
}
