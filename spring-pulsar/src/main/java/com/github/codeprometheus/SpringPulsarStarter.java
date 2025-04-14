package com.github.codeprometheus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringPulsarStarter {
  /**
   -javaagent:/Users/zhouzixin/observability/opentelemetry-java-instrumentation/javaagent/build/libs/opentelemetry-javaagent-2.14.0-SNAPSHOT.jar
   -Dotel.javaagent.debug=true
   -Dotel.traces.exporter=zipkin
   -Dotel.logs.exporter=logging
   -Dotel.metrics.exporter=logging
   -Dotel.resource.attributes=service.name=pulsar-spring
   -javaagent:/Users/zhouzixin/agent-framework/pinpoint/agent-module/agent/target/pinpoint-agent-3.1.0-SNAPSHOT/pinpoint-bootstrap-3.1.0-SNAPSHOT.jar
   -Dpinpoint.agentId=pulsar-agentId
   -Dpinpoint.applicationName=pulsar-name
   -Dpinpoint.profiler.profiles.active=local
   -Dprofiler.pulsar.producer.enable=true
   -Dprofiler.pulsar.consumer.enable=true
   -javaagent:/Users/zhouzixin/sw-framework/skywalking-java/skywalking-agent/skywalking-agent.jar
   */
  public static void main(String[] args) {
    SpringApplication.run(SpringPulsarStarter.class, args);
  }
}
