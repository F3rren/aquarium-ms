## Stack di Monitoring e Logging - Aquarium Microservices

### Panoramica

Lo stack di monitoring e logging è composto da:
- **ELK Stack**: Elasticsearch (8.11.3), Logstash, Kibana (8.11.3) per log aggregation e visualization
- **Prometheus**: Time-series database per metrics collection  
- **Grafana**: Dashboard e visualization per metrics

### URLs dei Servizi

- **Kibana**: http://localhost:5601
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090
- **Elasticsearch**: http://localhost:9200

### Setup Completato 

Tutti i servizi sono configurati e operativi:

#### Prometheus Targets (9/9 UP)
-  ai-assistant-service:8090
-  api-gateway:8080
-  aquariums-service:8081
-  inhabitants-service:8082
-  species-service:8083
-  maintenance-service:8084
-  parameters-service:8085
-  manual-parameters-service:8086
-  target-parameters-service:8087

### ✅ Log Collection Configurato

**Filebeat** è configurato e attivo per raccogliere automaticamente i log Docker da tutti i microservizi.

Gli indici Elasticsearch vengono creati automaticamente con il pattern: `aquarium-logs-YYYY.MM.DD`

### Configurazione Kibana

**NOTA**: Gli indici sono già stati creati! Ora puoi configurare Kibana.

1. **Apri Kibana**: http://localhost:5601

2. **Crea Data View per i Log**:
   - Vai su **Menu** (☰) → **Management** → **Stack Management** → **Data Views**
   - Clicca **Create data view**
   - Compila i campi:
     - **Name**: `Aquarium Logs`
     - **Index pattern**: `aquarium-logs-*`
     - **Timestamp field**: `@timestamp`
   - Clicca **Save data view to Kibana**

3. **Visualizza i Log**:
   - Vai su **Menu** (☰) → **Analytics** → **Discover**
   - Seleziona il data view **Aquarium Logs** appena creato
   - Dovresti vedere i log JSON dei microservizi con i campi:
     - `@timestamp`: Timestamp del log
     - `container.name`: Nome del container Docker
     - `service`: Nome del microservizio (da logback)
     - `level`: Livello di log (INFO, DEBUG, ERROR, WARN)
     - `message`: Messaggio del log
     - `logger`: Logger class
     - `thread`: Thread name

4. **Filtra i Log**:
   - Per servizio: `service: "aquariums-service"`
   - Per livello: `level: "ERROR"`
   - Per messaggio: cerca nel campo message

### Grafana - Dashboard Predefinita

1. **Login a Grafana**: http://localhost:3000
   - Username: `admin`
   - Password: `admin` (cambiarla al primo accesso)

2. **Dashboard Disponibile**:
   - Nome: **Aquarium Microservices - Overview**
   - Path: **Dashboards**  **Aquarium Microservices - Overview**
   - Pannelli inclusi:
     - HTTP Request Rate
     - HTTP Request Latency (p99)
     - HTTP Success Rate (%)
     - JVM Memory Usage
     - Thread Count

3. **Datasources Preconfigurati**:
   - Prometheus (http://prometheus:9090)
   - Elasticsearch (http://elasticsearch:9200)

### Metriche Disponibili

Ogni microservizio espone metriche su `/actuator/prometheus`:

#### HTTP Metrics
- `http_server_requests_seconds`: Latency delle richieste HTTP
- `http_server_requests_seconds_count`: Conteggio richieste HTTP
- `http_server_requests_seconds_sum`: Somma latenze

Tag disponibili:
- `application`: Nome del servizio
- `uri`: Endpoint HTTP
- `method`: HTTP method (GET, POST, etc.)
- `status`: HTTP status code

#### JVM Metrics
- `jvm_memory_used_bytes`: Memoria JVM usata
- `jvm_memory_max_bytes`: Memoria JVM massima
- `jvm_threads_live_threads`: Thread attivi
- `jvm_gc_pause_seconds`: Garbage collection pause

#### Database Metrics (HikariCP)
- `hikaricp_connections_active`: Connessioni attive al database
- `hikaricp_connections_idle`: Connessioni idle
- `hikaricp_connections_pending`: Connessioni in attesa
- `hikaricp_connections_timeout_total`: Timeout connessioni

### Troubleshooting

#### Prometheus target DOWN
```bash
# Verifica lo stato del servizio
docker-compose ps <service-name>

# Controlla i log del servizio
docker logs <service-name> --tail 100

# Verifica l'endpoint Actuator
curl http://localhost:<port>/actuator/prometheus
```

#### Kibana non mostra log
```bash
# Verifica che Logstash stia ricevendo log
docker logs logstash --tail 100

# Verifica indici Elasticsearch
curl http://localhost:9200/_cat/indices?v

# Dovrebbe mostrare indici: aquarium-logs-YYYY.MM.DD
```

#### Grafana non mostra metriche
- Verifica che i Prometheus targets siano UP: http://localhost:9090/targets
- Controlla la datasource Prometheus in Grafana: Configuration  Data sources  Prometheus
- Testa la query: `up{job=~".*-service"}`

### Next Steps

1.  Verificare che tutti i target Prometheus siano UP
2.  **Configurare Kibana Data View** (vedi sezione sopra) 
3.  Accedere a Grafana e visualizzare la dashboard
4. Creare alert personalizzati in Prometheus/Grafana
5. Configurare retention policy per log e metriche
