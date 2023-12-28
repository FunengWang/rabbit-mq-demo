package org.example.workqueues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class NewTask {
    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            String message = String.join(" ", args);
            boolean durable =true;
            boolean exclusive = false;
            boolean autoDelete = false;
            channel.queueDeclare(TASK_QUEUE_NAME,durable,exclusive,autoDelete,null);
            System.out.println(" [x] Sent '" + message + "'");

        }
    }
}
