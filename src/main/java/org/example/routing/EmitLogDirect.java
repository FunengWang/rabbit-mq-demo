package org.example.routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class EmitLogDirect {
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            String severity = null,message = null;

            for (int i=1;i<=15;i++){
               if(i%2==0){
                   if(i%4==0){
                       severity="error";
                       message="Error: The "+i+"-th message!";
                       channel.basicPublish(EXCHANGE_NAME,severity,null,message.getBytes(StandardCharsets.UTF_8));
                       System.out.println(" [x] Sent '" + severity + "':'" + message + "'");
                   }else {
                       severity = "warning";
                       message = "Warning: The "+i+"-th message!";
                       channel.basicPublish(EXCHANGE_NAME,severity,null,message.getBytes(StandardCharsets.UTF_8));
                       System.out.println(" [x] Sent '" + severity + "':'" + message + "'");
                   }
               }else{
                   severity = "info";
                   message = "Info: The "+i+"-th message!";
                   channel.basicPublish(EXCHANGE_NAME,severity,null,message.getBytes(StandardCharsets.UTF_8));
                   System.out.println(" [x] Sent '" + severity + "':'" + message + "'");
               }
            }
        }
    }
}
