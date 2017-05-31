package com.kafka.streams.api.processor.enricher;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Map;

public class StoreProcessor implements Processor<String, Map<String, Object>> {
    KeyValueStore<String, Map<String, Object>> locations;

    @Override
    public void init(ProcessorContext context) {
        this.locations = (KeyValueStore<String, Map<String, Object>> ) context.getStateStore("locations");
    }

    @Override
    public void process(String key, Map<String, Object> value) {
        locations.put(key, value);
    }

    @Override
    public void punctuate(long timestamp) {

    }

    @Override
    public void close() {

    }
}
