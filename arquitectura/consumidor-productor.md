# Productor y Consumidor

Una vez ya tenemos nuestros topics creados, es hora de producir algún mensaje en él y comprobar que el sistema funciona completamente. Para enviar mensajes vamos a utilizar las herrameientas que vienen con el paquete de Kafka que porporciona un consumidor y producor de consola.

## Productor
El productor de consola deja abierta una shell, donde podremos escribir mensajes para que los envie a Kafka.
```
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic nuevo-topic  --property parse.key=true --property key.separator=,
```
Este comando nos permite enviar mensajes con clave a Kafka. Usando el símbolo indicado en **--property key.seperator** se puede separar la clave del mensaje. Probemos a enviar varios mensajes:
```
1,hola
2,mundo
clave,mensaje
```
Los mensajes se dan por terminados y se envían cuando se pulsa enter.

* **Nota:** Si intentamos enviar un mensaje sin la ',' el productor fallará al no poder distinguir la clave del valor.

## Consumidor
Igual que el productor usaremos un script que viene proporcionado con Kafka:
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --property print.key=true --topic nuevo-topic  --new-consumer --consumer.config config/consumer.properties
```

Una vez ejecutado, la shell se quedará abierta y empezaremos a ver los mensajes que enviamos por el productor. Hemos utilizado la opción **--consumer.config config/consumer.properties** para poder indicar un grupo de consumidor. Si mostramos el contenido del fichero:

```
cat config/consumer.properties
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# see kafka.consumer.ConsumerConfig for more details

# Zookeeper connection string
# comma separated host:port pairs, each corresponding to a zk
# server. e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002"
zookeeper.connect=127.0.0.1:2181

# timeout in ms for connecting to zookeeper
zookeeper.connection.timeout.ms=6000

#consumer group id
group.id=test-consumer-group

#consumer timeout
#consumer.timeout.ms=5000
```

Podemos ver que se indica un group.id con valor de **test-consumer-group**.

* **Nota:** Hemos utilizado la opción **--new-consumer** ya que hemos utilizado la nueva API de Kafka introducida en la versión 0.9.0. El consumidor aun permite utilizar la API antigua.
