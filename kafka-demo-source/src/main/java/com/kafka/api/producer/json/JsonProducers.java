package com.kafka.api.producer.json;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class JsonProducers {
    public static String KAFKA_HOST = "openwebinars:9092,openwebinars1:9092,openwebinars2:9092";
    public static String topic = "openwebinars";

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_HOST);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "serializers.JsonSerializer");
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.kafka.api.producer.SimplePartitioner");

        Producer<String, Map<String, Object>> producer = new KafkaProducer<>(props);

        Map<String, Object> myJson = new HashMap<>();
        myJson.put("someData", "Values");

        List<String> array = new ArrayList<>();
        array.add("A");
        array.add("B");
        array.add("C");
        myJson.put("array", array);

        for (int id = 0; id < 100; id++) {
            myJson.put("id", id);
            String key = String.format("key[%d]", id);
            System.out.println("Sending message with: " + key);
            producer.send(new ProducerRecord<>(topic, key, myJson));
            Thread.sleep(1000);
        }

        producer.flush();
        producer.close();
    }
}
