package com.kafka.api.producer.ssl;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SslProducers {
    public static String KAFKA_HOST = "openwebinars:9092,openwebinars1:9092,openwebinars2:9092";
    public static String topic = "openwebinars";

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_HOST);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.kafka.api.producer.SimplePartitioner");

        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/Users/andresgomezfrr/Openwebinars/Kafka/sesiones/client.truststore.jks");
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "openwebinars");
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "/Users/andresgomezfrr/Openwebinars/Kafka/sesiones/client.keystore.jks");
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "openwebinars");
        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "openwebinars");

        Producer<String, String> producer = new KafkaProducer<>(props);

        for (int id = 0; id < 100; id++) {
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
