package com.kafka.api.producer.ack;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class AckProducers {
    public static String KAFKA_HOST = "openwebinars:9092,openwebinars1:9092,openwebinars2:9092";
    public static String topic = "openwebinars";

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_HOST);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.kafka.api.producer.SimplePartitioner");
        props.put(ProducerConfig.ACKS_CONFIG, "1");

        Producer<String, String> producer = new KafkaProducer<>(props);

        for (int id = 0; id < 100; id++) {
            String key = String.format("key[%d]", id);
            String message = String.format("message[%d]", id);
            System.out.println("Sending message with: " + key);
            producer.send(new ProducerRecord<>(topic, key, message), new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if(exception == null) {
                        System.out.println(String.format("Message wrote into partition[%s] with offset[%s]",
                                metadata.partition(), metadata.offset()));
                    } else {
                        System.out.println("Failed sending message: " + exception.getMessage());
                    }
                }
            });
            Thread.sleep(1000);
        }

        producer.flush();
        producer.close();
    }
}