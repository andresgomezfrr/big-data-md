# ZooKeeper y Kafka

Una vez vista la arquitectura básica de ZooKeeper y Kafka, vamos a iniciar ambos servicios y ver como Kafka se registra en ZooKeeper.

1. En primer lugar vamos a descargar la versión estable de Apache ZooKeeper y a descomprimirla.
```
wget http://apache.rediris.es/zookeeper/stable/zookeeper-3.4.8.tar.gz
```
```
tar -xvf zookeeper-3.4.8.tar.gz ; cd zookeeper-3.4.8
```
2. Una vez tenemos la distribución vamos a renombrar el fichero de configuración que viene por defecto.
```
cp conf/zoo_sample.cfg conf/zoo.cfg
```

3. Cuando ya tenemos el fichero de configuración preparado podemos ejecutar el servidor.
```
 bin/zkServer.sh start
```

4. Finalmente podemos comprobar su correcto funcionamiento conectándonos usando la herramienta que viene con la distribución.
```
bin/zkCli.sh -server localhost:2181
Connecting to localhost:2181
[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper]
```

Ya tenemos un servidor de ZooKeeper disponible y podemos empezar a descargar y configurar Kafka.

1. Ahora vamos a descargar la distribución de Kafka y la descomprimimos.
```
wget http://apache.rediris.es/kafka/0.10.0.1/kafka_2.11-0.10.0.1.tgz
```
```
tar -xvf kafka_2.11-0.10.0.1.tgz ; cd kafka_2.11-0.10.0.1
```

2. Para ejecutar Kafka utilizaremos uno de los scripts que proporciona:
```
bin/kafka-server-start.sh config/server.properties
```
Si se desea ejecutar en segundo plano se puede añadir la opción -daemon:
```
bin/kafka-server-start.sh -daemon config/server.properties
```
  * **Nota:** Por defecto la configuración de Kafka busca el servidor de ZooKeper en localhost:2181, si hemos levantado el servicio de Kafka y ZooKeeper en máquinas separadas deberemos editar el fichero ```config/server.properties```

3. Para verificar que el servidor esta corriendo y se ha registrado correctamente en ZooKeeper podemos utilizar la utilidad **zkCli.sh**. Veremos que efectivamente existe un Broker con identificador 0, es el valor que viene en el fichero por defecto.
```
[zk: localhost:2181(CONNECTED) 5] ls /brokers/ids
[0]
```
Si hacemos un **get** del zNode podemos ver el contenido que el broker ha almacenado dentro.
```
[zk: localhost:2181(CONNECTED) 7] get /brokers/ids/0
{"jmx_port":-1,"timestamp":"1472318261728","endpoints":["PLAINTEXT://192.168.99.1:9092"],"host":"192.168.99.1","version":3,"port":9092}
```

* **Nota:** Si quisiéramos levantar varios Brokers en la misma maquina para realizar pruebas deberíamos de cambiar el identificador del fichero **broker.id**, el directorio donde se almacena la información de Kafka **log.dirs** y el puerto donde escucha el servicio **listeners**.
