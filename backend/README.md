# Backend — Authify (Spring Boot)

Descrizione
-----------
Il backend di Authify è un'API RESTful enterprise costruita con Spring Boot (Java 21). 
Fornisce autenticazione tramite JWT, gestione OTP per reset password e verifica email, storage persistente su MySQL dei dati dell'utente,
azione come il Login e codici OTP hashati. Invio email tramite SMTP. 

## Integrazione future
- login history
- rateLimiter

# Avvio docker
mvn clean package -DskipTests
docker compose up -d --build 