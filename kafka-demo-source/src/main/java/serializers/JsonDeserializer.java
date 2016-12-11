package serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class JsonDeserializer implements Deserializer<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(JsonDeserializer.class);

    ObjectMapper mapper = new ObjectMapper();

    public void configure(Map configs, boolean isKey) {

    }

    public Map<String, Object> deserialize(String topic, byte[] data) {
        Map<String, Object> json = null;
        if(data != null) {
            try {
                json = mapper.readValue(data, Map.class);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return json;
    }

    public void close() {

    }
}
