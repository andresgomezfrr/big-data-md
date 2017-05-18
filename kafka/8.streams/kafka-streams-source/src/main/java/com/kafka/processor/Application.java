package com.kafka.processor;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.Properties;

public class Application {
    public static void main(String[] args) {
        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-lambda-example");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        streamsConfiguration.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);

        TopologyBuilder builder = new TopologyBuilder();

        StateStoreSupplier countStore = Stores.create("Counts")
                .withKeys(Serdes.String())
                .withValues(Serdes.Long())
                .persistent()
                .build();

        builder.addSource("Source", "multiple")
                .addProcessor("Process", WordCountProcessor::new, "Source")
                .addStateStore(countStore, "Process")
                .addSink("Sink", "sink-topic", Serdes.String().serializer(), Serdes.Long().serializer(), "Process");


        final KafkaStreams streams = new KafkaStreams(builder, streamsConfiguration);
        streams.start();
    }
}
