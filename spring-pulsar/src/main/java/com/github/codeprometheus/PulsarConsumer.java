package com.github.codeprometheus;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Service;

@Service
public class PulsarConsumer {

  @Resource
  private RedisTemplate redisTemplate;

  @PulsarListener(subscriptionName = "my-subscription", topics = "my-topic")
  public void receiveMessage(String message) {
    System.out.println("my|Received message: " + message);
    process(message);
  }

  //@WithSpan
  public void process(String msg) {
    redisTemplate.opsForValue().set("pulsar", msg);
  }
}
