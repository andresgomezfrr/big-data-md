En Druid es posible indexar mensajes en batch. Este proceso se realiza mediante una tarea de indexación de tipo `index` o `index_hadoop`.

En este caso vamos a indexar un fichero que tiene un JSON por linea. Este fichero lo tenemos comprimido en formato GZIP y esta almacenado en un bucket de S3.

El fichero de S3 contiene una muestra de datos sobre analitica de red, por lo que hemos definido las siguiente dimensiones y métricas para su indexado.


**Muestra de los datos:**

```json
{"type":"netflowv10","flow_sequence":"14982","src":"169.50.74.122","dst":"10.81.218.209","ip_protocol_version":4,"l4_proto":6,"src_port":8080,"dst_port":42256,"srv_port":8080,"input_vrf":0,"input_snmp":58,"flow_end_reason":"lack of resources","biflow_direction":"reverse initiator","application_id_name":"http-alt","engine_id_name":"IANA-L4","output_snmp":53,"output_vrf":0,"direction":"egress","client_mac":"34:64:87:a1:f6:b9","sensor_ip":"10.128.15.10","sensor_uuid":"-0e2c-42c2-8324-e3528717c0d8","sensor_name":"ISG1","first_switched":1488285163,"timestamp":1488285167,"bytes":3194,"pkts":18}
{"type":"netflowv10","flow_sequence":"14983","src":"52.209.58.155","dst":"10.82.156.135","ip_protocol_version":4,"l4_proto":6,"src_port":443,"dst_port":32795,"srv_port":443,"input_vrf":0,"input_snmp":58,"flow_end_reason":"lack of resources","biflow_direction":"reverse initiator","application_id_name":"ssl","engine_id_name":"PANA-L7","output_snmp":54,"output_vrf":0,"direction":"egress","client_mac":"ac:5f:3e:82:e6:cd","sensor_ip":"10.128.15.10","sensor_uuid":"-0e2c-42c2-8324-e3528717c0d8","sensor_name":"ISG1","first_switched":1488285165,"timestamp":1488285167,"bytes":1603,"pkts":2}
{"type":"netflowv10","flow_sequence":"15000","src":"10.81.215.178","dst":"92.122.148.144","ip_protocol_version":4,"l4_proto":6,"src_port":54757,"dst_port":80,"srv_port":80,"input_vrf":0,"input_snmp":53,"flow_end_reason":"lack of resources","biflow_direction":"initiator","application_id_name":"13:637","engine_id_name":"PANA-L7","output_snmp":58,"output_vrf":0,"direction":"ingress","client_mac":"60:a3:7d:9a:d2:ee","sensor_ip":"10.128.15.10","sensor_uuid":"-0e2c-42c2-8324-e3528717c0d8","sensor_name":"ISG1","first_switched":1488285167,"timestamp":1488285167,"bytes":208,"pkts":4}
{"type":"netflowv10","flow_sequence":"15003","src":"10.82.149.114","dst":"35.186.213.138","ip_protocol_version":4,"l4_proto":6,"src_port":38541,"dst_port":443,"srv_port":443,"input_vrf":0,"input_snmp":54,"flow_end_reason":"lack of resources","biflow_direction":"initiator","application_id_name":"13:687","engine_id_name":"PANA-L7","output_snmp":58,"output_vrf":0,"direction":"ingress","client_mac":"e0:98:61:ee:15:99","sensor_ip":"10.128.15.10","sensor_uuid":"-0e2c-42c2-8324-e3528717c0d8","sensor_name":"ISG1","first_switched":1488285164,"timestamp":1488285167,"bytes":2823,"pkts":27}
{"type":"netflowv10","flow_sequence":"15004","src":"208.67.222.222","dst":"10.81.244.252","ip_protocol_version":4,"l4_proto":17,"src_port":53,"dst_port":6809,"srv_port":53,"input_vrf":0,"input_snmp":58,"flow_end_reason":"lack of resources","biflow_direction":"reverse initiator","application_id_name":"dns","engine_id_name":"IANA-L4","output_snmp":53,"output_vrf":0,"direction":"egress","client_mac":"98:f1:71:11:b7:ff","sensor_ip":"10.128.15.10","sensor_uuid":"-0e2c-42c2-8324-e3528717c0d8","sensor_name":"ISG1","first_switched":1488285167,"timestamp":1488285167,"bytes":146,"pkts":1}
```


**index-s3.json**:

