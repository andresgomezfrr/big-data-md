# Configuración Productor
Las principales propiedades para el productor son las siguientes:

| Propiedad     | Descripción     |
| :------------- | :------------- |
| bootstrap.servers  | Lista de brokers de Kafka separados por coma e indicando el puerto: ```server01:9092,server02:9092,server03:9092```|
| key.serializer      | Clase utilizada para serializar las keys.     |
| value.serializer       | Clase utilizada para serializar los mensajes.  |

Existen varios serializadores ya implementados:

| Deserializador     | Clase    |
| :------------- | :------------- |
| String       | org.apache.kafka.common.serialization.StringSerializer |
| Long       | org.apache.kafka.common.serialization.LongSerializer |
| Integer       | org.apache.kafka.common.serialization.IntegerSerializer |
| Double       | org.apache.kafka.common.serialization.DoubleSerializer |
| Bytes       | org.apache.kafka.common.serialization.BytesSerializer |
| ByteArray       | org.apache.kafka.common.serialization.ByteArraySerializer |
| ByteBuffer      | org.apache.kafka.common.serialization.ByteBufferSerializer |

Si se desea implementar uno propio se debe utilizar la interfaz:[org.apache.kafka.common.serialization.Serializer](https://github.com/apache/kafka/blob/trunk/clients/src/main/java/org/apache/kafka/common/serialization/Serializer.java)

Hay varias propiedades que resultan de interés en el productor y son las siguientes:

| Propiedad     | Descripción     |
| :------------- | :------------- |
| batch.size      |  Número de mensajes que el productor almacenara antes de enviarlos en lotes al broker.|
| linger.ms      |   Número máximo de espera a que se acumulen los mensajes especificados en **batch.size**, antes de enviar los mensajes.|
| acks      | Nivel de asentimiento definido por el productor.|

Lista completa de las configuraciones del productor: [Configuración Productor](http://kafka.apache.org/documentation.html#producerconfigs).

# Configuración Consumidor
Las principales propiedades para el consumidor son las siguientes:

| Propiedad     | Descripción     |
| :------------- | :------------- |
| bootstrap.servers  | Lista de brokers de Kafka separados por coma e indicando el puerto: ```server01:9092,server02:9092,server03:9092```|
| key.deserializer      | Clase utilizada para deserializar las keys.     |
| value.deserializer       | Clase utilizada para deserializar los mensajes.  |
| group.id | String que identifica al consumidor dentro de un grupo de consumidores.|


Existen varios deserializadores ya implementados:

| Deserializador     | Clase    |
| :------------- | :------------- |
| String       | org.apache.kafka.common.serialization.StringDeserializer |
| Long       | org.apache.kafka.common.serialization.LongDeserializer |
| Integer       | org.apache.kafka.common.serialization.IntegerDeserializer |
| Double       | org.apache.kafka.common.serialization.DoubleDeserializer |
| Bytes       | org.apache.kafka.common.serialization.BytesDeserializer |
| ByteArray       | org.apache.kafka.common.serialization.ByteArrayDeserializer |
| BytesBuffer      | org.apache.kafka.common.serialization.ByteBufferDeserializer |

Si se desea implementar uno propio se debe utilizar la interfaz: [org.apache.kafka.common.serialization.Deserializer](https://github.com/apache/kafka/blob/trunk/clients/src/main/java/org/apache/kafka/common/serialization/Deserializer.java)

Lista completa de las configuraciones del consumidor: [Configuración Consumidor](http://kafka.apache.org/documentation.html#newconsumerconfigs).
