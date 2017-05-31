package com.kafka.streams.api.processor.enricher;

import com.kafka.streams.api.serializers.JsonSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.Properties;

public class Application {
    public static void main(String[] args) {
        JsonSerde jsonSerde = new JsonSerde();

        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, args[0]);
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, args[1]);
        streamsConfiguration.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, jsonSerde.getClass().getName());
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);

        TopologyBuilder builder = new TopologyBuilder();

        StateStoreSupplier locationStore = Stores.create("locations")
                .withKeys(Serdes.String())
                .withValues(jsonSerde)
                .persistent()
                .build();

        builder
                .addSource("App-Source", "app-data")
                .addSource("Loc-Source", "loc-data")
                .addProcessor("Querier", QueryProcessor::new, "App-Source")
                .addProcessor("Storer", StoreProcessor::new, "Loc-Source")
                .addStateStore(locationStore, "Querier", "Storer")
                .addSink("Sink", "enrich-data", Serdes.String().serializer(), jsonSerde.serializer(), "Querier");


        final KafkaStreams streams = new KafkaStreams(builder, streamsConfiguration);
        streams.start();
    }
}
