package com.github.codeprometheus;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Consumer {
  private final static String QUEUE_NAME = "test_queue";

  public static void main(String[] argv) throws Exception {
    // 创建连接工厂
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost"); // RabbitMQ 服务器地址
    factory.setUsername("admin"); // 默认用户名
    factory.setPassword("123456"); // 默认密码

    // 创建连接
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    // 声明队列
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    // 定义消息处理回调
    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), "UTF-8");
      System.out.println(" [x] Received '" + message + "'");
    };

    // 开始消费消息
    channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
    });
  }
}
