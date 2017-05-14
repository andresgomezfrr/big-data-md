# Kafka Docker

Para trabajar con Kafka en docker vamos a utilizar la imagen de *wurstmeister*.

[Kafka Docker Hub](https://hub.docker.com/r/wurstmeister/kafka/)

```
docker pull wurstmeister/kafka:0.10.2.0
```

El parametro que hay que tener en cuenta para configurar un broker de dentro de un docker, es el *listeners* que anunciamos cuando levantamos el docker. Este docker esta preparado para poder configurar las opciones de configuración de Kafka utilizando las variables de entorno añadiendole el sufijo **KAFKA_**.

KAFKA_ADVERTISED_LISTENERS

Lo importante que tenemos que tener en cuenta es que el listener que debemos anunciar es el de la maquina fisica donde se ejecuta el docker, es decir la interfaz principal de nuestro ordenador. Otro parametro que debemos configurar es la dirección de nuestro servidor ZooKeeper.

KAFKA_ZOOKEEPER_CONNECT

```
docker run -it -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://${MAIN_IP_ADDRESS}:9092 -e KAFKA_ZOOKEEPER_CONNECT=${ZOOKEEPER_SERVER} -p 9092:9092 wurstmeister/kafka:0.10.2.0
```
