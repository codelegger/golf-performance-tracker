# Golf Performance Tracker

An offline-first Android app that lists golf players and their shot performance metrics,
backed by a REST API and cached locally with Room. Built with Jetpack Compose and MVVM.

| Players (light) | Player detail (dark) | Shot bottom sheet | Offline cache |
|---|---|---|---|
| ![Players](screenshots/players-light.png) | ![Detail](screenshots/detail-dark.png) | ![Shot sheet](screenshots/shot-bottom-sheet.png) | ![Offline](screenshots/offline-cache.png) |

Player avatars are loaded from the API (`avatarUrl`) with **Coil**, falling back to colored
initials. Club badges follow Rapsodo's club color palette.

## Features

- **Player list** with search/filter by name or club.
- **Player detail** with animated hero stats (avg ball speed, avg carry).
- **Stats visualization**: per-club stat pills + a custom-drawn shot map (carry × launch).
- **Shots** per player; tap a shot for full metrics (ball speed, launch angle, carry, spin) in a bottom sheet.
- **Offline-first**: all data is cached in Room and readable with no connectivity; a background
  WorkManager job refreshes when back online.
- **Light & dark themes**, Rapsodo-style data-forward UI with club category colors.

## Tech stack

- **Kotlin**, Coroutines + Flow
- **Jetpack Compose** + Material 3 (single-Activity)
- **MVVM** + Repository pattern, single source of truth (Room)
- **Hilt** for dependency injection
- **Room** for local persistence (offline cache)
- **Retrofit 2 + Moshi** for the REST API
- **WorkManager** for background sync
- **Coil** for loading player avatar images
- **Navigation Compose**
- Tests: JUnit, Turbine, MockK, kotlinx-coroutines-test

## Setup & build

### Requirements
- Android Studio (latest stable) / JDK 17
- Android SDK 36, min SDK 24

### 1. Configure the API base URL
Data comes from a [MockAPI](https://mockapi.io/) project exposing two resources:
- `GET /players`
- `GET /players/{id}/shots`

Set your base URL in
[`NetworkModule.kt`](app/src/main/java/com/codelegger/golfperformancetracker/di/NetworkModule.kt):

```kotlin
private const val BASE_URL = "https://<your-project-id>.mockapi.io/api/v1/"
```

**Player** resource fields: `name`, `club`, `avatarUrl`, `averageBallSpeed`, `averageCarryDistance`.
**Shot** resource fields (child of `players`): `ballSpeed`, `launchAngle`, `carryDistance`,
`clubType`, `spinRate`, `createdAt`.

> The app is offline-first: with an unreachable URL it runs and shows cached data
> (an empty list on first run) rather than crashing.

### 2. Build & run
```bash
./gradlew assembleDebug        # build the APK
./gradlew installDebug         # install on a connected device/emulator
./gradlew testDebugUnitTest    # run unit tests
./gradlew lintDebug            # run Android lint
```

## Architecture

See [ARCHITECTURE.md](ARCHITECTURE.md) for the design and the decisions behind it. In short:

```
UI (Compose)  →  ViewModel (StateFlow)  →  Repository (SSOT)  →  Room (local) + Retrofit (remote)
```

Data flows **down** as immutable UI state; events flow **up**. The repository reads from Room
and writes the network response into Room, so the UI always observes a single source of truth.

## Modules

The project is split into three Gradle modules (dependency direction `:app → :data → :domain`):

```
:domain   Pure Kotlin — model/ (Player, Shot) + repository/ interfaces. No Android/network/db.
:data     Room (local/), Retrofit+Moshi (remote/), mapper/, repository/ impls, di/ (Hilt),
          work/ (WorkManager sync). Exposes :domain via `api(...)`.
:app      Compose UI — theme/, navigation/, players/ (list + detail), components/ —
          plus GolfApplication (@HiltAndroidApp) and MainActivity.
```

`:domain` depends on nothing (just coroutines for `Flow`), keeping the core business types
free of framework concerns and instantly unit-testable.

## Testing

Unit tests cover DTO parsing, DTO↔Entity↔domain mapping, ViewModel state (list, search,
detail) using Turbine, and the club-color mapping. Run with `./gradlew testDebugUnitTest`.
