package com.github.codeprometheus;

import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.stereotype.Service;

@Service
public class PulsarProducerService {

  private final PulsarTemplate<String> pulsarTemplate;

  public PulsarProducerService(PulsarTemplate<String> pulsarTemplate) {
    this.pulsarTemplate = pulsarTemplate;
  }

  public void sendMessage(String topic, String message) {
    pulsarTemplate.send(topic, message);
    System.out.println("my|Producer message: " + message);
  }
}
