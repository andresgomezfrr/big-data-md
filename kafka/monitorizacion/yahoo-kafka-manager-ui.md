# Yahoo Kafka Manager UI

En primer lugar vamos a descargar la última versión del software desde el enlace que se encuentra en el repositorio oficial [Kafka Manager Github](https://github.com/yahoo/kafka-manager).

```
wget https://github.com/yahoo/kafka-manager/archive/1.3.1.8.tar.gz
```

Una vez descargado tenemos que descomprimir el fichero y nos creada una carpeta con el siguiente nombre *kafka-manager-1.3.1.8*

```
tar -xvf 1.3.1.8.tar.gz
```

Cuando tengamos la carpeta únicamente tenemos que construir la distribución utilizando el siguiente comando:

```
./sbt clean dist
```

**Nota:** Si no se suele usar sbt puede que la construcción dure unos minutos.

Al terminar el comando nos habra construido un fichero ZIP, que vamos a descomprimir, y entrar en la carpeta que se genera.

```
unzip target/universal/kafka-manager-1.3.1.8.zip
```
```
cd kafka-manager-1.3.1.8/
```

Tenemos que editar el fichero de configuración en la ruta *conf/application.conf* y editar la linea donde se indica el servidor de ZooKeeper.

```
kafka-manager.zkhosts="kafka-manager-zookeeper:2181"
```

Una vez ya modificado el fichero de configuración podemos ejecutar el servicio.

```
bin/kafka-manager
```

Para acceder a la interfaz web debemos usar la URL: http://localhost:9000
