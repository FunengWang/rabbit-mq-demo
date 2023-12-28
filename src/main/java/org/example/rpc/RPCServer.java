package org.example.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RPCServer {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static int fibonacci(int n){
        if (n == 0) return 0;
        if (n == 1) return 1;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(RPC_QUEUE_NAME,false,false,false,null);

        System.out.println(" [x] Awaiting RPC requests");

        DeliverCallback deliverCallback = (consumerTag,delivery)->{
            String deliveryMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
            int parseInt = Integer.parseInt(deliveryMessage);
            String response="";
            response+= fibonacci(parseInt);
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties()
                    .builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();
            channel.basicPublish("",delivery.getProperties().getReplyTo(),basicProperties,response.getBytes(StandardCharsets.UTF_8));
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
        };

        channel.basicConsume(RPC_QUEUE_NAME,false,deliverCallback,(consumerTag -> {}));

    }
}
