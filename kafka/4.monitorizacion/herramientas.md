# Monitorización Herramientas

### Monitorización CentOS/Ubuntu: Script Ruby
En primer lugar vamos a descargar la última versión de CentOS 7.
```
http://centos.mirror.xtratelecom.es/7/isos/x86_64/CentOS-7-x86_64-Minimal-1511.iso
```

Una vez instalado iniciamos sesión con el usuario root y hacemos un update del sistema.

```
yum update
```

Cuando hayamos actualizado el sistema vamos a instalar Ruby, utilizando para ello RVM, antes de instalar el software debemos de descargar las claves GPG.

```
gpg2 --keyserver hkp://keys.gnupg.net --recv-keys 409B6B1796C275462A1703113804BB82D39DC0E3
```

```
\curl -sSL https://get.rvm.io | bash -s stable --ruby
```
Finalmente para tener el entorno de ruby cargado deberemos ejecutar:

```
source /usr/local/rvm/scripts/rvm
```

Una vez tenemos ruby instalado y configurado, debemos instalar algunas gemas que necesita nuestro script para su correcto funcionamiento:

```
gem install mincore
```

```
gem install poseidon
```

```
gem install getopt
```

```
gem install json
```

Cuando hayamos instalados estas gemas, únicamente nos quedará instalar un herramienta conocida como **pcstat** que nos es de utilidad para analizar las páginas en cache.

```
curl -L -o pcstat https://github.com/tobert/pcstat/raw/2014-05-02-01/pcstat.x86_64
```

```
chmod +x pcstat
```

```
mv pcstat /usr/bin/
```

Ahora ya! Podemos probar el script, que podemos encontrar al final de este documento.

```
ruby rb_kmonitor.rb  -h
Usage: rb_kmonitor.rb -i interval [-t topic] [-b broker list] [-q query]
		-i interval	-> [60] 		Interval of monitorization
		-t topic	-> [rb_monitor]		Topic to produce
		-b brokers	-> [localhost:9092]	Kafka broker list separated values
		-q query	-> [all]		Query for specific metric
```
**Nota:** El script buscará los logs de Kafka en los directorios dentro de **/tmp** que cumpla la siguiente expresión regular:

```regex
\/tmp\/kafka-logs(?:-\d)?
```

Finalmente, para ejecutar el script utilizaremos:

```
ruby rb_kmonitor.rb -i 5 -t monitor -b localhost:9092
```

Si, consumimos del topic que hemos indicado veremos mensajes como los siguientes:
```json
{"timestamp":1474139509,"sensor_name":"localhost.localdomain","monitor":"cache_pages_size","value":30,"type":"system","unit":"MB"}
{"timestamp":1474139509,"sensor_name":"localhost.localdomain","monitor":"cache_pages_usage","value":13.457287738915616,"type":"system","unit":"%"}
{"timestamp":1474139509,"sensor_name":"localhost.localdomain","monitor":"cache_hits","value":100.0,"type":"system","unit":"%"}
{"timestamp":1474139509,"sensor_name":"localhost.localdomain","monitor":"disk_read_hits","value":0.0,"type":"system","unit":"%"}
{"timestamp":1474139509,"sensor_name":"localhost.localdomain","monitor":"disk_write_hits","value":100.0,"type":"system","unit":"%"}
{"timestamp":1474139509,"sensor_name":"localhost.localdomain","monitor":"disk_usage","value":23.91457479508197,"type":"system","unit":"%"}
{"timestamp":1474139509,"sensor_name":"localhost.localdomain","monitor":"network_eno16777736_tx","value":0,"type":"system","unit":"bps"}
{"timestamp":1474139509,"sensor_name":"localhost.localdomain","monitor":"network_eno16777736_rx","value":0,"type":"system","unit":"bps"}
```

### Consultando LAG
Kafka nos proporciona una herramienta para consultar el LAG que tienen los distintos consumidores. Para calcular el LAG únicamente se necesita saber el último offset de una partición y el offset actual de un grupo de consumidores para esa partición.

Utilizando la herramienta **kafka-consumer-groups.sh** podemos listar los grupos de consumidores actuales y solicitar de que particiones y por que offset están cada uno de ellos.

```
bin/kafka-consumer-groups.sh --new-consumer --bootstrap-server localhost:9092 --list
```
Este comando nos muestra los grupos de consumidores, dando una salida como la siguiente:

