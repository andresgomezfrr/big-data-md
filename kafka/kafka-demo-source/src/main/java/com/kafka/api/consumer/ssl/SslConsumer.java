package com.kafka.api.consumer.ssl;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SslConfigs;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class SslConsumer {
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

        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/Users/andresgomezfrr/Openwebinars/Kafka/sesiones/client.truststore.jks");
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "openwebinars");
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "/Users/andresgomezfrr/Openwebinars/Kafka/sesiones/client.keystore.jks");
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "openwebinars");
        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "openwebinars");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC));

        while (!closed.get()) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("partition = %2d   offset = %5d   key = %7s   value = %12s\n",
                        record.partition(), record.offset(), record.key(), record.value());
        }

        consumer.close();
    }
}
