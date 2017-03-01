package com.kafka.api.consumer.simple;

import org.apache.kafka.clients.consumer.*;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleConsumer {
    public static String KAFKA_HOST = "openwebinars:9092,openwebinars1:9092,openwebinars2:9092";
    public static String TOPIC = "openwebinars";
    private static final AtomicBoolean closed = new AtomicBoolean(false);

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                System.out.println("Shutting down");
                closed.set(true);
            }
        });

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_HOST);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "openwebinars-new");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC));

        while (!closed.get()) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("partition = %2d   offset = %5d   key = %7s timestamp = %8s  value = %12s\n",
                        record.partition(), record.offset(), record.key(), String.valueOf(record.timestamp()), record.value());
        }

        consumer.close();
    }
}