```
test-consumer-group-2
console-consumer-69989
```
Para preguntar por un grupo de consumidor en concreto deberíamos ejecutar lo siguiente:

```
bin/kafka-consumer-groups.sh --new-consumer --bootstrap-server localhost:9092 --describe --group console-consumer-69989
```

Y nos mostrará una salida con el siguiente formato:

```
GROUP                          TOPIC                          PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             OWNER
test-consumer-group-2          output                         0          5032656         14075668        9043012         consumer-1_/127.0.0.1
```

Cual es el offset de la partición del topic del que se encuentra leyendo el consumidor, también nos muestra el último offset y la diferencia entre ellos, que sería el LAG.


#### rb_kmonitor.rb
```ruby
require 'mincore'
require 'json'
require 'poseidon'
require 'socket'
require 'getopt/std'

@sensor_name = Socket.gethostname

opt = Getopt::Std.getopts("i:t:b:hq:")

# Print message
def logit(message)
	printf("%s\n", message)
end

# Print usage
def usage
	logit "Usage: rb_kmonitor.rb -i interval [-t topic] [-b broker list] [-q query]"
	logit "		-i interval	-> [60] 		Interval of monitorization"
	logit "		-t topic	-> [rb_monitor]		Topic to produce"
	logit "		-b brokers	-> [localhost:9092]	Kafka broker list separated values"
	logit "		-q query	-> [all]		Query for specific metric"
	exit 0
end

usage if opt["h"]

interval = if opt["i"].nil? then 60 elsif /\d+/.match(opt["i"].to_s) then opt["i"].to_i else logit "ERROR : '#{opt["i"].to_s}' NaN"; exit 0 end - 2

topic = if opt["t"].nil? then "rb_monitor" else opt["t"].to_s.strip end

broker_list = if opt["b"].nil? then "localhost:9092" else opt["b"].to_s.strip.split(/\s/) end
# Get kafka services PIDs
stats = {}

# Swap usage
def get_swap_usage ()
	swap_usage = {}

	swap_data = `cat /proc/swaps | sed '1d' | cut -f2-3`.split(/\s/)

	swap_usage["timestamp"] = @timestamp
	swap_usage["sensor_name"] = @sensor_name
	swap_usage["monitor"] = "swap_usage"
	swap_usage["value"] = (swap_data[1].to_f/swap_data[0].to_f)*100
	swap_usage["type"] = "system"
	swap_usage["unit"] = "%"

	return swap_usage
end

def get_io_operations()

	io_operations = []

	stats = `cat /proc/diskstats|grep 'sd[a-z]'`.split(/\n/)
	sleep(1)
	stats_delta = `cat /proc/diskstats|grep 'sd[a-z]'`.split(/\n/)

	total = 0
	delta_rms = 0
	delta_wms = 0

	stats.each do |line|
		if m = /\s+\d+\s+\d+\s+(?<dev>sd[a-z])\s+\d+\s+\d+\s+\d+\s+(?<rms>\d+)\s+\d+\s+\d+\s+\d+\s+(?<wms>\d+)\s+\d+\s+\d+\s+(?<tms>\d+)/.match(line)
			total = m["rms"].to_i + m["wms"].to_i
			delta_rms = m["rms"].to_i
			delta_wms = m["wms"].to_i
		end
	end

	stats_delta.each do |line|
		if m = /\s+\d+\s+\d+\s+(?<dev>sd[a-z])\s+\d+\s+\d+\s+\d+\s+(?<rms>\d+)\s+\d+\s+\d+\s+\d+\s+(?<wms>\d+)\s+\d+\s+\d+\s+(?<tms>\d+)/.match(line)
			total_delta = (m["rms"].to_i + m["wms"].to_i) - total
			total =  if total_delta == 0 then total else total_delta end

			stats_r = {}
			stats_r["timestamp"] = @timestamp
			stats_r["sensor_name"] = @sensor_name
			stats_r["monitor"] = "disk_read_hits"
			stats_r["value"] = ((m["rms"].to_i - delta_rms)/total.to_f)*100
			stats_r["type"] = "system"
			stats_r["unit"] = "%"

			io_operations << stats_r

			stats_w = {}
			stats_w["timestamp"] = @timestamp
			stats_w["sensor_name"] = @sensor_name
			stats_w["monitor"] = "disk_write_hits"
			stats_w["value"] = ((m["wms"].to_i - delta_wms)/total.to_f)*100
			stats_w["type"] = "system"
			stats_w["unit"] = "%"

			io_operations << stats_w
		end
	end

	return io_operations
end

# Disk usage
def get_disk_usage ()
	# Get disk usage
	#usage = `df . | awk '{print $5}' | sed -ne 2p |cut -d"%" -f1`
	disk_stat = `df . | awk '{print $2" "$3}' | sed -ne 2p`.split(/\s/)

	disk_usage = {}
	disk_usage["timestamp"] = @timestamp
	disk_usage["sensor_name"] = @sensor_name
	disk_usage["monitor"] = "disk_usage"
	disk_usage["value"] = (disk_stat[1].to_f/disk_stat[0].to_f) * 100
	disk_usage["type"] = "system"
	disk_usage["unit"] = "%"

	return disk_usage
end

# Network usage
def get_network_stats ()

	network_usage = []

	# Get networks statistics
	networks = `ls /sys/class/net`.split(/\n/)

	network_stats = {}

	networks.each do |network|
		if not /lo/.match(network)
			network_stats[network] = {}
			network_stats[network]["txbps"] = `cat /sys/class/net/#{network}/statistics/tx_bytes`.strip.to_i
			network_stats[network]["rxbps"] = `cat /sys/class/net/#{network}/statistics/rx_bytes`.strip.to_i
		end
	end

	sleep(1)

	networks.each do |network|

		if not /lo/.match(network)
			stats_tx = {}
			stats_tx["timestamp"] = @timestamp
			stats_tx["sensor_name"] = @sensor_name
			stats_tx["monitor"] = "network_" + network + "_tx"
			stats_tx["value"] = `cat /sys/class/net/#{network}/statistics/tx_bytes`.strip.to_i - network_stats[network]["txbps"]
			stats_tx["type"] = "system"
			stats_tx["unit"] = "bps"

			network_usage << stats_tx

			stats_rx = {}
			stats_rx["timestamp"] = @timestamp
			stats_rx["sensor_name"] = @sensor_name
			stats_rx["monitor"] = "network_" + network + "_rx"
			stats_rx["value"] = `cat /sys/class/net/#{network}/statistics/rx_bytes`.strip.to_i - network_stats[network]["rxbps"]
			stats_rx["type"] = "system"
			stats_rx["unit"] = "bps"

			network_usage << stats_rx
		end
	end

	return network_usage
