# FocusGuard

<p align="center"><strong>Protect your study time.</strong><br />Lightweight Android app-time limits for social media apps.</p>

<p align="center">
<img src="https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white" alt="Android" />
<img src="https://img.shields.io/badge/language-Kotlin-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin" />
<img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
<img src="https://img.shields.io/badge/license-TBD-lightgrey" alt="License" />
</p>

FocusGuard reduces social-media distractions by tracking daily usage, enforcing per-app limits, and showing a lock screen when a configured allowance is exhausted.

> **Status:** active development. Core limits, usage tracking, Accessibility Service enforcement, blocking screen, PIN/password protection, and permission shortcuts are implemented. Test on target devices because Android OEM behavior differs.

## Features

- Dark, lightweight Material 3 interface.
- Native Android 12+ compatible splash screen.
- Per-app daily limits for:
  - TikTok — `com.zhiliaoapp.musically`
  - Facebook — `com.facebook.katana`
  - YouTube — `com.google.android.youtube`
  - Instagram — `com.instagram.android`
- Independent app toggles and editable limits from `1` to `720` minutes.
- Quick limit presets: `10`, `15`, `30`, `45`, `60`, and `90` minutes.
- Real daily usage through `UsageStatsManager`.
- Accessibility-driven foreground-app detection.
- Full-screen blocking activity when an enabled app reaches its limit.
- Temporary five-minute unlock after successful authentication.
- PIN or password protection:
  - PIN: 4–6 digits.
  - Password: minimum 8 characters, including symbols and special characters.
- Salted `PBKDF2WithHmacSHA256` credential hashing.
- SQLite storage for security credentials and lockout state.
- Settings shortcuts for Usage Access, Accessibility Service, and overlay permission.
- Local dark glass-style cards with lightweight drawable assets.

## Screens

- **Home** — usage summary and quick actions.
- **App Time Limits** — per-app limits, switches, progress, and edit dialog.
- **PIN & Security** — setup, change, disable, and protection status.
- **Settings** — system permission status and shortcuts.
- **Block screen** — shown when an app reaches its daily limit.

## Requirements

- Android Studio with Android SDK.
- JDK supported by the installed Android Gradle Plugin.
- Android device or emulator running API 24 or newer.
- Android SDK 37 for the current project configuration.

| Setting | Value |
| --- | --- |
| Application ID | `com.example.focusguard` |
| Module | `:app` |
| Minimum SDK | 24 |
| Target SDK | 37 |
| Compile SDK | 37 |
| Kotlin | `2.2.10` |
| Compose BOM | `2026.02.01` |
| Java compatibility | 11 |

## Setup

1. Clone repository.
2. Open it in Android Studio.
3. Sync Gradle files.
4. Connect a device or start an emulator.
5. Build and run the `app` configuration.

From project root:

```powershell
cd C:\Users\Deshan\Desktop\Projects\Android\FocusGuard
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
```

If Java is not found, set Android Studio's embedded JDK as Gradle JDK:

```text
File > Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK
```

## Required permissions

FocusGuard cannot grant special permissions itself. Enable them manually through Android Settings from:

```text
FocusGuard > Settings
```

### Usage Access

Required for daily usage totals:

```text
Settings > Apps > Special app access > Usage access > FocusGuard
```

Enable usage tracking.

### Accessibility Service

Required to detect monitored apps entering foreground and enforce blocking:

```text
Settings > Accessibility > Installed apps > FocusGuard
```

Enable FocusGuard's Accessibility Service.

### Overlay permission

Used for displaying blocking UI where supported:

```text
Settings > Apps > Special app access > Display over other apps > FocusGuard
```

FocusGuard includes a full-screen `BlockActivity` fallback when overlay permission is unavailable.

## How enforcement works

1. The Accessibility Service receives window-state changes.
2. FocusGuard identifies TikTok, Facebook, YouTube, or Instagram.
3. Current enabled state and daily limit are read from Preferences DataStore.
4. `UsageStatsManager` calculates usage from local midnight to now.
5. While a monitored app remains foreground, usage is checked periodically.
6. `BlockActivity` appears when usage reaches the configured limit.
7. The user must authenticate with the existing FocusGuard PIN/password.
8. A successful five-minute unlock stores a package-specific expiry timestamp.

Usage resets naturally at local midnight because queries use the current local calendar day.

## Data storage

### Preferences DataStore

```text
app_limit_enabled_<packageName>
app_limit_minutes_<packageName>
unlock_until_<packageName>
```

### SQLite

Security data is stored in `security.db`:

```text
enabled
salt
hash
credential_type
failed_attempts
lockout_until
```

Plain PINs and passwords are never stored.

## Security

Credentials use:

```text
PBKDF2WithHmacSHA256
```

Current implementation uses:

- Random 16-byte salt.
- 120,000 PBKDF2 iterations.
- 256-bit derived hash.
- Constant-time hash comparison.
- Five failed attempts followed by a 30-second lockout.

Password hashing is one-way. FocusGuard does not decrypt or recover stored credentials.

## Project structure

```text
app/src/main/java/com/example/focusguard/
├── blocking/       BlockActivity.kt
├── data/           AppLimit and app-limit storage
├── monitoring/     Accessibility and UsageStats monitoring
├── permissions/    Special-permission checks and intents
├── security/       SQLite security storage and hashing
└── ui/             Compose screens, components, and theme
```

## Performance choices

- No network access.
- No Firebase, Hilt, Room, Retrofit, Coil, Glide, or Lottie.
- No continuous foreground service.
- Usage queries run on `Dispatchers.IO`.
- Accessibility monitoring is event-driven.
- Active-app checks use a low-frequency interval.
- Compose state is lightweight.
- App icons are local drawable resources.

## Known limitations

- Usage Access and Accessibility Service require manual enablement.
- Android and OEMs may delay, suppress, or alter accessibility events.
- UsageStats data may lag behind real-time usage.
- Background activity launch restrictions vary by Android version and manufacturer.
- The blocker cannot control Android system screens, uninstall flows, or OEM security UI.
- Reinstalling or updating the app may require re-enabling Accessibility Service.
- Aggressive battery optimization may stop or delay the Accessibility Service.
- No schedule feature, user-selectable app list, notifications, biometrics, or cloud sync yet.

## Roadmap

- Improve OEM-specific service reliability.
- Add optional midnight temporary unlock.
- Add usage refresh indicators and permission diagnostics.
- Add automated unit and instrumentation coverage.
- Add user-selectable monitored apps.
- Add optional study schedules.

## License

License has not been selected yet. Add license text before publishing or distributing this project publicly.

## Disclaimer

FocusGuard relies on Android special-access APIs. Blocking behavior may vary by device manufacturer, Android version, battery manager, and user-granted permissions. Do not treat FocusGuard as a guaranteed parental-control or security boundary.
