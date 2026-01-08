# Monitoring & Logging - Aquarium Microservices

Questo documento descrive il sistema di monitoring e logging implementato per il progetto Aquarium Microservices.

## Stack di Monitoring e Logging

### ELK Stack (Logging)
- **Elasticsearch** (porta 9200): Storage dei log
- **Logstash** (porta 5000, 12201): Elaborazione e aggregazione log
- **Kibana** (porta 5601): Visualizzazione e analisi log

### Prometheus & Grafana (Metrics)
- **Prometheus** (porta 9090): Raccolta metriche
- **Grafana** (porta 3000): Dashboard e visualizzazione metriche

---

## Quick Start

### 1. Avviare l'infrastruttura completa

```bash
docker-compose up -d
```

Questo avvierà tutti i servizi inclusi ELK, Prometheus e Grafana.

### 2. Accedere alle interfacce web

- **Kibana**: http://localhost:5601
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090

---

## Configurazione Servizi

### Prerequisiti per ogni microservizio

Per abilitare monitoring e logging su tutti i servizi, aggiungi al `pom.xml`:

```xml
<!-- Monitoring & Metrics -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>

<!-- Logging -->
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

### Configurazione application.properties

Aggiungi al `application.properties` di ogni servizio:

```properties
# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus,loggers
management.endpoint.health.show-details=always
management.endpoint.prometheus.enabled=true
management.metrics.export.prometheus.enabled=true
management.metrics.tags.application=${spring.application.name}

# Logging Configuration
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.level.it.f3rren.aquarium=INFO
logging.level.org.springframework.web=INFO
```

### Configurazione Logback

Copia il file `logback-spring.xml` nella cartella `src/main/resources` di ogni servizio.

---

## Utilizzo

### Kibana - Visualizzazione Log

1. Accedi a Kibana: http://localhost:5601
2. Vai su **Management** > **Index Patterns**
3. Crea un index pattern: `aquarium-logs-*`
4. Seleziona `@timestamp` come campo temporale
5. Vai su **Discover** per visualizzare i log in tempo reale

**Esempi di query utili:**
- Tutti gli errori: `level: "ERROR"`
- Log di un servizio specifico: `service: "aquariums-service"`
- Errori di un servizio: `service: "aquariums-service" AND level: "ERROR"`

### Grafana - Dashboard Metriche

1. Accedi a Grafana: http://localhost:3000 (admin/admin)
2. La dashboard "Aquarium Microservices Overview" è già pre-configurata
3. Visualizza:
   - Request rate per servizio
   - Response time (p99)
   - Success rate
   - Utilizzo memoria JVM
   - Thread attivi

**Dashboard disponibili:**
- **Aquarium Microservices Overview**: Vista generale di tutti i servizi
- Puoi creare dashboard personalizzate usando il datasource Prometheus

### Prometheus - Metriche Raw

Accedi a Prometheus: http://localhost:9090

**Query utili:**
```promql
# Request rate
rate(http_server_requests_seconds_count[1m])

# Response time p99
histogram_quantile(0.99, rate(http_server_requests_seconds_bucket[1m]))

# Memory usage
jvm_memory_used_bytes

# CPU usage
process_cpu_usage

# Active connections
hikaricp_connections_active
```

---

## Metriche Esposte

Ogni servizio espone metriche su:
- `http://localhost:808X/actuator/prometheus`

### Metriche principali:

1. **HTTP Requests**
   - `http_server_requests_seconds_count`: Conteggio richieste
   - `http_server_requests_seconds_sum`: Tempo totale richieste
   - `http_server_requests_seconds_bucket`: Istogramma tempi di risposta

2. **JVM**
   - `jvm_memory_used_bytes`: Memoria utilizzata
   - `jvm_memory_max_bytes`: Memoria massima
   - `jvm_threads_live_threads`: Thread attivi
   - `jvm_gc_*`: Statistiche Garbage Collection

