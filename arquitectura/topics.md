# Topics
Ahora vamos a crear un nuevo topic con 1 partición y factor de replicación igual a 1. En primer lugar tendremos que estar en la carpeta donde se encuentra la distribución de Kafka que nos descargamos anteriormente, y ejecutar lo siguiente:
```
bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic nuevo-topic --partitions 1 --replication-factor 1
```

Una ves hemos creado el topic podemos verificar su creación utilizando:
```
bin/kafka-topics.sh --zookeeper localhost:2181 --list
```

También podemos ver una descripción más detallada utilizando:
```
bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic nuevo-topic
```
Obtendremos una salida similar a la siguiente:
```
Topic:nuevo-topic	PartitionCount:1	ReplicationFactor:1	Configs:
	Topic: nuevo-topic	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
```
Analizando la salida podemos ver que nos dice que el topic *nuevo-topic* tiene una partición que su líder se encuentra en el broker con id 0 y que tiene una replica en el broker 0 y se encuentra en sincronía con la partición líder. Esto último es obvio ya que solamente tenemos una partición.
