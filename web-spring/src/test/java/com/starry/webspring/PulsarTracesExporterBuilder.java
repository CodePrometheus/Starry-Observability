package com.starry.webspring;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;

public class PulsarTracesExporterBuilder {

    public static PulsarTracesExporterBuilder builder() {
        return new PulsarTracesExporterBuilder();
    }

    private String topic = "persistent://public/default/my-topic";
    private String serviceURL = "pulsar://localhost:6650";
    private boolean enableBatch = true;

    private PulsarTracesExporterBuilder() {
    }

    public PulsarTracesExporterBuilder withTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public PulsarTracesExporterBuilder withServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
        return this;
    }

    public PulsarTracesExporterBuilder withEnableBatch(boolean enableBatch) {
        this.enableBatch = enableBatch;
        return this;
    }

    public PulsarTracesExporter build() {
        try {
            var client = PulsarClient.builder()
                    .serviceUrl(serviceURL)
                    .build();

            var producer = client.newProducer(Schema.BYTES)
                    .topic(this.topic)
                    .enableBatching(this.enableBatch)
                    .create();

            return new PulsarTracesExporter(client, producer);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}