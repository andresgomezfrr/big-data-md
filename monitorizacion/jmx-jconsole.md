# JMX & JConsole

Si queremos activar el puerto de JMX remoto en una aplicación JAVA tenemos que utilizar los distintos flags:

```
 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=9595
 ```

**Nota:** JMX tiene la opción de utilizar autenticación pero en nuestro caso no será necesario.

Kafka verifica si existe la variable de entorno JMX_PORT, si esta definida el Broker incorpora los flags mencionados automáticamente en su ejecución. Para definir la variable de entorno podemos ejecutar lo siguiente:

```
export JMX_PORT=9595
```

Si queremos que el Broker exponga el puerto en una dirección IP concreta debemos añadir el flag:

```
-Djava.rmi.server.hostname=192.168.1.40
```

Una vez tenemos el Broker configurado con las opciones de JMX, podemos utilizar la herramienta **jconsole** para conectarnos al proceso y monitorizarlo.

```
jconsole
```

**Nota:** La herramienta jconsole viene en la distribución de Java.
