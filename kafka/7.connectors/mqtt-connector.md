# MQTT Kafka Source Connector

En primer lugar vamos a descargar el proyecto del MQTT connector y haremos la build.

```
git clone https://github.com/evokly/kafka-connect-mqtt.git
```

```
cd kafka-connect-mqtt; ./gradlew clean jar copyRuntimeLibs
```

Una vez generado el jar lo copiamos dentro de la carpeta `libs` de la distribución de Kafka.

```
cp build/libs/kafka-connect-mqtt-1.1-SNAPSHOT.jar ~/kafka_2.11-0.10.2.1/libs/
cp build/output/lib/org.eclipse.paho.client.mqttv3-1.0.2.jar ~/kafka_2.11-0.10.2.1/libs/
```

Una vez añadida las dependencias tenemos que [iniciar un servidor ZooKeeper y un broker de Kafka](https://github.com/andresgomezfrr/big-data-md/blob/master/kafka/1.arquitectura/zookeeper-kafka.md).

Creamos el fichero que usaremos para configurar nuestro conector mqtt:
**mqtt.properties**

```properties
name=mqtt
connector.class=com.evokly.kafka.connect.mqtt.MqttSourceConnector
tasks.max=1

#Settings
kafka.topic=mqtt
mqtt.client_id=my-id
mqtt.clean_session=true
mqtt.connection_timeout=30
mqtt.keep_alive_interval=60
mqtt.server_uris=tcp://broker.hivemq.com:1883
mqtt.topic=testtopic/1
mqtt.qos=1
message_processor_class=com.evokly.kafka.connect.mqtt.sample.DumbProcessor
```

Vamos a utilizar un broker mqtt público que podemos encontar en el siguiente enlace:

[Broker MQTT](http://www.hivemq.com/try-out/)

Ejecutamos el conector en este caso vamos a ejecutar el modo standalone para testing:

```
kafka_2.11-0.10.2.1/bin/connect-standalone.sh kafka_2.11-0.10.2.1/config/connect-standalone.properties mqtt.properties
```

Para enviar mensajes podemos usar la siguiente web, debemos enviar los mensajes al mqtt topic `testtopic/1` .

[Producor online MQTT](http://www.hivemq.com/demos/websocket-client/)

Podemos veriricar su funcionamiento consumiendo del kafka topic `mqtt` utilizando el consumidor de consola.
