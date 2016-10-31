package rabbitmq;
/***************************************************************
 * Copyright (c) 2016 Errigal Inc.
 *
 * This software is the confidential and proprietary information
 * of Errigal, Inc.  You shall not disclose such confidential
 * information and shall use it only in accordance with the
 * license agreement you entered into with Errigal.
 *
 *************************************************************** */

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Colm Carew on 31/10/2016.
 */
public class Rabbit {

    public String queueName = "testQueue";
    public ConnectionFactory factory;
    public Connection connection;
    public Channel channel;

    public Rabbit(String host, String username, String password, String virtualhost, String queueName) throws Exception {
        queueName = queueName;
        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualhost);
        setUpConnection();
        setUpChannel();
        setUpQueue();
    }

    public void setUpConnection() throws Exception {
        connection = factory.newConnection();
    }

    public void setUpChannel() throws Exception {
        channel = connection.createChannel();
    }

    public void setUpQueue() throws Exception {
        channel.queueDeclare(queueName, false, false, false, null);
    }

    public void sendMessage(String message) throws Exception {
        channel.basicPublish("", queueName, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "'");
        closeConnectionAndChannel();

    }

    public void closeConnectionAndChannel() throws Exception {
        channel.close();
        connection.close();
    }

    public void receive() throws Exception {
        System.out.println("Listening on RX Queue");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                try {
                    channel.close();
                    System.out.println("Channel Closed");
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
