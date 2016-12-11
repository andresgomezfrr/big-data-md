package com.kafka.api.consumer.info;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class InfoConsumer {
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
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "openwebinars-simple");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        for(Map.Entry<String, List<PartitionInfo>> entry : consumer.listTopics().entrySet()){
            System.out.println("Topic: " + entry.getKey());
            for(PartitionInfo partition : entry.getValue()) {
                Set<Integer> replicas = new HashSet<>();
                Set<Integer> inSync = new HashSet<>();

                for(Node node : partition.replicas()) replicas.add(node.id());
                for(Node node : partition.inSyncReplicas()) inSync.add(node.id());

                System.out.println(String.format("  P: %2s   Leader: %2s   Replicas: %4s   InSync: %4s",
                        partition.partition(), partition.leader().id(), replicas, inSync));
            }
        }

        System.out.println("---------------------------");
        System.out.println("---------------------------");

        TopicPartition topic = new TopicPartition("openwebinars", 0);
        System.out.println(String.format("Last offsets for %s : %s", topic, consumer.committed(topic)));

        consumer.close();
    }
}

