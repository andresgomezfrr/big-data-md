# Kafka -> HDFS Connector

En primer lugar debemos instalar o tener un cluster HDFS disponible, para su instalación podemos seguir la siguiente guía. [HDFS Instalación](https://github.com/andresgomezfrr/big-data-md/blob/master/hadoop/instalacion-cluster.md#hadoop-hdfs-cluster)

Una vez configurado HDFS, ya podemos descargar y descomprimir el binario de Flume.

```
wget http://apache.rediris.es/flume/1.8.0/apache-flume-1.8.0-bin.tar.gz; tar -xvf apache-flume-1.8.0-bin.tar.gz
```

Una vez descargados podemos crear un fichero de configuración con nuestro conector:

```
a1.sources = s1
a1.channels = c1
a1.sinks = k1

a1.sources.s1.type = org.apache.flume.source.kafka.KafkaSource
a1.sources.s1.channels = c1
a1.sources.s1.batchSize = 100
a1.sources.s1.batchDurationMillis = 1000
a1.sources.s1.kafka.bootstrap.servers = ${KAFKA_BROKER_IP}:9092
a1.sources.s1.kafka.topics = test
a1.sources.s1.kafka.consumer.group.id = flume-id

a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 1000

a1.sinks.k1.type = hdfs
a1.sinks.k1.channel = c1
a1.sinks.k1.hdfs.path = hdfs://${HADOOP_NAMENODE_IP}:5050/flume/events/%y/%m/%d/%H/%M/%S
a1.sinks.k1.hdfs.filePrefix = events-
a1.sinks.k1.hdfs.round = true
a1.sinks.k1.hdfs.roundValue = 10
a1.sinks.k1.hdfs.roundUnit = minute
```

Este fichero de configuración almacenera los mensajes del topic `test` en HDFS, separando los mensajes en base al tiempo. La comunicación entre Kafka y HDFS se realiza mediante un canal en memoria.

Una vez ya tenemos creado nuestro fichero de configuración por último tenemos que ejecutar el agente de Flume.

```
java -Xmx300m -Dflume.root.logger=INFO,console -cp '${FLUME_HOME}/conf:${FLUME_HOME}lib/*:${HADOOP_HOME}/conf:${HADOOP_HOME}/share/hadoop/common/lib/*:${HADOOP_HOME}/share/hadoop/common/*:${HADOOP_HOME}/share/hadoop/hdfs:${HADOOP_HOME}/share/hadoop/hdfs/lib/*:${HADOOP_HOME}/share/hadoop/hdfs/*:${HADOOP_HOME}/share/hadoop/yarn/lib/*:${HADOOP_HOME}/share/hadoop/yarn/*:${HADOOP_HOME}/share/hadoop/mapreduce/lib/*:${HADOOP_HOME}/share/hadoop/mapreduce/*:/contrib/capacity-scheduler/*.jar' -Djava.library.path= org.apache.flume.node.Application --conf-file conf/flume-conf.properties.template --name a1
```

Tenemos que tener en cuenta de configurar las variables de entorno: `FLUME_HOME` y `HADOOP_HOME`
