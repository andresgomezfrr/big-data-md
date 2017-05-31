package com.kafka.streams.api.dsl.enricher;

import com.kafka.streams.api.serializers.JsonSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Application {
    public static void main(String[] args) {
        final JsonSerde jsonSerde = new JsonSerde();

        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, args[0]);
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, args[1]);
        streamsConfiguration.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, jsonSerde.getClass().getName());
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        final Serde<String> stringSerde = Serdes.String();

        final KStreamBuilder builder = new KStreamBuilder();

        final KStream<String, Map<String, Object>> appStream = builder.stream(stringSerde, jsonSerde, "app-data");
        final KTable<String, Map<String, Object>> locTable = builder.table(stringSerde, jsonSerde, "loc-data", "locations");

        final KStream<String, Map<String, Object>> enrichStream = appStream.leftJoin(locTable, (app, loc) -> {
            Map<String, Object> newMessage = new HashMap<>();
            if(loc != null){
                newMessage.putAll(loc);
                newMessage.putAll(app);
            } else {
                newMessage.putAll(app);
            }
            return newMessage;
        });

        enrichStream.to(stringSerde, jsonSerde, "enrich-data");

        final KafkaStreams streams = new KafkaStreams(builder, streamsConfiguration);

        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
