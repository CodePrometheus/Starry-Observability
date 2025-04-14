package com.starry.javaamqp;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQConfig {

  @Resource
  private CachingConnectionFactory connectionFactory;

  @Value("${mq.email.queue-name}")
  private String queueName;
  @Value("${mq.email.exchange-name}")
  private String exchangeName;
  @Value("${mq.email.routing-key-name}")
  private String routingKeyName;

  @Bean(name = "singleListenerContainer")
  public SimpleRabbitListenerContainerFactory listenerContainer() {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(new Jackson2JsonMessageConverter());
    factory.setConcurrentConsumers(1);
    factory.setMaxConcurrentConsumers(1);
    factory.setPrefetchCount(1);
    factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
    return factory;
  }

  @Bean
  public RabbitTemplate rabbitTemplate() {
    // connectionFactory.setPublisherConfirms(true);
    connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.SIMPLE);
    connectionFactory.setPublisherReturns(true);
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMandatory(true);
    rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> log.info("消息发送成功: correlationData({}), ack({}), cause({})", correlationData, ack, cause));
    // rabbitTemplate.setReturnsCallback((message, replyCode, replyText, exchange, routingKey) -> log.info("消息丢失: exchange({}), route({}), replyCode({}), replyText({}), message:{}", exchange, routingKey, replyCode, replyText, message));
    return rabbitTemplate;
  }

  @Bean
  public Queue emailQueue() {
    return new Queue(queueName, true);
  }

  @Bean
  public DirectExchange emailExchange() {
    return new DirectExchange(exchangeName, true, false);
  }

  @Bean
  public Binding emailBinding() {
    return BindingBuilder.bind(emailQueue()).to(emailExchange()).with(routingKeyName);
  }
}
