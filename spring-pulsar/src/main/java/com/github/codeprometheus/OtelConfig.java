package com.github.codeprometheus;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtelConfig {
  @Bean
  public OpenTelemetry openTelemetry() {
    ZipkinSpanExporter zipkin = ZipkinSpanExporter.builder()
      .setEndpoint("http://localhost:9411/api/v2/spans")
      .build();
    SpanProcessor spanProcessor = SimpleSpanProcessor.create(zipkin);
    SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
      .addSpanProcessor(spanProcessor)
      .build();
    return OpenTelemetrySdk.builder()
      .setTracerProvider(tracerProvider)
      .build();
  }
}
