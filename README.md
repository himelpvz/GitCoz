# GitCoz

GitCoz is a premium-styled Android GitHub explorer built with Kotlin and Jetpack Compose.

## Features
- GitHub login and profile view
- Trending repositories (last 3 months)
- Search repositories and users
- Release feed (latest + your releases)
- Repository and profile detail screens
- Skeleton loading states for smooth UX
- In-app feedback screen with Telegram delivery
- Theme customization in Settings

## Tech Stack
- Kotlin 2.2
- Jetpack Compose (Material 3)
- Retrofit + Gson
- Coroutines + Flow
- Clean Architecture + MVVM
- Manual DI (`AppContainer`)

## Requirements
- JDK 17
- Android SDK 35
- Android device/emulator (minSdk 32)

## Setup
1. Create `gradle.properties` from the example:
   - use `gradle.properties.example`
2. Fill your values:
   - `TELEGRAM_BOT_TOKEN`
   - `TELEGRAM_CHAT_ID`

## Build
```bash
sh ./gradlew assembleDebug
sh ./gradlew installDebug
sh ./gradlew assembleRelease
```

## GitHub Actions Signed Release
Workflow: `.github/workflows/android-signed-release.yml`

Add these repository secrets before running:
- `ANDROID_SIGNING_KEYSTORE_BASE64`
- `ANDROID_SIGNING_STORE_PASSWORD`
- `ANDROID_SIGNING_KEY_ALIAS`
- `ANDROID_SIGNING_KEY_PASSWORD`

## APK Size Notes
- Debug APK can be large (~70MB) due to no shrink/minify.
- Release uses minify + resource shrink + ABI split APKs for much smaller output.

## Project Structure
```
app/src/main/java/com/hypex/gitcoz/
  data/
  domain/
  presentation/
  di/
  ui/theme/
```

## License
Add your preferred license (MIT/Apache-2.0/etc.).
