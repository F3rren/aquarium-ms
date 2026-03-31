FROM docker.elastic.co/logstash/logstash:8.11.3

# Copia la configurazione
COPY logstash.conf /usr/share/logstash/pipeline/logstash.conf

# Configura Logstash
RUN logstash-plugin install logstash-codec-json_lines
