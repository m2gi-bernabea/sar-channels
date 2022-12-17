package edu.uga.m2gi.ar.channels.test;

import edu.uga.m2gi.ar.channels.Broker;
import edu.uga.m2gi.ar.channels.BrokerImpl;
import edu.uga.m2gi.ar.channels.Channel;
import edu.uga.m2gi.ar.channels.Task;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;

import static org.testng.AssertJUnit.assertEquals;

public class ChannelTest {

    private static final byte[] data = "hello world".getBytes();


    @Test
    public void testTaskCommunication() throws InterruptedException {
        // Create a broker to connect the tasks
        Broker broker1 = new BrokerImpl("broker1");
        Broker broker2 = new BrokerImpl("broker2");

        // Create the first task and pass the broker to its constructor
        Task task1 = new Task(broker1) {
            @Override
            public void run() {
                Channel channel1 = broker1.connect(broker2.name(), 666);
                // Test the read method of the channel
                byte[] readBuffer = new byte[11];
                channel1.read(readBuffer, 0, 11);
                assertEquals("hello world", new String(readBuffer, StandardCharsets.UTF_8));
            }
        };

        // Create the second task and pass the broker to its constructor
        Task task2 = new Task(broker2) {
            @Override
            public void run() {
                Channel channel2 = broker2.accept(666);
                // Test the write method of the channel
                assertEquals(11, channel2.write(data, 0, 11));
            }
        };
        task1.start();
        task2.start();

        task1.join();
        task2.join();
    }
}
