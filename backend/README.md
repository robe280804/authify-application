# Backend — Authify (Spring Boot)

## Descrizione
-----------
Il backend di Authify è un'API RESTful enterprise costruita con Spring Boot (Java 21). 
Fornisce autenticazione tramite JWT, gestione OTP per reset password e verifica email, storage persistente su MySQL dei dati dell'utente,
azione come il Login e codici OTP hashati. Invio email tramite SMTP. 

-----------
## Funzionalità principali 
Vedere le classi per maggiori dettagli

- **Registrazione** con invio email di conferma.
- **Login** segue i seguenti step:
   1. Creo un DTO history del login che salverò nel DB in modo asincrono con Kafka.
   2. Eseguo l'autenticazione dell'utente; se positiva genero un access-token e un refresh-token.
   3. Salvo il refresh-token eseguendo l'hashing nel DB.
   4. Invio al producer Kafka il DTO history e ritorno un DTO con i dati del login.
- **Filtro richieste** segue i seguenti step:
   1. Ottengo l'access token dall'header o dai cookie.
   2. Se presente lo valido e se positivo popolo il Security context.
   3. Se il token non è presente o non è valido controllo se l'utente ha un refresh token non scaduto valido.
   4. Se presente genero un nuovo access token, lo salvo in un cookie e popolo il Security context


## Entità database
- **User**: rappresentazione di un utente con nome, email e password hashata.
- **Login history**: tiene traccia dei login del client con campi come email utente, data login, successo, ip utente ... (Vedere la classe per maggiori dettagli).
- **Refresh token**: tiene traccia dei refresh token emessi durante il login, utili per creare nuovi access token.

## Developer
1) **Creare un file .env nella root della cartella /backend con le seguenti variabili:**
   - DB_USER=
   - DB_PASSWORD=
   - JWT_SECRET=
   - EMAIL_USERNAME=
   - EMAIL_PASSWORD=
   
2) **Compila il progetto senza test, comando:** mvn clean package -DskipTests
3) **Avvia i container, comando:** docker compose up -d --build
4) **Verifica i container con i comandi:**
    - docker ps
    - docker logs -f <nome_container>

5) Passa al README della cartella /frontend per testare il tutto