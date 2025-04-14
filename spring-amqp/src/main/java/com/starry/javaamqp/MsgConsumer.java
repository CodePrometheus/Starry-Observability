package com.starry.javaamqp;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MsgConsumer {

  @Autowired
  private RedisTemplate redisTemplate;
  
  @RabbitListener(queues = "${mq.email.queue-name}", containerFactory = "singleListenerContainer")
  public void process(String msg) {
    System.out.println("【消费者接收的消息】: " + msg);
    redisTemplate.opsForValue().set("pulsar", msg);
  }
}
