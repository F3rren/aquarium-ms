# API Gateway - Aquarium Microservices

API Gateway per il sistema di microservizi Aquarium.

## Configurazione

Il gateway è in ascolto su **http://localhost:8080** e instrада le richieste ai seguenti microservizi:

### Route configurate:

| Path | Servizio | Porta Backend |
|------|----------|---------------|
| `/api/aquariums/**` | aquariums-service | 8081 |
| `/api/aquariums/*/inhabitants/**` | inhabitants-service | 8082 |
| `/api/species/**` | species-service | 8083 |
| `/api/aquariums/*/maintenance/**` | maintenance-service | 8084 |
| `/api/water-parameters/**` | water-parameters-service | 8085 |
| `/api/manual-parameters/**` | manual-parameters-service | 8086 |
| `/api/target-parameters/**` | target-parameters-service | 8087 |

## ⚠️ IMPORTANTE: Conflitti di Porte

Prima di avviare il gateway, devi **correggere i conflitti di porte** nei tuoi microservizi.

### Modifiche necessarie:

✅ **Tutte le porte sono state configurate correttamente!**

1. **aquariums-service**: porta **8081**
2. **inhabitants-service**: porta **8082**
3. **species-service**: porta **8083**
4. **maintenance-service**: porta **8084**
5. **parameters-service**: porta **8085**
6. **manual_parameters-service**: porta **8086**
7. **target_parameter-service**: porta **8087**

## Avvio

```bash
mvnw.cmd spring-boot:run
```

## Test

Dopo aver avviato tutti i microservizi, puoi accedere a qualsiasi endpoint tramite il gateway:

```bash
# Esempio: ottenere tutti gli acquari
curl http://localhost:8080/api/aquariums

# Esempio: ottenere le specie di pesci
curl http://localhost:8080/api/species/fishs
```

## Features

- ✅ Routing automatico ai microservizi
- ✅ CORS configurato globalmente
- ✅ Logging dettagliato per debug
- ✅ Basato su Spring Cloud Gateway (reattivo)
