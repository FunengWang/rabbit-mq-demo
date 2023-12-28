package org.example.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class RPCClient implements AutoCloseable{
    private Connection connection;
    private Channel channel;
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        this.connection = factory.newConnection();
        this.channel = this.connection.createChannel();
    }

    public static void main(String[] args) {
        try (RPCClient fibonacciRpc = new RPCClient()){
            for (int i = 0; i < 32; i++) {
                String str = Integer.toString(i);
                System.out.println(" [x] Requesting fib(" + str + ")");
                String response = fibonacciRpc.call(str);
                System.out.println(" [.] Got '" + response + "'");
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public String call(String message) throws IOException, ExecutionException, InterruptedException {
        String uuid = UUID.randomUUID().toString();
        String replyQueueName = this.channel.queueDeclare().getQueue();
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties()
                .builder()
                .correlationId(uuid)
                .replyTo(replyQueueName)
                .build();
        this.channel.basicPublish("",RPC_QUEUE_NAME,false,basicProperties,message.getBytes(StandardCharsets.UTF_8));

        CompletableFuture<String> response = new CompletableFuture<>();
        DeliverCallback deliverCallback = (consumerTag,delivery)->{
            if(delivery.getProperties().getCorrelationId().equals(uuid)){
                response.complete(new String(delivery.getBody(),StandardCharsets.UTF_8));
            }
        };
        String consumerTag = this.channel.basicConsume(replyQueueName, false, deliverCallback, (ctag -> {}));

        String result = response.get();
        this.channel.basicCancel(consumerTag);
        return result;
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }
}
