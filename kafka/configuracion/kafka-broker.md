# Configuración Kafka Broker

En esta lección vamos a hablar de los distintos parámetros de configuración que se pueden aplicar al Broker de Kafka. El fichero utilizado para configurar el servicio de Kafka es ```server.properties```

```properties
################################ Server Basics ################################
broker.id=0

################################# Log Basics ##################################
log.dirs=/tmp/kafka-logs
num.partitions=1

############################# Log Retention Policy ############################
log.retention.hours=168
log.retention.bytes=1073741824
log.segment.bytes=536870912
log.retention.check.interval.ms=300000

################################## Zookeeper ##################################
zookeeper.connect=localhost:2181
```

Este es un ejemplo de fichero de configuración básico.

* **broker.id** : Identificador único por cada Broker. También existe la posibilidad de generarlo de manera automática para ello debemos: eliminar esta propiedad y configurar las siguientes propiedades. Con esto conseguimos que el Broker obtenga un identificador único utilizando Zookeeper.

```properties
broker.id.generation.enable=true
reserved.broker.max.id=1000
```

* **log.dirs** : En esta propiedad indicamos el directorio donde queremos que se almacenen los distintos ficheros de log de Kafka, es decir, los datos que son almacenados en las distintas particiones de los topics.

* **num.partitions** : Número de particiones por defecto al crear un topic de manera automática.

* **log.retention.hours** : Número de horas que se mantienen los datos de un log, antes de eliminarlos. Existen otras dos propiedades que pueden suplantar esta propiedad.

```properties
log.retention.minutes
log.retention.ms
```

* **log.retention.bytes** : Número total de bytes que Kafka almacena por cada log, antes de eliminarlos.

**Nota:**  *log.retention.hours* y *log.retention.bytes* son complementarias una con otra. Los datos se borran cuando se cumpla la primera de ellas.

* **log.segment.bytes** : Tamaño máximo que puede ocupar los ficheros por lo que están compuestos el log.

* **log.retention.check.interval.ms** : Define el intervalo de tiempo que configura cada cuanto tiempo se verifica si existen logs para ser borrados.

* **zookeeper.connect** : La dirección y puerto donde se encuentra el o los nodos de ZooKeeper. Si existiera un chroot se podría indicar detrás de cada host. Ejemplo: ```zookeeper1:2181/kafka-test,zookeeper2:2181/kafka-test,zookeeper3:2181/kafka-test```

La lista completa de configuraciones del Broker se pueden encontrar en el siguiente enlace:
[Configuración Broker Kafka](http://kafka.apache.org/documentation.html#brokerconfigs).
