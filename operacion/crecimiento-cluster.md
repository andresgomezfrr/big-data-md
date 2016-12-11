# Crecimiento del Cluster

En esta sección vamos a tratar cómo expandir un cluster de Kafka ya formado para distribuir la carga y de esta forma conseguir un mayor rendimiento.

Para hacer crecer un cluster solamente tenemos que añadir un nuevo broker con un nuevo identificador único. Este nuevo broker se unirá al cluster utilizando ZooKeeper, pero no empezara a recibir datos hasta que le asignemos particiones. Utilizaremos la herramienta ```kafka-reassign-partitions.sh```para esta operación.

Mediante esta herramienta podemos incrementar el número de réplicas que queremos para una partición y la distribución de en qué broker queremos asignar cada réplica. Esta operación vimos cómo funcionaba anteriormente en la sección de *Configuración* en la sesión de *Topics*, y mediante un fichero en formato JSON podíamos indicar donde asignabamos cada replica.

```json
{"version":1, "partitions":[{"topic":"my-topic", "partition":0, "replicas":[1,2,3]}]}
```
La herramienta *kafka-reassign-partitions.sh* nos permite generar este fichero de manera automática:

  1. Indicamos en un fichero JSON los topics que deseamos mover, creando un fichero *topics-move.json* :
  ```json
  {"topics":[{"topic":"topic1"},{"topic":"topic2"}], "version":1}
  ```
  2. Una vez tenemos el fichero, utilizamos el comando indicando el identificador de los brokers donde queremos mover los topics.
  ```
  bin/kafka-reassign-partitions.sh --zookeeper localhost:2181 --topics-to-move-json-file topics-move.json --broker-list "5,6" --generate
  ```
  Este comando genera una salida como la que utilizamos en la sesión anteriormente, copiamos la salida y creamos un fichero *mover-replicas.json*.
  ```json
  {"version":1, "partitions":[
        {"topic":"topic1", "partition":0, "replicas":[5,6]},
        {"topic":"topic1", "partition":1, "replicas":[5,6]},
        {"topic":"topic2", "partition":0, "replicas":[5,6]},
        {"topic":"topic2", "partition":1, "replicas":[5,6]}
  ]}
  ```
  3. Finalmente, tenemos que ejecutar el comando que ya conocemos para aplicar la nueva configuración.
  ```
  bin/kafka-reassign-partitions.sh --zookeeper localhost:2181   --reassignment-json-file mover-replicas.json --execute
  ```

**Nota:** En ocasiones al mover replicas con datos este proceso puede tardar un poco mientras se migran los logs de un broker al otro. Es importante asegurarse cuando el proceso a finalizado para realizar acciones como eliminar un broker, para ello el comando proporciona la opción **--verify**
