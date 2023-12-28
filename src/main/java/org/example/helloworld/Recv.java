package org.example.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

public class Recv {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        System.out.println("[*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback callback = ((consumerTag, message) -> {
            String s = new String(message.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + s + "'");
            try {
                doWork(s);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                System.out.println("[x] Done");
            }

        });
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME,autoAck, callback, consumerTag -> {});
    }

    static  void doWork(String task) throws InterruptedException{
        for (char ch: task.toCharArray()){
            if(ch=='.')
                Thread.sleep(1000);
        }
    }
}