```json
{
  "type" : "index_hadoop",
  "spec" : {
    "dataSchema" : {
      "dataSource" : "network-data",
      "parser" : {
        "type" : "string",
        "parseSpec" : {
          "format" : "json",
          "timestampSpec" : {
            "column" : "timestamp",
            "format" : "ruby"
          },
          "dimensionsSpec" : {
            "dimensions": [
              "src",
              "dst",
              "client_mac",
              "application_id_name",
              "biflow_direction",
              "direction",
              "engine_id_name",
              "http_user_agent_os",
              "http_host",
              "http_social_media",
              "http_social_user",
              "http_referer_l1",
              "l4_proto",
              "ip_protocol_version",
              "sensor_name",
              "sensor_uuid",
              "deployment",
              "deployment_uuid",
              "namespace",
              "namespace_uuid",
              "src_country_code",
              "src_net_name",
              "src_port",
              "src_as_name",
              "client_mac_vendor",
              "dot11_status",
              "src_vlan",
              "src_map",
              "srv_port",
              "dst_country_code",
              "dst_net_name",
              "dst_port",
              "dst_as_name",
              "dst_vlan",
              "dst_map",
              "input_snmp",
              "output_snmp",
              "input_vrf",
              "output_vrf",
              "tos",
              "client_latlong",
              "coordinates_map",
              "campus",
              "campus_uuid",
              "building",
              "building_uuid",
              "floor",
              "floor_uuid",
              "zone",
              "zone_uuid",
              "wireless_id",
              "client_rssi",
              "wireless_station",
              "market",
              "market_uuid",
              "organization",
              "organization_uuid",
              "dot11_protocol",
              "type",
              "target_name",
              "service_provider",
              "service_provider_uuid",
              "proxy_uuid"
            ]
          }
        }
      },
      "metricsSpec" : [
                                    {"type":"count"                                      , "name":"events"}
                                   ,{"type":"longSum"    , "fieldName":"bytes"           , "name":"sum_bytes"}
                                   ,{"type":"longSum"    , "fieldName":"pkts"            , "name":"sum_pkts"}
                                   ,{"type":"hyperUnique", "fieldName":"client_mac"      , "name":"clients"}
                                   ,{"type":"hyperUnique", "fieldName":"wireless_station", "name":"wireless_stations"}
      ],
      "granularitySpec" : {
        "type" : "uniform",
        "segmentGranularity" : "FIFTEEN_MINUTE",
        "queryGranularity" : "MINUTE",
        "intervals" : [ "2017-02-01/2017-03-03" ]
      }
    },
    "ioConfig" : {
      "type" : "hadoop",
      "inputSpec" : {
        "type" : "static",
        "paths" : "s3n://my-druid-deep-storage/raw/1_1_00000000000160928670.gz"
      }
    },
    "tuningConfig" : {
      "type": "hadoop",
      "jobProperties" : {
          "fs.s3.awsAccessKeyId" : "AKIAIFEXIBU2R------",
          "fs.s3.awsSecretAccessKey" : "yhQ7eCOjO15EuIFKnkeXOLyf+1TetHTdw------",
          "fs.s3.impl" : "org.apache.hadoop.fs.s3native.NativeS3FileSystem",
          "fs.s3n.awsAccessKeyId" : "AKIAIFEXIBU2R7------",
          "fs.s3n.awsSecretAccessKey" : "yhQ7eCOjO15EuIFKnkeXOLyf+1TetHTdw------",
          "fs.s3n.impl" : "org.apache.hadoop.fs.s3native.NativeS3FileSystem",
          "io.compression.codecs" : "org.apache.hadoop.io.compress.GzipCodec,org.apache.hadoop.io.compress.DefaultCodec,org.apache.hadoop.io.compress.BZip2Codec,org.apache.hadoop.io.compress.SnappyCodec"
       }
    }
  }
}
```

Una vez configurado nuestras AccessKey y SecretKey para poder comunicarnos correctamente con S3, podemos subir la task al overlord.

```
curl -X POST -H 'Content-Type: application/json' -d @index-s3.json http://${OVERLORD_IP}:8090/druid/indexer/v1/task
```

Una vez el indexado haya terminado podemos consultar los datos utilizando una query:

```
curl -sX POST http://${BROKER_IP}:8082/druid/v2/?pretty=true -H 'content-type: application/json'  -d @query-raw.json
```

**query:**

```json
{
  "queryType": "topN",
  "dataSource": "network-data",
  "granularity": "all",
  "dimension": "client_mac",
  "threshold": 10,
  "metric": "bytes",
  "aggregations": [
    {
      "name": "bytes",
      "fieldName": "sum_bytes",
      "type": "doubleSum"
    }
  ],
  "intervals": [
    "2017-02-01/2017-03-03"
  ]
}
```

**resultado:**

```json
[
  {
    "timestamp" : "2017-02-28T12:31:00.000Z",
    "result" : [ {
      "client_mac" : "84:89:ad:21:49:e9",
      "bytes" : 1.716101921E9
    }, {
      "client_mac" : "e4:a7:a0:cf:7e:c5",
      "bytes" : 1.369409426E9
    }, {
      "client_mac" : "6c:72:e7:e4:50:b7",
      "bytes" : 1.345175188E9
    }, {
      "client_mac" : "ac:bc:32:8c:3b:15",
      "bytes" : 1.255369449E9
    }, {
      "client_mac" : "cc:f3:a5:da:a7:9f",
      "bytes" : 1.196843738E9
    }, {
      "client_mac" : "ac:5f:3e:f7:ca:ff",
      "bytes" : 1.141691446E9
    }, {
      "client_mac" : "f8:cf:c5:a9:81:45",
      "bytes" : 1.091635412E9
    }, {
      "client_mac" : "f8:1e:df:d6:ae:8d",
      "bytes" : 1.069005343E9
    }, {
      "client_mac" : "5c:c5:d4:6c:d4:23",
      "bytes" : 1.043613463E9
    }, {
      "client_mac" : "f4:5c:89:8a:af:0b",
      "bytes" : 9.36836663E8
    } ]
  }
]
```
