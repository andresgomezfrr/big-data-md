package com.kafka.streams.api.processor.wordcount;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

public class WordCountProcessor implements Processor<String, String> {
    KeyValueStore<String, Long> store;
    ProcessorContext context;

    @Override
    public void init(ProcessorContext processorContext) {
        context = processorContext;
        processorContext.schedule(5000);
        store = (KeyValueStore<String, Long>) processorContext.getStateStore("Counts");
    }

    @Override
    public void process(String key, String value) {
        for(String word : value.split(" ")){
            Long count = store.get(word);

            if(count == null){
                store.put(word, 1L);
                context.forward(word, "1");
            } else {
                store.put(word, count + 1);
                context.forward(word, count.toString());
            }

        }

        context.commit();
    }


    @Override
    public void punctuate(long l) {
        KeyValueIterator<String, Long> iter = store.all();
        while (iter.hasNext()){
            KeyValue<String, Long> kv = iter.next();
            System.out.println("KEY: "  + kv.key + " VALUE: " + kv.value);
        }
    }

    @Override
    public void close() {

    }
}
