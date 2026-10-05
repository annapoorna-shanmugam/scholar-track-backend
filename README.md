# Scholar Track backend

A Java (Spring Boot) + PostgreSQL backend for the Scholar Track practice site, plus the
frontend (the same HTML you already reviewed) served straight out of this same app.

Right now it's seeded with **Nursery**, and the subjects **English / Maths / Science** —
with **no questions yet**. Questions get added later once the real set is ready; until
then, each subject shows a "Coming soon" state in the UI.

## What's here

- `src/main/java/com/scholartrack/model` — the four database tables: `Grade`, `Subject`,
  `Question`, `Attempt`.
- `src/main/java/com/scholartrack/repository` — Spring Data JPA repositories (no SQL to
  write by hand).
- `src/main/java/com/scholartrack/controller` — the REST endpoints (see below).
- `src/main/java/com/scholartrack/service/ProgressService.java` — turns raw practice
  attempts into everything the Progress screen shows (streak, accuracy, badges).
- `src/main/java/com/scholartrack/config/DataSeeder.java` — inserts Nursery +
  English/Maths/Science on first startup, and only then (safe to restart any time).
- `src/main/resources/static/index.html` — the frontend. Spring Boot serves this
  automatically, so opening `http://localhost:8080/` shows the whole site.

## API

| Method | Path | What it does |
|---|---|---|
| GET | `/api/grades` | List all grades |
| GET | `/api/grades/{gradeId}/subjects` | Subjects for a grade |
| GET | `/api/subjects/{subjectId}/questions?limit=N` | Up to N questions for a subject |
| GET | `/api/subjects/{subjectId}/questions/count` | How many questions exist for a subject |
| POST | `/api/attempts` | Record one completed practice round `{gradeId, subjectId, correct, total}` |
| DELETE | `/api/attempts` | Wipe all recorded practice rounds (Settings → Reset) |
| GET | `/api/progress` | Everything the Progress screen needs, precomputed |

## One important caveat

I built this in a cloud sandbox that can't reach Maven Central (the repository Java
projects download their libraries from), so **I was not able to actually compile or run
this project myself.** I checked it as carefully as I could without that:

- Every file parses as valid Java (checked with `javac`) — the only errors it reports are
  "cannot find symbol" for Spring/Jakarta classes, which is exactly what's expected when
  those libraries aren't on the classpath, and nothing else.
- The code follows standard, well-worn Spring Boot patterns throughout.

That said, the very first real build is happening on your machine, not mine. If `mvn
spring-boot:run` fails, **paste me the error output** and I'll fix it — don't spend time
debugging Spring stack traces alone if you're new to this.

---

## Setup on macOS

### 1. Install Homebrew (skip if you already have it)

Open **Terminal** and run:

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

Follow any instructions it prints at the end (it sometimes asks you to run a couple of
extra lines to add Homebrew to your PATH).

### 2. Install Java 17

```bash
brew install openjdk@17
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

(If you're on an older Intel Mac, `brew` installs to `/usr/local` instead of
`/opt/homebrew` — Homebrew's own install output will tell you which path it used.)

Check it worked:

```bash
java -version
```

You should see something mentioning `17`.

### 3. Install Maven

```bash
brew install maven
mvn -version
```

### 4. Install and start PostgreSQL

```bash
brew install postgresql@16
brew services start postgresql@16
```

Create the database and a user for this app to use (these exact names match
`src/main/resources/application.properties`, so don't change one without the other):

```bash
createdb scholartrack
psql scholartrack -c "CREATE USER scholartrack WITH PASSWORD 'scholartrack';"
psql scholartrack -c "GRANT ALL PRIVILEGES ON DATABASE scholartrack TO scholartrack;"
psql scholartrack -c "ALTER DATABASE scholartrack OWNER TO scholartrack;"
```

### 5. Run the app

From inside the unzipped `scholar-track-backend` folder:

```bash
mvn spring-boot:run
```

The first run will take a minute or two — Maven is downloading Spring Boot itself.
Once you see a line like `Started ScholarTrackBackendApplication in ... seconds`, open:

```
http://localhost:8080/
```

You should see the site — Home, Olympiad Demo, Progress, Settings — now backed by a real
database instead of anything hardcoded. Practice a round in Olympiad Demo (English/Maths/
Science will show "Coming soon" until real questions are added), then check Progress —
it should reflect what you just did.

To stop the app, go back to Terminal and press `Ctrl+C`.

### Everyday use after this first setup

You only need to repeat steps 4's `brew services start postgresql@16` (Postgres needs to
be running) and step 5's `mvn spring-boot:run` each time — steps 1–4's installs are
one-time.

## Adding real questions later

Once you share the real question set, the cleanest way in is a few rows in the
`questions` table (via `psql`, or I can write a small importer if you hand me the
questions in a spreadsheet/CSV/text format) — no code changes needed, since the API and
frontend already handle however many questions exist per subject.
