package com.kafka.api.producer.simple;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Producers {
    public static String brokerList = "openwebinars:9092,openwebinars1:9092,openwebinars2:9092";
    public static String topic = "openwebinars";

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.kafka.api.producer.SimplePartitioner");

        Producer<String, String> producer = new KafkaProducer<>(props);

        for (int id = 0; id < 5000; id++) {
            String key = String.format("key[%d]", id);
            String message = String.format("message[%d]", id);
            System.out.println("Sending message with: " + key);
            producer.send(new ProducerRecord<>(topic, key, message));
            Thread.sleep(1000);
        }

        producer.flush();
        producer.close();
    }
}
