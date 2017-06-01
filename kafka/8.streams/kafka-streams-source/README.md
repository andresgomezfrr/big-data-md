# Kafka Streams

En este código hay un total de 4 ejemplos:

* WordCount usando la API de Processor
* WordCount usando la API DSL
* Enricher usando la API de Processor
* Enricher usando la API DSL

Para compilar el proyecto vamos a usar `mvn clean package` al ejecutarlo hace falta tener docker instalado, ya que genera una imagen docker para poder ejecutar los ejemplos con más facilidad.

El docker proporciona 3 variables de entornos para su configuración:

* **MAIN_CLASS:** Clase principal que deseamos ejecutar.
* **APP_ID:** El indentificador de la aplicación, similar al `group.id` del consumer de Kafka.
* **KAFKA_BOOTSTRAPER:** Subconjunto de los brokers de Kafka.

Un ejemplo de ejecucción podría ser el siguiente:

```
docker run -it -e MAIN_CLASS=com.kafka.streams.api.dsl.enricher.Application -e APP_ID=enrich -e KAFKA_BOOTSTRAPER=192.168.1.103:9092 kafka-streams:latest
```
