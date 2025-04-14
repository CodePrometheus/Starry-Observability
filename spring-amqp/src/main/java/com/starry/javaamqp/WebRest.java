package com.starry.javaamqp;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebRest {

  @Value("${mq.email.exchange-name}")
  private String exchangeName;

  @Value("${mq.email.routing-key-name}")
  private String routingKeyName;

  @Resource
  private RabbitTemplate rabbitTemplate;

  @GetMapping("/test")
  public void test() {
    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    rabbitTemplate.setExchange(exchangeName);
    rabbitTemplate.setRoutingKey(routingKeyName);
    rabbitTemplate.convertAndSend("我是队列里的一条消息");
  }
}
