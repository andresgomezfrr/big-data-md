# Hadoop YARN Cluster

En la instalación vamos a usar una ubuntu-server 16.04. Antes de nada vamos a actualizar nuestra distribución e instalar java.

```
apt-get -y update; apt-get install -y openjdk-8-jre
```

```
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/jre/
```

En primer lugar nos descargamos la última versión estable de Apache Hadoop 2.8.0

```
wget http://apache.rediris.es/hadoop/common/hadoop-2.8.0/hadoop-2.8.0.tar.gz
```

Una vez tengamos la descarga descomprimimos el tar.gz y accedemos a la carpeta de la distribución:

```
tar -xvf hadoop-2.8.0.tar.gz; cd hadoop-2.8.0
```

## ResourceManager

En primer lugar vamos a configurar los ficheros pertenecientes al nodo que actuara de ResourceManager. Para ello vamos a editar el fichero que se enceuntra en `etc/hadoop/yarn-site.xml` y escribimos el siguiente contenido que es la configuración minima necesaria para levantar el ResourceManager con la configuración por defecto.

```xml
<configuration>
<!-- Site specific YARN configuration properties -->
  <property>
    <name>yarn.resourcemanager.hostname</name>
    <value>172.31.51.168</value>
  </property>
</configuration>
```

Estando dentro de la carpeta de la distribución de hadoop ejecutamos los siguientes comandos en todos los nodos que formen parte del YARN.

```
export HADOOP_YARN_HOME=$(pwd)
mkdir con
export HADOOP_CONF_DIR=$HADOOP_YARN_HOME/conf
cp ./etc/hadoop/yarn-site.xml conf
```

Una vez tenemos el fichero de configuración preparado podemos arrancar el resourcemanager:

```
sbin/yarn-daemon.sh --config $HADOOP_CONF_DIR start resourcemanager
```

Tambien podemos decidir levantar un nodemanager en el mismo nodo que el resourcemanager, para ello podemos ejecutar:

```
sbin/yarn-daemon.sh --config $HADOOP_CONF_DIR start nodemanager
```

Si tenemos otro nodo que queramos que funcione como nodemanager tenemos que llevarnos la misma configuración que hemos aplicado en el resourcemanager al nuevo nodo que actuara de nodemanager, dejando la dirección IP del resourcemanager en el fichero de configuración.

Podemos verificar si el cluster esta funcionando correctamente, para ello accedemos a la dirección IP del resource manager en el puerto 8088 por defecto.

`http://${RESOURCE_MANAGER_ADDRESS}:8088/cluster`