3. **Database (HikariCP)**
   - `hikaricp_connections_active`: Connessioni attive
   - `hikaricp_connections_idle`: Connessioni idle
   - `hikaricp_connections_pending`: Connessioni in attesa

4. **System**
   - `process_cpu_usage`: Utilizzo CPU
   - `system_cpu_usage`: CPU sistema
   - `process_uptime_seconds`: Uptime del processo

---

## Troubleshooting

### Elasticsearch non parte
- Verifica memoria disponibile (richiede almeno 512MB)
- Controlla i log: `docker logs elasticsearch`

### Logstash non riceve log
- Verifica che i servizi abbiano la configurazione logback-spring.xml
- Controlla che logstash sia in ascolto: `docker logs logstash`

### Prometheus non raccoglie metriche
- Verifica che l'endpoint actuator/prometheus sia accessibile
- Controlla i targets in Prometheus: http://localhost:9090/targets
- Assicurati che i servizi abbiano le dipendenze actuator e micrometer

### Grafana non mostra dati
- Verifica che il datasource Prometheus sia configurato correttamente
- Controlla che i servizi siano in esecuzione
- Attendi qualche minuto per la raccolta iniziale dei dati

---

## Struttura File

```
.
├── docker-compose.yml              # Configurazione container
├── logstash/
│   ├── Dockerfile                  # Build Logstash custom
│   └── logstash.conf               # Pipeline configuration
├── prometheus/
│   └── prometheus.yml              # Scrape configuration
└── grafana/
    └── provisioning/
        ├── datasources/
        │   └── datasources.yml     # Auto-configured datasources
        └── dashboards/
            ├── dashboard.yml       # Dashboard provisioning
            └── aquarium-overview.json  # Pre-built dashboard
```

---

## Best Practices

1. **Logging**
   - Usa livelli di log appropriati (DEBUG, INFO, WARN, ERROR)
   - Includi contesto utile nei log (IDs, parametri rilevanti)
   - Evita log eccessivi in produzione

2. **Metriche**
   - Monitora sempre: request rate, latency, error rate
   - Imposta alert per metriche critiche
   - Usa tag per categorizzare metriche (service, domain, etc.)

3. **Dashboard**
   - Crea dashboard per dominio (core, inhabitants, parameters)
   - Monitora risorse (CPU, memoria, connessioni DB)
   - Imposta time range appropriati (5m, 1h, 24h)

4. **Performance**
   - Limita retention dei log (attualmente 7 giorni)
   - Configura rollover degli indici Elasticsearch
   - Monitora l'utilizzo disco dei volumi Docker

---

## Prossimi Passi

1. **Alerting**: Configura Alertmanager per notifiche via email/Slack
2. **Distributed Tracing**: Aggiungi Zipkin/Jaeger per tracciamento distribuito
3. **Advanced Dashboards**: Crea dashboard specifiche per business metrics
4. **Security**: Abilita autenticazione su Elasticsearch e Prometheus
5. **Backup**: Implementa backup automatico dei dati Elasticsearch e Grafana

---

## Servizi e Porte

| Servizio | Porta | Descrizione |
|----------|-------|-------------|
| Elasticsearch | 9200, 9300 | Storage log |
| Logstash | 5000, 12201 | Ingestion log |
| Kibana | 5601 | UI log analysis |
| Prometheus | 9090 | Metrics storage |
| Grafana | 3000 | Metrics visualization |
| API Gateway | 8080 | Entry point |
| Aquariums Service | 8081 | Core service |
| Inhabitants Service | 8082 | Inhabitants |
| Species Service | 8083 | Species catalog |
| Maintenance Service | 8084 | Maintenance tasks |
| Parameters Service | 8085 | Water parameters |
| Manual Parameters | 8086 | Manual readings |
| Target Parameters | 8087 | Target ranges |
| AI Assistant | 8090 | AI service |

---

## Supporto

Per problemi o domande:
1. Controlla i log dei container: `docker logs <container-name>`
2. Verifica lo stato dei servizi: `docker-compose ps`
3. Riavvia un servizio specifico: `docker-compose restart <service-name>`

