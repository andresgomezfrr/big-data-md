# Creación de un Proyecto Apex

Para crear un esqueleto base que podemos usar para desarrollar nuestra aplicación, vamos a utilizar maven.

```
mvn archetype:generate \
                -DarchetypeGroupId=org.apache.apex \
                -DarchetypeArtifactId=apex-app-archetype -DarchetypeVersion=3.6.0 \
                -DgroupId=com.example -Dpackage=com.example.myapexapp -DartifactId=myapexapp \
                -Dversion=1.0-SNAPSHOT
```

Una vez termine la ejecucción veremos una carpeta llamada `myapexapp` donde encontraremos un esqueleto base. Este ejemplo base es un DAG que genera numero aleatorio que se imprimen en la consola. Para compilar el proyecto utilizaremos maven `mvn clean package`. Al compilarlo gracias al assembly que viene en el esqueleto nos genera un fichero `.apa` dentro de la carpeta `target` este fichero es nuestra aplicación Apex que subiremos al cluster utilizando la ApexCLI.

Para lanzar la aplicación con la ApexCLI, en primer lugar arrancamos el ApexCLI:

```
apex> launch ~/myapexapp/target/myapexapp-1.0-SNAPSHOT.apa
```

Una vez ejecutado deberiamos de ver su ejecucción en el resourcemanager `http://${resource.manager.address}:8088`. 
