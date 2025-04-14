package com.starry.webspring;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j
public record PulsarTracesExporter(PulsarClient client,
                                   Producer<byte[]> producer) implements SpanExporter {

    @Override
    public CompletableResultCode export(Collection<SpanData> spans) {
        CompletableResultCode result = new CompletableResultCode();

        for (SpanData span : spans) {
            byte[] value;
            try {
                // 将 SpanData 转换为字节数组
                value = serializeSpan(span);
            } catch (IOException e) {
                log.error("Failed to serialize span: " + span.getSpanContext(), e);
                result.fail();
                continue; // 继续处理下一个 span
            }

            producer.newMessage()
                    .value(value)
                    .sendAsync()
                    .whenComplete((__, t) -> {
                        if (t == null) {
                            result.succeed();
                        } else {
                            log.error("Failed to send message to Pulsar", t);
                            result.fail();
                        }
                    });
        }

        return result;
    }

    private byte[] serializeSpan(SpanData span) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 获取 SpanContext
            var spanContext = span.getSpanContext();

            // 获取 SpanId 和 TraceId
            String spanId = spanContext.getSpanId(); // SpanId 是 String
            String traceId = spanContext.getTraceId(); // TraceId 也是 String

            // 序列化逻辑
            String serializedData = String.format("TraceId: %s, SpanId: %s", traceId, spanId);
            outputStream.write(serializedData.getBytes(StandardCharsets.UTF_8));

            return outputStream.toByteArray();
        }
    }

    @Override
    public CompletableResultCode flush() {
        var result = new CompletableResultCode();
        this.producer.flushAsync()
                .whenComplete((__, t) -> {
                    if (null == t) {
                        result.succeed();
                    } else {
                        result.fail();
                    }
                });

        return result;
    }

    @Override
    public CompletableResultCode shutdown() {
        var result = new CompletableResultCode();
        this.producer.closeAsync()
                .thenApply(__ -> this.client.closeAsync())
                .thenAccept(__ -> result.succeed())
                .exceptionally(__ -> {
                    result.fail();
                    return null;
                });

        return result;
    }
}