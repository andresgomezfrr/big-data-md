package com.kafka.streams.api.processor.enricher;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.HashMap;
import java.util.Map;

public class QueryProcessor implements Processor<String, Map<String, Object>> {
    KeyValueStore<String, Map<String, Object>> locations;
    ProcessorContext context;

    @Override
    public void init(ProcessorContext context) {
        this.locations = (KeyValueStore<String, Map<String, Object>> ) context.getStateStore("locations");
        this.context = context;
    }

    @Override
    public void process(String key, Map<String, Object> value) {
        Map<String, Object> lastLocation = locations.get(key);
        Map<String, Object> newMessage = new HashMap<>();

        if(lastLocation != null) {
            newMessage.putAll(lastLocation);
            newMessage.putAll(value);
        } else {
            newMessage.putAll(value);
        }
        context.forward(key, newMessage);
    }

    @Override
    public void punctuate(long timestamp) {

    }

    @Override
    public void close() {

    }
}
