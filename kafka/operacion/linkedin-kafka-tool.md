# LinkedIn Kafka Tool

En primer lugar, vamos a descargarnos el gestor de paquetes de python.

```
yum install epel-release
```
```
yum install -y python-pip
```

Una vez hemos instalado el gestor ya podemos instalar el paquete de utilidades de LinkedIn:

```
pip install kafka-tools
```

Una vez instalado ya podríamos utilizar la herramienta **kafka-assigner**, aunque puede ser necesario tener que exportar la variable de entorno JAVA_HOME si no esta exportada.

```
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.102-1.b14.el7_2.x86_64/jre/
```

```
kafka-assigner -h
usage: kafka-assigner [-h] -z ZOOKEEPER [-l] [-g] [-e] [-m MOVES]
                     [--sizer {ssh}] [-s] [-d DATADIR] [--skip-ple]
                     [--ple-size PLE_SIZE] [--ple-wait PLE_WAIT]
                     [--tools-path TOOLS_PATH]

                     {trim,elect,set-replication-factor,clone,remove,balance,reorder}
                     ...

Rejigger Kafka cluster partitions

positional arguments:
 {trim,elect,set-replication-factor,clone,remove,balance,reorder}
                       Select manipulation module to use
   trim                Remove partitions from some brokers (reducing RF)
   elect               Reelect partition leaders using preferred replica
                       election
   set-replication-factor
                       Increase the replication factor of the specified
                       topics
   clone               Copy partitions from some brokers to a new broker
                       (increasing RF)
   remove              Move partitions from one broker to one or more other
                       brokers (maintaining RF)
   balance             Rebalance partitions across the cluster
   reorder             Reelect partition leaders using replica reordering

optional arguments:
 -h, --help            show this help message and exit
 -z ZOOKEEPER, --zookeeper ZOOKEEPER
                       Zookeeper path to the cluster (i.e. zk-
                       eat1-kafka.corp:12913/kafka-data-deployment)
 -l, --leadership      Show cluster leadership balance
 -g, --generate        Generate partition reassignment file
 -e, --execute         Execute partition reassignment
 -m MOVES, --moves MOVES
                       Max number of moves per step
 --sizer {ssh}         Select module to use to get partition sizes
 -s, --size            Show partition sizes
 -d DATADIR, --datadir DATADIR
                       Path to the data directory on the broker
 --skip-ple            Skip preferred replica election after finishing moves
 --ple-size PLE_SIZE   Max number of partitions in a single preferred leader
                       election
 --ple-wait PLE_WAIT   Time in seconds to wait between preferred leader
                       elections
 --tools-path TOOLS_PATH
                       Path to Kafka admin utilities, overriding PATH env var
```

Vamos a crear un topic con 30 particiones:

```
bin/kafka-topics.sh --create --partitions 30 --topic test01 --replication-factor 1 --zookeeper localhost
```

Si ejecutamos un *describe* del topic veremos que las particiones están distribuidas entre el broker0 y el broker1.

```
bin/kafka-topics.sh --describe --topic test01 --zookeeper localhost
Topic:test01	PartitionCount:30	ReplicationFactor:1	Configs:
  Topic: test01	Partition: 0	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 1	Leader: 0	Replicas: 0	Isr: 0
  Topic: test01	Partition: 2	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 3	Leader: 0	Replicas: 0	Isr: 0
  Topic: test01	Partition: 4	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 5	Leader: 0	Replicas: 0	Isr: 0
  Topic: test01	Partition: 6	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 7	Leader: 0	Replicas: 0	Isr: 0
  Topic: test01	Partition: 8	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 9	Leader: 0	Replicas: 0	Isr: 0
  Topic: test01	Partition: 10	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 11	Leader: 0	Replicas: 0	Isr: 0
  Topic: test01	Partition: 12	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 13	Leader: 0	Replicas: 0	Isr: 0
  Topic: test01	Partition: 14	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 15	Leader: 0	Replicas: 0	Isr: 0
  Topic: test01	Partition: 16	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 17	Leader: 0	Replicas: 0	Isr: 0
  Topic: test01	Partition: 18	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 19	Leader: 0	Replicas: 0	Isr: 0
  Topic: test01	Partition: 20	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 21	Leader: 0	Replicas: 0	Isr: 0
  Topic: test01	Partition: 22	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 23	Leader: 0	Replicas: 0	Isr: 0
  Topic: test01	Partition: 24	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 25	Leader: 0	Replicas: 0	Isr: 0
  Topic: test01	Partition: 26	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 27	Leader: 0	Replicas: 0	Isr: 0
  Topic: test01	Partition: 28	Leader: 1	Replicas: 1	Isr: 1
  Topic: test01	Partition: 29	Leader: 0	Replicas: 0	Isr: 0
```

