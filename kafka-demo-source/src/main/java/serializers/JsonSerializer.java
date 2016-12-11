package serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JsonSerializer implements Serializer<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(JsonDeserializer.class);
    ObjectMapper mapper = new ObjectMapper();

    public void configure(Map<String, ?> configs, boolean isKey) {
        // Nothing to do
    }

    public byte[] serialize(String topic, Map<String, Object> data) {
        byte[] raw = null;
        try {
            raw = mapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return raw;
    }

    public void close() {
        // Nothing to do
    }
}
