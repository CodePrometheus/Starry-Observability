/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.starry.webspring;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.common.Clock;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.IdGenerator;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanLimits;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.time.Duration;

@Slf4j
public class PulsarExporterTest {
    private static final String INSTRUMENTATION_NAME = "otlp-pulsar_exporter";
    private static final int MAX_TRACES = 200;
    private static final int MAX_SPANS = 10;

    @BeforeAll
    public static void setup() {
        var baggage = W3CBaggagePropagator.getInstance();
        var trace = W3CTraceContextPropagator.getInstance();

        var propagator = TextMapPropagator.composite(trace, baggage);
        var propagators = ContextPropagators.create(propagator);

        var exporter = PulsarTracesExporterBuilder.builder()
                .withEnableBatch(true)
                .withServiceURL("pulsar://localhost:6650")
                .withTopic("persistent://public/default/my-topic")
                .build();

        var processor = BatchSpanProcessor.builder(exporter)
                .setExporterTimeout(Duration.ofSeconds(5))
                .setMaxExportBatchSize(100)
                .build();

        var attributes = Attributes.of(
                AttributeKey.stringKey("service.name"), INSTRUMENTATION_NAME,
                AttributeKey.stringKey("service.env"), "dev");

        var provider = SdkTracerProvider.builder()
                .setClock(Clock.getDefault())
                .setIdGenerator(IdGenerator.random())
                .setSpanLimits(SpanLimits.getDefault())
                .setSampler(Sampler.alwaysOn())
                .setResource(Resource.create(attributes))
                .addSpanProcessor(processor)
                .build();

        OpenTelemetrySdk.builder()
                .setPropagators(propagators)
                .setTracerProvider(provider)
                .buildAndRegisterGlobal();

        log.info("Open Telemetry Completing...");
    }

    @Test
    public void testSendTraces() throws Exception {
        var tracer = GlobalOpenTelemetry.getTracer(INSTRUMENTATION_NAME);
        for (var i = 0; i < MAX_TRACES; i++) {
            var parent = tracer.spanBuilder(INSTRUMENTATION_NAME + "_trace_" + i)
                    .setSpanKind(SpanKind.INTERNAL)
                    .setNoParent()
                    .startSpan();

            try (var _ignore = parent.makeCurrent()) {
                for (var j = 0; j < MAX_SPANS; j++) {
                    var child = tracer.spanBuilder("Child_" + j)
                            .setParent(Context.current())
                            .setSpanKind(SpanKind.INTERNAL)
                            .startSpan();

                    Thread.sleep(1);
                    child.end();
                }
            }
            parent.end();
        }

        log.info("Test Finished...");
    }
}
