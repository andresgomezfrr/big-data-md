package com.kafka.api.consumer.groups;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Consumer {
    public static String KAFKA_HOST = "openwebinars:9092,openwebinars1:9092,openwebinars2:9092";
    public static String TOPIC = "openwebinars";
    public static Integer THREADS = 1;
    public static List<KafkaConsumerRunner> consumers = new ArrayList<>();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                System.out.println("Shutting down");
                for(KafkaConsumerRunner consumerRunner : consumers) consumerRunner.shutdown();
            }
        });

        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_HOST);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "openwebinars-group-2");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        for (Integer threads = 0; threads < THREADS; threads++) {
            KafkaConsumerRunner consumerRunner = new KafkaConsumerRunner(threads, consumer, TOPIC);
            consumers.add(consumerRunner);
            executor.submit(consumerRunner);
        }
    }
}
