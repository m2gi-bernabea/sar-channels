package edu.uga.m2gi.ar.message;

import java.nio.charset.StandardCharsets;
import java.util.List;

abstract class Server extends MessageTask {
    private final MessageQueue messageQueue;
    private final List<Client> clients;

    public Server(QueueBroker queueBroker, MessageQueue messageQueue, List<Client> clients) {
        super(queueBroker);
        this.messageQueue = messageQueue;
        this.clients = clients;
    }

    public String receiveMessage() {
        return new String(messageQueue.receive(), StandardCharsets.UTF_8);
    }

    public void sendMessage(String message) {
        for (Client client : clients) {
            client.sendMessage(message.getBytes());
        }
    }
}
