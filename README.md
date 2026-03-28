# OpenURLFile

A simple Android app to open `.url` files (Windows Internet Shortcuts) and extract the URL inside.  
A classic usage is to easily share URLs between PCs and Android devices through .url files stored in Google Drive.

<a href="https://github.com/AdrienVR/OpenURLFile/releases"><img src="https://img.shields.io/github/release/AdrienVR/OpenURLFile?style=flat-square" alt="Release"></a>
<a href="https://android.com"><img src="https://img.shields.io/badge/platform-Android-green?style=flat-square" alt="Platform"></a>
<a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/language-Kotlin-orange?style=flat-square" alt="Language"></a>
<a href="https://github.com/AdrienVR/OpenURLFile/blob/main/LICENSE"><img src="https://img.shields.io/github/license/AdrienVR/OpenURLFile?style=flat-square" alt="License"></a>

## Features

- Open `.url` files directly from any app (Files, Drive, Gmail, etc.)
- Extract and display the URL from Windows Internet Shortcut format
- Open URL in your preferred browser
- Copy URL to clipboard
- Material Design 3 UI
- Works with content:// URIs (to support Google Drive, Gmail, etc.)

## Supported File Sources

- File managers (Files, Solid Explorer, ES File Manager)
- Google Drive
- Gmail attachments
- Any app that shares `.url` files

## Screenshots

| |
|:---:|
| ![URL Display](docs/screenshot1.png) |

## Installation

### From Release

Download the latest APK from the [Releases](https://github.com/AdrienVR/OpenURLFile/releases) page.

### Build from Source

```bash
# Clone the repository
git clone https://github.com/AdrienVR/OpenURLFile.git
cd OpenURLFile

# Build debug APK
./gradlew assembleDebug

# APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

## Usage

1. Install the app
2. Open any `.url` file from your file manager or app
3. The app will extract and display the URL
4. Tap **Open in Browser** or **Copy URL**

## Permissions

| Permission | Purpose |
|------------|---------|
| `INTERNET` | Required to open URLs in browser |

No other permissions required.

## File Format

The app parses the Windows Internet Shortcut format:

```ini
[InternetShortcut]
URL=https://example.com
```

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose with Material 3
- **Architecture:** Clean Architecture (UI → Domain → Data layers)
- **Target SDK:** 34
- **Min SDK:** 24

## Project Structure

```
app/src/main/java/com/urlopener/
├── MainActivity.kt          # Main activity with UI
├── data/
│   └── UrlFileRepository.kt # Data layer (URI handling)
├── domain/
│   └── UrlParser.kt         # Domain layer (URL parsing logic)
├── ui/theme/
│   └── Theme.kt             # Material 3 theme
└── util/
    └── Logger.kt            # Debug logging
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

Distributed under the MIT License. See [LICENSE](LICENSE) for more information.
