package com.github.codeprometheus;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PulsarRest {

  private final PulsarProducerService pulsarService;

  public PulsarRest(PulsarProducerService pulsarService) {
    this.pulsarService = pulsarService;
  }

  @GetMapping("/send")
  public String send() throws PulsarClientException {
    // pulsarService.sendMessage("my-topic", "Hello, Pulsar!");
    System.out.println("my|Producer message: Hello, Pulsar!");
    PulsarClient.builder()
      .serviceUrl("pulsar://localhost:6650")
      .build()
      .newProducer()
      .topic("my-topic").create()
      .newMessage()
      .value("Hello, Pulsar!".getBytes())
      .send();
    return "success";
  }
}
