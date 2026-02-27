# Agent Instructions - GitCoz

Technical context and coding standards for agents operating in this repository.

## 1. Project Overview

GitCoz is an Android GitHub Explorer with a premium SaaS aesthetic.

- **Stack:** Kotlin 2.2, Jetpack Compose (Material 3), Retrofit 2, Coroutines & Flow
- **Build:** AGP 8.10.0, Gradle KTS, JVM target 17
- **Architecture:** Clean Architecture (Data → Domain ← Presentation) + MVVM
- **DI:** Manual only (`AppContainer`). Do NOT introduce Hilt, Dagger, or Koin.
- **SDK:** minSdk 32 | targetSdk 35 | compileSdk 35

## 2. Build & Test Commands

```bash
./gradlew assembleDebug          # Debug APK
./gradlew installDebug           # Install to device
./gradlew clean                  # Clean build
./gradlew test                   # All unit tests

# Single test
./gradlew :app:testDebugUnitTest --tests "com.hypex.gitcoz.ExampleUnitTest"
./gradlew :app:testDebugUnitTest --tests "com.hypex.gitcoz.ExampleUnitTest.methodName"

./gradlew lint                   # Android lint
```

## 3. Project Structure

```
app/src/main/java/com/hypex/gitcoz/
├── data/api/GitHubApi.kt        # Retrofit interface (suspend functions)
├── data/model/GitHubDtos.kt     # DTOs with @SerializedName
├── data/repository/            # Repository implementations
├── di/AppContainer.kt           # Manual DI container
├── domain/model/                # Clean domain data classes
├── domain/repository/           # Repository interface (returns Flow)
├── presentation/
│   ├── AppViewModelProvider.kt  # Centralized ViewModelProvider.Factory
│   ├── NavItems.kt              # Screen sealed class + navItems
│   ├── UiState.kt               # Sealed: Idle, Loading, Success, Error
│   ├── components/              # GitHubCard, SaaSTopBar, AppBackground
│   └── screens/<feature>/       # Screen composable + ViewModel
├── ui/theme/                    # Color.kt, Theme.kt, Type.kt
├── GitCozApp.kt                 # Application class
└── MainActivity.kt              # NavHost, bottom bar
```

## 4. Architecture Patterns

### Manual DI
- `AppContainer` → `DefaultAppContainer` in `di/AppContainer.kt`
- Dependencies as `lazy` properties. Access via `GitCozApp.container`
- ViewModels through `AppViewModelProvider.Factory`

### Data Layer
- API methods: `suspend`, use `@GET`/`@POST` + `@Path`/`@Query`
- DTOs: suffix `Dto`, use `@SerializedName`
- Map DTOs → domain models in repository via extension functions

### Domain Layer
- Models: plain `data class`, no Android deps, no suffix, `camelCase` fields
- Repository contracts: interface returning `Flow<T>`, suffix `Contract`

### Presentation Layer
- ViewModels: constructor-injected, expose `StateFlow<UiState<T>>`
- UiState pattern:
  ```kotlin
  sealed class UiState<out T> {
      object Idle : UiState<Nothing>()
      object Loading : UiState<Nothing>()
      data class Success<T>(val data: T) : UiState<T>()
      data class Error(val message: String) : UiState<Nothing>()
  }
  ```

## 5. Typography & Colors

- **Fonts:** Space Grotesk (headings), Inter (body), JetBrains Mono (stats)
- **Never use** `FontFamily.Default` — always use `MaterialTheme.typography.*`
- **Colors:** All tokens in `ui/theme/Color.kt`. NEVER hardcode hex values
- Key tokens: `CardGradientStart/End` (#6366F1/#4338CA), `ElectricViolet` (#6366F1)

## 6. UI Components

- **`GitHubCard`:** ALL entity listings. Unified indigo gradient
- **`SaaSTopBar`:** Required on every screen
- **`AppBackground`:** Wrap detail screens for gradient
- **`AnimatedBottomBar`:** Bar = `ElectricViolet`, ball = `CardStar`

## 7. Coding Standards

### Naming
| Element | Convention | Example |
|---------|------------|---------|
| Classes/Interfaces | PascalCase, no `I` prefix | `GitHubRepositoryContract` |
| Functions/Variables | camelCase | `loadTrending()` |
| ViewModels | Suffix `ViewModel` | `TrendingViewModel` |
| Repo implementations | Suffix `Impl` | `GitHubRepositoryImpl` |
| DTOs | Suffix `Dto` | `UserDto` |
| Composables | PascalCase | `TrendingScreen` |

### Imports
Order: `androidx` → `com.hypex` → third-party → stdlib. Remove unused. Wildcard `ui.theme.*` OK in components only.

### Formatting
- Indentation: 4 spaces
- Max line length: 120 chars
- Max 2 consecutive blank lines
- Functions max 40 lines; classes max 300 lines

### Null Safety & Types
- Prefer immutable `val` over `var`
- Avoid `!!`; use `?.` and `?:` instead
- Explicit types for public APIs

### Error Handling
- Wrap API calls in try-catch with meaningful errors
- Never hardcode API keys — use `BuildConfig`
- Show user-friendly messages in UiState.Error

### Coroutines
- Use `viewModelScope` in ViewModels
- Use `Dispatchers.IO` for network/disk ops

### Compose
- Use `remember` for UI state, `rememberSaveable` for persisted
- Use `LaunchedEffect` for one-time side effects
- Avoid recomposition traps

## 8. Navigation

Routes in `NavItems.kt` via `Screen` sealed class:
- `profile_detail/{username}`
- `repo_detail/{owner}/{repo}`
- Bottom bar only on top-level routes

## 9. Adding a New Feature

1. DTO in `data/model/`, domain model in `domain/model/`
2. `suspend` method in `data/api/GitHubApi.kt`
3. Contract in `domain/repository/`, impl in `data/repository/`
4. Register as `lazy` in `DefaultAppContainer`
5. ViewModel in `presentation/screens/<feature>/`
6. Register factory in `AppViewModelProvider`
7. Composable + route in `MainActivity.kt` NavHost
8. Add to `navItems` in `NavItems.kt` if top-level tab

## 10. Environment

- JDK 17 required
- Gradle JVM: `-Xmx4608m -Dfile.encoding=UTF-8`
- Repos: google(), mavenCentral(), jitpack.io
