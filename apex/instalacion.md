#Apache Apex Instalación

En primer lugar vamos a instalar las herramientas que nos haran falta para compilar la CLI y un proyecto base.

```
apt-get -y update; apt-get -y install openjdk-8-jdk maven
```

Definimos la variable de entorno de JAVA_HOME

```
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/
```

Ahora vamos a descargar el código fuente de Apex para construir la ApexCLI:

```
wget http://apache.rediris.es/apex/apache-apex-core-3.6.0/apache-apex-core-3.6.0-source-release.tar.gz; tar -xvf apache-apex-core-3.6.0-source-release.tar.gz; cd apache-apex-core-3.6.0
```

Ejecutamos `mvn install -DskipTests` para crear el binario `apex`. Una vez haya finalizado encontramos el binario en `./engine/src/main/scripts/apex`.

Una vez ejecutemos el binario `apex` intentara localizar hadoop por lo que vamos a instalarlo en primer lugar:

```
root@ip-172-31-2-55:~/apache-apex-core-3.6.0# ./engine/src/main/scripts/apex
Warning: hadoop executable not found.  Running standalone with java.
Apex CLI 3.6.0 Unknown Unknown
apex>
```

La instalación de hadoop podemos realizarla siguiente el tutorial [Instalación Hadoop](https://github.com/andresgomezfrr/big-data-md/tree/master/hadoop). Una vez instalado debemos añadir el binario de hadoop al PATH:

```
export PATH=$PATH:/root/hadoop-2.8.0/bin/
```

Si ahora volvemos a ejecutar la ApexCLI veremos que ya no nos muestra el warning:

```
root@ip-172-31-2-55:~/apache-apex-core-3.6.0# ./engine/src/main/scripts/apex
Apex CLI 3.6.0 Unknown Unknown
apex>
```



