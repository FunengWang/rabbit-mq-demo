package org.example.topics;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class EmitLogTopic {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            String routingKey = null,message=null;

            for (int i=0;i<=5;i++) {
                if(i==0){
                    routingKey=message="quick.range.rabbit";

                }else if(i==1){
                    routingKey=message="lazy.orange.elephant";
                }else if(i==2){
                    routingKey=message="quick.orange.fox";
                }else if(i==3){
                    routingKey=message="lazy.brown.fox";
                }else  if(i==4){
                    routingKey=message="lazy.pink.rabbit";
                } else if (i==5) {
                    routingKey=message="quick.brown.fox";
                }
                channel.basicPublish(EXCHANGE_NAME,routingKey,null,message.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");

            }

        }
    }
}
