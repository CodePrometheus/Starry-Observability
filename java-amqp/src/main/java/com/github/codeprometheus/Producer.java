package com.github.codeprometheus;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {
  private final static String QUEUE_NAME = "test_queue";

  public static void main(String[] argv) throws Exception {
    // 创建连接工厂
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost"); // RabbitMQ 服务器地址
    factory.setUsername("admin"); // 默认用户名
    factory.setPassword("123456"); // 默认密码

    // 创建连接
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      // 声明队列
      channel.queueDeclare(QUEUE_NAME, false, false, false, null);

      // 发送消息
      String message = "Hello, RabbitMQ!";
      channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
      System.out.println(" [x] Sent '" + message + "'");
    }
  }
}
