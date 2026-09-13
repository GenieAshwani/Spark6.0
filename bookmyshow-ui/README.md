# Showtime UI

A plain HTML, CSS, and JavaScript frontend for the BookMyShow-style Spring Boot API in `../bookmyshow-app`.

## Run

Start the backend first:

```powershell
Set-Location ..\bookmyshow-app
mvn spring-boot:run
```

Then serve this folder with any static file server. For example:

```powershell
Set-Location ..\bookmyshow-ui
py -m http.server 5500
```

Open http://localhost:5500.

The UI expects the API at `http://localhost:8080/api/v1`. The backend must allow requests from the static server origin during local development.