end

# Cache pages usage
def get_cache_pages_stats ()

	cache_pages_stats = []

	total = 0
	cached = 0

	filter = ["recovery-point-offset-checkpoint", "cleaner-offset-checkpoint", "replication-offset-checkpoint", "meta.properties", "__consumer_offsets"]

	# TODO Change kafka directory!!
	Dir.glob("/tmp/*") do |directory|
		if File.directory?(directory) and /\/tmp\/kafka-logs(?:-\d)?/.match(directory)

			Dir.glob(directory + "/*") do |kafka_dir|
				if not filter.any? { |s| kafka_dir.include? s}

					Dir.glob(kafka_dir + "/*") do |topic_dir|

						if not File.stat(topic_dir).size == 0
							data = JSON.parse(`pcstat --json #{topic_dir}`)
							total += data[0]["pages"]
							cached += data[0]["cached"]
						end
					end
				end
			end
		end
	end

	cache_pages_size = {}
	cache_pages_size["timestamp"] = @timestamp
	cache_pages_size["sensor_name"] = @sensor_name
	cache_pages_size["monitor"] = "cache_pages_size"
	cache_pages_size["value"] = (total*File.PAGESIZE) >> 20
	cache_pages_size["type"] = "system"
	cache_pages_size["unit"] = "MB"

	cache_pages_stats << cache_pages_size

	cache_pages_usage = {}
	cache_pages_usage["timestamp"] = @timestamp
	cache_pages_usage["sensor_name"] = @sensor_name
	cache_pages_usage["monitor"] = "cache_pages_usage"
	cache_pages_usage["value"] = if total > 0 then (cached/total.to_f)*100  else 0 end
	cache_pages_usage["type"] = "system"
	cache_pages_usage["unit"] = "%"

	cache_pages_stats << cache_pages_usage

	return cache_pages_stats
