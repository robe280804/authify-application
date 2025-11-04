# Backend — Authify (Spring Boot)

## Descrizione
-----------
Il backend di Authify è un'API RESTful enterprise costruita con Spring Boot (Java 21). 
Fornisce autenticazione tramite JWT, gestione OTP per reset password e verifica email, storage persistente su MySQL dei dati dell'utente,
invio email tramite SMTP, rate limiter per proteggere il sistema e storage di codici OTP eseguendo l'hashing.

-----------
## Funzionalità principali 
Vedere le classi per maggiori dettagli

- **Registrazione** con invio email di conferma.
- **Login** segue i seguenti step:
   1. Creo un DTO history del login che salverò nel DB in modo asincrono con Kafka.
   2. Eseguo l'autenticazione dell'utente; se positiva genero un access-token e un refresh-token.
   3. Salvo il refresh-token eseguendo l'hashing nel DB.
   4. Invio al producer Kafka il DTO history e ritorno un DTO con i dati del login + cookie con access token.
- **Filtro richieste** segue i seguenti step:
   1. Ottengo l'access token dall'header o dai cookie.
   2. Se presente lo valido e se positivo popolo il Security context.
   3. Se il token non è presente o non è valido controllo se l'utente ha un refresh token non scaduto valido.
   4. Se presente genero un nuovo access token, lo salvo in un cookie e popolo il Security context
- **Reset password** segue i seguenti step:
    1. Creo un OTP, lo salvo nel db eseguendo l'hashing.
    2. Invio un email all'utente contenente l'OTP in chiaro.
    3. L'utente invierà nuovamente l'OTP e la nuova password, se questo è ancora valido verrà aggiornata.
- **Verifica email** segue i seguenti step:
  1. Creo un OTP, lo salvo nel db eseguendo l'hashing.
  2. Invio un email all'utente contenente l'OTP in chiaro.
  3. Valido l'OTP e se ancora valido aggiorno il model dell'utente come validato=true

-----------
## API

- **/register** | Registrazione 
- **/user** | Info profilo utente
- **/send-reset-otp** | Invio OTP reset password
- **/reset-password** | Conferma reset password
- **/login** | Login
- **/logout** | Logout
- **/is-authenticated** | Utente autenticato?
- **/send-otp** | Invio OTP verifica email
- **/verify-otp** | Verifica OTP email

-----------
## Entità database
- **User**: rappresentazione di un utente con nome, email e password hashata.
- **Login history**: tiene traccia dei login del client con campi come email utente, data login, successo, ip utente ... (Vedere la classe per maggiori dettagli).
- **Refresh token**: tiene traccia dei refresh token emessi durante il login, utili per creare nuovi access token.
- **Reset OTP**: tiene traccia degli OTP generati per il reset password
- **Varify OTP**: tiene traccia degli OTP generati per la verifica dell'email

-----------
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

## Stack tecnologico
- **Java 21**
- ***Spring Boot**
- **Spring Web** 
- **Spring Data JPA** 
- **Spring Validation** 
- **Spring Security** 
- **Spring Mail**
- **Spring Kafka** → comunicazione asincrona basata su eventi
- **Spring Data Redis** → caching distribuito e gestione sessioni
- **JWT** → gestione dei token di autenticazione
- **Commons Codec** → per codifica e hashing
- **Spring Boot DevTools** → reload automatico in fase di sviluppo