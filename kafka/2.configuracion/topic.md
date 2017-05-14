# Configuración de Topic

## Configuración particiones y réplicas
A la hora de crear un topic nuevo podemos indicar el número de particiones y el factor de replicación.

```
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic my-topic --partitions 1 --replication-factor 1
```
 * En la creación Kafka asigna la replicas automáticamente a los brokers.

Una vez ya tenemos creado el topic es facil incrementar el número de particiones:
```
bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic my-topic --partitions 10
```

Aunque no es tan sencillo incrementar el factor de replicación, para hacerlo debemos hacer lo siguiente:

1. En primer lugar debemos definir un fichero, que vamos a nombrar *incremento-replicas.json*, en formato JSON donde indicamos en que broker queremos asignar las distintas particiones (incluidas sus replicas):
```json
{"version":1, "partitions":[{"topic":"my-topic", "partition":0, "replicas":[1,2,3]}]}
```

Mediante este fichero indicamos que para el topic *my-topic* queremos que su partición 0 se encuentre en los brokers con ID: 1,2,3; es decir conseguiríamos un factor de replicación de 3. Kafka se encarga automáticamente de decidir que partición es la líder.

2. Ejecutar el siguiente comando para aplicar la configuración deseada:
```
bin/kafka-reassign-partitions.sh --zookeeper localhost:2181 --reassignment-json-file incremento-replicas.json --execute
```

## Configuración a nivel de topic

Como hemos visto en la lección también se pueden aplicar configuraciones especificas por topic. Si esta configuración no es aplicada se utilizan los valores utilizados en el fichero del broker, o los por defecto si no están especificados.

La configuración por topic se puede aplicar a la hora de creación del topic:

```bash
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic my-topic --partitions 1 --replication-factor 1 --config max.message.bytes=64000 --config flush.messages=1
```

* Mediante este comando crearemos un topic denominado **my-topic** que tendrá 1 partición y factor de replicación de 1, el tamaño máximo de los mensajes de este topic sera 64000 bytes y se realizara un flush al log que se encuentra en el disco duro por cada mensaje.

Si quisiéramos cambiar la configuración especifica de un topic utilizaríamos un comando como el siguiente:

```bash
bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic my-topic --config max.message.bytes=128000
```

* Este comando cambia el valor para la configuración **max.message.bytes** ahora el topic soporta mensajes con un tamaño máximo de 128000 bytes en lugar de 64000 bytes.

Finalmente, también existe la posibilidad de borrar una configuración aplicada a un topic, en ese caso tendremos que usar el comando con la opción **--delete-config**:

```bash
bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic my-topic --delete-config max.message.bytes
```

* Una vez ejecutado el valor el topic utilizará el valor definido en el fichero de configuración del broker ```server.properties``` o de no estar definida utilizará el valor por defecto.

Algunas configuraciones más interesantes que se pueden aplicar por topic son las siguientes:

| Propiedad     | Descripción     |
| :------------- | :------------- |
| cleanup.policy      | Política de borrado aplicada al topic: **delete** o **compact**.  |
| max.message.bytes| Tamaño máximo de los mensajes almacenados en este topic.|
|retention.bytes| Tamaño máximo que un log puede crecer antes de que se aplique una política de borrado.|
|min.insync.replicas| Número de replicas que deben asentir antes de devolver el ACK al productor.|

La lista completa de configuración por topic se pueden encontrar en el siguiente enlace:
[Configuración a nivel de topic](http://kafka.apache.org/documentation.html#topic-config).

cleanup.policy=compact
