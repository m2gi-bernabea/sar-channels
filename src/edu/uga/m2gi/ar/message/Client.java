package edu.uga.m2gi.ar.message;

import java.nio.charset.StandardCharsets;
import java.util.Random;

class Client extends MessageTask {
    public final int id;
    public final MessageQueue messageQueue;

    public Client(int id, QueueBroker broker, MessageQueue messageQueue) {
        super(broker);
        this.id = id;
        this.messageQueue = messageQueue;
    }

    public void sendMessage(byte[] message) {
        messageQueue.send(message, 0, message.length);
    }

    public String receiveMessage() {
        return new String(messageQueue.receive(), StandardCharsets.UTF_8);
    }

    @Override
    public void run() {
        for (int i = 0; i < 496; i++) {
            int messageLength = 16 + i;
            byte[] messageContent = new byte[messageLength];
            new Random().nextBytes(messageContent);
            sendMessage(messageContent);
        }
        // Receive messages from the server
        System.out.println("message received: " + receiveMessage() + " on task number: " + id);
        messageQueue.close();
    }
}