Si ejecutamos el siguiente comando:

```
kafka-assigner -z localhost:2181 --tools-path /root/kafka_2.11-0.10.0.1/bin/ --generate -e balance --types even
```

Nos genera un DRY-RUN de la ejecución que se va a realizar. Si estamos conformes con el nuevo particionado tenemos que cambiar la opción **--generate** por **--execute**, y el comando comenzara a ejecutar el reparticionado poco a poco, para que el sistema no se vea muy afectado.

```
kafka-assigner -z localhost:2181 --tools-path /root/kafka_2.11-0.10.0.1/bin/ --execute -e balance --types even
```

```
bin/kafka-topics.sh --describe --topic test01 --zookeeper localhost
Topic:test01	PartitionCount:30	ReplicationFactor:1	Configs:
	Topic: test01	Partition: 0	Leader: 2	Replicas: 2	Isr: 2
	Topic: test01	Partition: 1	Leader: 2	Replicas: 2	Isr: 2
	Topic: test01	Partition: 2	Leader: 2	Replicas: 2	Isr: 2
	Topic: test01	Partition: 3	Leader: 2	Replicas: 2	Isr: 2
	Topic: test01	Partition: 4	Leader: 2	Replicas: 2	Isr: 2
	Topic: test01	Partition: 5	Leader: 2	Replicas: 2	Isr: 2
	Topic: test01	Partition: 6	Leader: 2	Replicas: 2	Isr: 2
	Topic: test01	Partition: 7	Leader: 2	Replicas: 2	Isr: 2
	Topic: test01	Partition: 8	Leader: 2	Replicas: 2	Isr: 2
	Topic: test01	Partition: 9	Leader: 2	Replicas: 2	Isr: 2
	Topic: test01	Partition: 10	Leader: 1	Replicas: 1	Isr: 1
	Topic: test01	Partition: 11	Leader: 0	Replicas: 0	Isr: 0
	Topic: test01	Partition: 12	Leader: 1	Replicas: 1	Isr: 1
	Topic: test01	Partition: 13	Leader: 0	Replicas: 0	Isr: 0
	Topic: test01	Partition: 14	Leader: 1	Replicas: 1	Isr: 1
	Topic: test01	Partition: 15	Leader: 0	Replicas: 0	Isr: 0
	Topic: test01	Partition: 16	Leader: 1	Replicas: 1	Isr: 1
	Topic: test01	Partition: 17	Leader: 0	Replicas: 0	Isr: 0
	Topic: test01	Partition: 18	Leader: 1	Replicas: 1	Isr: 1
	Topic: test01	Partition: 19	Leader: 0	Replicas: 0	Isr: 0
	Topic: test01	Partition: 20	Leader: 1	Replicas: 1	Isr: 1
	Topic: test01	Partition: 21	Leader: 0	Replicas: 0	Isr: 0
	Topic: test01	Partition: 22	Leader: 1	Replicas: 1	Isr: 1
	Topic: test01	Partition: 23	Leader: 0	Replicas: 0	Isr: 0
	Topic: test01	Partition: 24	Leader: 1	Replicas: 1	Isr: 1
	Topic: test01	Partition: 25	Leader: 0	Replicas: 0	Isr: 0
	Topic: test01	Partition: 26	Leader: 1	Replicas: 1	Isr: 1
	Topic: test01	Partition: 27	Leader: 0	Replicas: 0	Isr: 0
	Topic: test01	Partition: 28	Leader: 1	Replicas: 1	Isr: 1
	Topic: test01	Partition: 29	Leader: 0	Replicas: 0	Isr: 0
```
