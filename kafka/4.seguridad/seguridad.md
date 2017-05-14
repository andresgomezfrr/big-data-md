# Seguridad

## Encriptación SSL

##### 1. Generación de una clave SSL y certificado por cada Kafka Broker

El primer paso para desplegar HTTPS es generar una clave y certificado por cada máquina donde tengamos instalado un broker. Para realizar esta tarea podemos usar la herramienta de java *keytool*, generaremos la clave en un keystore temporal del cual podemos exportar y firmar posteriormente.


```
 keytool -keystore server.keystore.jks -alias localhost -validity 360 -genkey
```

 * keystore: El keystore donde se almacenará la clave privada del certificado.
 * validity: La validez del certificado en días.

La herramienta nos solicitará la siguiente información:

```
Introduzca la contraseña del almacén de claves:
Volver a escribir la contraseña nueva:
¿Cuáles son su nombre y su apellido?
  [Unknown]:  Andres
¿Cuál es el nombre de su unidad de organización?
  [Unknown]:  openwebinars
¿Cuál es el nombre de su organización?
  [Unknown]:  openwebinars
¿Cuál es el nombre de su ciudad o localidad?
  [Unknown]:  Sevilla
¿Cuál es el nombre de su estado o provincia?
  [Unknown]:  Andalucia
¿Cuál es el código de país de dos letras de la unidad?
  [Unknown]:  ES
¿Es correcto CN=Andres, OU=openwebinars, O=openwebinars, L=Sevilla, ST=Andalucia, C=ES?
  [no]:  si

Introduzca la contraseña de clave para <localhost>
	(INTRO si es la misma contraseña que la del almacén de claves):
```

**Nota:** Como contraseña se ha usado *openwebinars*.

Si queremos verificar el certificado podemos usar:

```
keytool -list -v -keystore server.keystore.jks
```

##### 2. Creación de nuestra propia autoridad certificadora (CA)

La CA es la responsable de firmar los certificados, la firma del certificado debe realizarse por cada maquina del cluster.

Utilizando el siguiente comando generamos un CA que simplemente es una par de clave publica/privada y un certificado que nos permitirán firmar otros certificados.

```
openssl req -new -x509 -keyout ca-key -out ca-cert -days 365
```

La herramienta nos solicitará la siguiente información:

```
Generating a 1024 bit RSA private key
..........++++++
.........................................++++++
writing new private key to 'ca-key'
Enter PEM pass phrase:
Verifying - Enter PEM pass phrase:
-----
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:ES
State or Province Name (full name) [Some-State]:Sevilla
Locality Name (eg, city) []:Sevilla
Organization Name (eg, company) [Internet Widgits Pty Ltd]:openwebinars
Organizational Unit Name (eg, section) []:openwebinars
Common Name (e.g. server FQDN or YOUR name) []:openwebinars
Email Address []:openwebinars@openwebinars.org
```

**Nota:** La contraseña usada para la contraseña de paso es *openwebinars*.

Este comando nos generara dos ficheros *ca-cert* y *ca-key* que utilizaremos seguidamente.

El siguiente paso es añadir el certificar al almacén de confianza de los clientes para que estos confíen en ese certificado.

```
keytool -keystore server.truststore.jks -alias CARoot -import -file ca-cert
```

**Nota:** En el comando usamos de contraseña nuevamente *openwebinars* y contestamos que confiamos en el certificado.

##### 3. Firmado del certificado
En este punto vamos a firmar todos los certificaos generados en el primer punto, con la CA generada en el punto 2.

En primer lugar debemos exportar el certificado que tenemos almacenado en el keystore.

```
keytool -keystore server.keystore.jks -alias localhost -certreq -file cert-file
```

Después, firmamos con la CA:
```
openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file -out cert-signed -days 360 -CAcreateserial -passin pass:openwebinars
```

Finalmente, importamos de nuevo el certificado del CA y el certificado firmado dentro del keystore.

```
keytool -keystore server.keystore.jks -alias CARoot -import -file ca-cert
```

```
keytool -keystore server.keystore.jks -alias localhost -import -file cert-signed
```

* keystore: Localización del keystore.
* ca-cert: El certificado del CA.
* ca-key: La clave privada del CA.
* ca-password: Clave de paso del CA (*openwebinars*)
* cert-file: El certificado del servido sin firmar.
* cert-signed: El certificado del servidor firmado.

##### 4. Configuración de los Brokers

Kafka proporciona la posibilidad para crear conexiones con multiples puertos utilizando la propiedad **listeners**.

```
listeners=SSL://:9102
```

**Nota:** Se pueden proporcionar varios listeners de distinto tipo, podemos decir que escuche en SSL en el puerto 9102 y en claro en el puerto 9092:
```
listeners=SSL://:9102,PLAINTEXT://:9092
```

También debemos configurar el fichero de cada broker para que utilice los almacenes de claves, añadiendo las siguientes propiedades en el fichero *server.properties*.

```
ssl.keystore.location=server.keystore.jks
ssl.keystore.password=openwebinars
ssl.key.password=openwebinars

ssl.truststore.location=server.truststore.jks
ssl.truststore.password=openwebinars

security.inter.broker.protocol=SSL
```

Una forma rapida de verificar si funciona correctamente es utilizar el comando:

```
openssl s_client -debug -connect localhost:9102 -tls1
```

Que nos muestrará el certificado del servidor.

##### 5. Configuración clientes de Kafka

Una vez configurado los brokers es hora de configurar los clientes para que cifren la información, para ello debemos crear un truststore para los clientes donde importaremos el certificado de la CA.

```
keytool -keystore client.truststore.jks -alias CARoot -import -file ca-cert
```

También, añadiremos la configuración en los ficheros de configuración del consumidor y del productor.

```
security.protocol=SSL
ssl.truststore.location=client.truststore.jks
ssl.truststore.password=openwebinars
```

## Autenticación usando SSL

Si queremos habilitar la autenticación entre los clientes y los brokers debemos configurar la propiedad **ssl.client.auth=required** y seguir los siguientes pasos:

1. Debemos generar un keystore en el cliente y generar un certificado propio, como hicimos en el primer punto.
```
keytool -keystore client.keystore.jks -alias localhost -validity 360 -genkey
```
2. Posteriormente debemos exportar su certificado y firmarlo con el certificado del CA.
```
keytool -keystore client.keystore.jks  -alias localhost -certreq -file cert-file-client
```
```
openssl x509 -req -CA ca-cert -CAkey ca-key -in cert-file-client  -out cert-signed-client -days 360 -CAcreateserial -passin pass:openwebinars
```
3. Finalmente, debemos importar el certificado del cliente firmado en el truststore de cada broker.
```
keytool -keystore server.truststore.jks  -alias localhost -import -file cert-signed-client
```
