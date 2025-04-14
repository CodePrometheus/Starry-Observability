package com.starry.webspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class WebSpringApplication {

    /**
     * -javaagent:/Users/zhouzixin/observability/opentelemetry-java-instrumentation/javaagent/build/libs/opentelemetry-javaagent-2.13.0-SNAPSHOT.jar
     * -Dotel.javaagent.debug=true -Dotel.traces.exporter=zipkin -Dotel.logs.exporter=logging -Dotel.metrics.exporter=logging -Dotel.resource.attributes=service.name=zhouzixin
     * 
     * -javaagent:/Users/zhouzixin/sw-framework/skywalking-java/skywalking-agent/skywalking-agent.jar
     */
    public static void main(String[] args) {
        SpringApplication.run(WebSpringApplication.class, args);
    }
}
