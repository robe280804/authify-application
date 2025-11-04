# Frontend — Authify (React + Vite)

Descrizione
-----------
Il frontend di Authify è una Single Page Application realizzata con React + Vite. Fornisce le pagine di login, registrazione, verifica email e reset password. È progettato per essere servito come asset statico (CDN / static web hosting) o come parte di un deployment containerizzato.

Caratteristiche principali
-------------------------
- React (functional components)
- Vite (dev server e build veloce)
- Axios per chiamate HTTP
- Bootstrap + bootstrap-icons
- React Router per routing client-side

Indice
------
1. Requisiti
2. Variabili di ambiente
3. Esecuzione (sviluppo / produzione)
4. Build e deployment
5. Sicurezza e CORS
6. Testing e linting
7. Contatti

1) Requisiti
---------------
- Node.js (consigliato LTS: 18+ o 20)
- npm o yarn

2) Variabili di ambiente
------------------------
Il frontend legge la base URL del backend da `import.meta.env.VITE_BACKEND_URL` (file: `frontend/src/utils/constants.js`).

Esempio `.env` nella cartella `frontend/`:

```
VITE_BACKEND_URL=https://api.example.com/api/v1.0
```

Note:
- Le variabili che iniziano con `VITE_` vengono iniettate da Vite nel bundle a build time.

3) Esecuzione
--------------
Installazione dipendenze e dev server (PowerShell):

```powershell
cd frontend
npm install
npm run dev
```

Build di produzione:

```powershell
cd frontend
npm run build
# Preview della build locale
npm run preview
```

4) Build e deployment
----------------------
La cartella `dist/` generata da `npm run build` contiene gli asset statici. In produzione:

- Servire `dist/` tramite CDN o web server statico (NGINX, Azure Static Web Apps, S3 + CloudFront).
- Se containerizzi, usare un'immagine leggera (nginx/alpine) per servire i file.

Esempio Docker semplice:

```dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

5) Sicurezza e CORS
-------------------
- Il backend deve abilitare CORS solo per gli origin attesi.
- Non memorizzare JWT in localStorage senza valutare XSS; preferire cookie HttpOnly per applicazioni con maggiori requisiti di sicurezza.
- Validare e sanificare qualunque input prima di inviarlo al backend.

6) Testing e linting
---------------------
- Linting con ESLint è già configurato (`npm run lint`).
- Aggiungere test con React Testing Library e Jest per garantire regressioni.

Comandi utili:

```powershell
cd frontend
npm run lint
# npm test (se aggiunti)
```

7) Contatti
------------
Per domande sul frontend:

- Maintainer UI: Roberto Sodini (nome@example.com)

Note finali
-----------
Posso aggiungere esempi CI (GitHub Actions) per build/test e deploy automatico della SPA su GitHub Pages / Azure Static Web Apps o Google Cloud Storage.
# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) (or [oxc](https://oxc.rs) when used in [rolldown-vite](https://vite.dev/guide/rolldown)) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.
