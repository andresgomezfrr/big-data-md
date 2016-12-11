package com.kafka.api.consumer.seek;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class SeekConsumer {
    public static String KAFKA_HOST = "openwebinars:9092,openwebinars1:9092,openwebinars2:9092";
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
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "500");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "manual-openwebinars");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        Set<TopicPartition> partitions = new HashSet<>();
        partitions.add(new TopicPartition("A", 0));
        partitions.add(new TopicPartition("B", 1));
        partitions.add(new TopicPartition("C", 0));
        partitions.add(new TopicPartition("C", 1));
        consumer.assign(partitions);

        consumer.seekToBeginning(Collections.singleton(new TopicPartition("C", 0)));
        consumer.seekToEnd(Collections.singleton(new TopicPartition("C", 1)));

        consumer.seek(new TopicPartition("A", 0), 6);
        consumer.seek(new TopicPartition("B", 1), 10);

        while (!closed.get()) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("topic = %2s partition = %2d   offset = %5d   key = %7s   value = %12s\n",
                        record.topic(), record.partition(), record.offset(), record.key(), record.value());
            }
        }

        consumer.close();
    }
}
