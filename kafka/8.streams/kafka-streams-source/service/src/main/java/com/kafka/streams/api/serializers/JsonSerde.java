package com.kafka.streams.api.serializers;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class JsonSerde implements Serde<Map<String, Object>> {

    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    public void close() {

    }

    public Serializer<Map<String, Object>> serializer() {
        return new JsonSerializer();
    }

    public Deserializer<Map<String, Object>> deserializer() {
        return new JsonDeserializer();
    }
}