end

# Cache hits
def get_cache_hits_usage ()
	# Enable kernel functions trace
	File.open('/proc/sys/kernel/ftrace_enabled','w') { |f| f.write("1") }

	kernel_functions = ["mark_page_accessed", "mark_buffer_dirty", "add_to_page_cache_lru", "account_page_dirtied"]

	`printf "mark_page_accessed\nmark_buffer_dirty\nadd_to_page_cache_lru\naccount_page_dirtied\n" > /sys/kernel/debug/tracing/set_ftrace_filter
	if ! echo 1 > /sys/kernel/debug/tracing/function_profile_enabled; then
	    echo > /sys/kernel/debug/tracing/set_ftrace_filter
	    die "ERROR: enabling function profiling. Have CONFIG_FUNCTION_PROFILER? Exiting."
	fi`

	mem_stats = {}

	File.open('/proc/meminfo', 'r') do |f|
		meminfo = f.read.split(/\n/).each do |a|
			if (m = /^(?<name>Cached|Buffers):\s+(?<value>\d{1,})\skB/.match(a))
				mem_stats[m["name"]] = (m["value"].to_i >> 10).to_s + " MB"
			end
		end
	end

	total = 0
	misses = 0

	Dir.glob("/sys/kernel/debug/tracing/trace_stat/*") do |file|

		File.open(file, 'r') do |f|
			cache_stats = {}

			f.read.split(/\n/).each do |a|

				if (m = /(?<function>mark_page_accessed|mark_buffer_dirty|add_to_page_cache_lru|account_page_dirtied)\s+(?<value>\d+).+/.match(a))
					cache_stats[m["function"]] = m["value"].to_i
				end
			end

			misses = (cache_stats["add_to_page_cache_lru"].to_i - cache_stats["account_page_dirtied"].to_i)
			total = (cache_stats["mark_page_accessed"].to_i - cache_stats["mark_buffer_dirty"].to_i)

			mem_stats["cache_misses"] = if misses < 0 then 0 else misses end
			mem_stats["cache_hits"] = total - mem_stats["cache_misses"]
			mem_stats["cache_ratio"] = if total > 0 then 100*(mem_stats["cache_hits"]/total.to_f) else 0 end
		end
	end

	ratio = 100*(mem_stats["cache_hits"]/total.to_f)

	cache_hits = {}
	cache_hits["timestamp"] = @timestamp
	cache_hits["sensor_name"] = @sensor_name
	cache_hits["monitor"] = "cache_hits"
	cache_hits["value"] = if ratio > 0 then ratio else 0 end
	cache_hits["type"] = "system"
	cache_hits["unit"] = "%"

	`echo 0 > /sys/kernel/debug/tracing/function_profile_enabled`
	`echo 1 > /sys/kernel/debug/tracing/function_profile_enabled`

	return cache_hits
end

if opt["q"]

	@timestamp = Time.now.getutc().to_i

	query = opt["q"]

	case query
		when "swap_usage"
			p get_swap_usage.to_json.to_s
		when "cache_pages_usage"
			p get_cache_pages_stats.to_json.to_s
		when "cache_hits"
			p get_cache_hits_usage.to_json.to_s
		when "disk_usage"
			p get_disk_usage.to_json.to_s
		when "io_operations"
			p get_io_operations.to_json.to_s
		when "network_stats"
			p get_network_stats.to_json.to_s
	end
else
	# Main
	producer = Poseidon::Producer.new(broker_list, "monitor_producer")

	while true

		@timestamp = Time.now.getutc().to_i

		messages = []
		messages << Poseidon::MessageToSend.new(topic, get_swap_usage.to_json)

		get_cache_pages_stats.each do |data|
			messages << Poseidon::MessageToSend.new(topic, data.to_json)
		end

		messages << Poseidon::MessageToSend.new(topic, get_cache_hits_usage.to_json)

		get_io_operations.each do |data|
			messages << Poseidon::MessageToSend.new(topic, data.to_json)
		end

		messages << Poseidon::MessageToSend.new(topic, get_disk_usage.to_json)

		get_network_stats.each do |data|
			messages << Poseidon::MessageToSend.new(topic, data.to_json)
		end

		producer.send_messages(messages)

		sleep(interval)

	end
end
```
