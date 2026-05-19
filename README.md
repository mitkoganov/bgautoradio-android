# Bulgarian Auto Radio

Android приложение за онлайн радио, оптимизирано за **Android head units** — Carlinkit T-Box Ambient, Android AI Box и подобни.

**Статус на build:** ✅ `BUILD SUCCESSFUL` — тествано на 2026-05-12

---

## APK файл

```
app\build\outputs\apk\debug\app-debug.apk   (~21 MB)
```

---

## Бърз старт — build от сорс

### Изисквания

| | |
|---|---|
| Android Studio | Hedgehog 2023.1.1+ (носи JDK 17 и Android SDK) |
| Android SDK | API 35 — инсталирай от SDK Manager |
| Gradle | 8.7 (изтегля се автоматично) |

### Стъпки

```bat
cd C:\tmp\BulgarianAutoRadio

REM Само при първо използване — изтегля Gradle 8.7 (~120 MB)
gradlew.bat assembleDebug
```

**APK ще бъде тук:**
```
app\build\outputs\apk\debug\app-debug.apk
```

> **Ако `gradlew.bat` се оплаче за `gradle-wrapper.jar`:**
> `gradle-wrapper.jar` вече е наличен в проекта (`gradle\wrapper\gradle-wrapper.jar`).
> Ако все пак липсва — отвори проекта в Android Studio, то ще го регенерира.

---

## Debug APK

```bat
gradlew.bat assembleDebug
```

Резултат: `app\build\outputs\apk\debug\app-debug.apk`

Debug APK-то е автоматично подписано с debug ключ на Android Studio — **не е нужен допълнителен ключ**.

---

## Release APK (за разпространение)

### Стъпка 1: Генерирай keystore (еднократно)

```bat
"C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" ^
  -genkeypair -v ^
  -keystore bulgarian-radio-release-key.jks ^
  -keyalg RSA ^
  -keysize 2048 ^
  -validity 10000 ^
  -alias bulgarian-radio
```

> Запомни паролите — без тях не можеш да обновяваш приложението!

### Стъпка 2: Добави в `local.properties`

```properties
STORE_FILE=C:/tmp/BulgarianAutoRadio/bulgarian-radio-release-key.jks
STORE_PASSWORD=ТВОЯТА_ПАРОЛА
KEY_ALIAS=bulgarian-radio
KEY_PASSWORD=ТВОЯТА_KEY_ПАРОЛА
```

### Стъпка 3: Добави signing config в `app/build.gradle.kts`

В `android {}` блока (след `buildFeatures {}`):

```kotlin
import java.util.Properties

val localProps = Properties().also { props ->
    rootProject.file("local.properties").takeIf { it.exists() }
        ?.inputStream()?.use { props.load(it) }
}
```

В `buildTypes { release { ... } }` добави:
```kotlin
release {
    signingConfig = signingConfigs.create("release").apply {
        storeFile     = localProps["STORE_FILE"]?.let { file(it as String) }
        storePassword = localProps["STORE_PASSWORD"] as String?
        keyAlias      = localProps["KEY_ALIAS"] as String?
        keyPassword   = localProps["KEY_PASSWORD"] as String?
    }
    isMinifyEnabled   = true
    isShrinkResources = true
    proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}
```

### Стъпка 4: Build

```bat
gradlew.bat assembleRelease
```

Резултат: `app\build\outputs\apk\release\app-release.apk`

---

## Инсталация на Carlinkit T-Box — 3 метода

### Метод 1: USB флашка (най-лесно, без компютър до колата)

1. Форматирай USB флашка като **FAT32**
2. Копирай APK-то:
   ```
   app\build\outputs\apk\debug\app-debug.apk  →  USB\
   ```
3. Постави флашката в USB порта на T-Box
4. Отвори **File Manager** на T-Box → навигирай до флашката
5. Натисни `app-debug.apk`
6. Ако се появи "Allow installs from this source" → **Allow**
7. **Install** → **Open**

### Метод 2: Browser download (без флашка)

1. Качи APK-то в Google Drive или Telegram (изпрати на себе си)
2. На T-Box отвори Chrome браузъра
3. Влез в Google Drive / Telegram Web → изтегли APK-то
4. Натисни известието за изтегления файл → **Install**

Алтернативно — изпрати директно чрез `adb push` + shell команда (вж. Метод 3).

### Метод 3: ADB (за разработчици)

#### Включи USB Debugging на T-Box

1. **Settings → About → Build Number** — натисни 7 пъти
2. **Settings → Developer Options → USB Debugging** → **On**

#### Свързване по WiFi

```bat
REM Намери IP от Settings → WiFi → Details
set ADB=C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools\adb.exe

%ADB% connect 192.168.1.XXX:5555
%ADB% devices
REM Трябва да видиш: 192.168.1.XXX:5555   device
```

#### Свързване по USB кабел

```bat
set ADB=C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools\adb.exe
%ADB% devices
```

#### Инсталация — Debug APK

```bat
set ADB=C:\Users\%USERNAME%\AppData\Local\Android\Sdk\platform-tools\adb.exe
set APK=C:\tmp\BulgarianAutoRadio\app\build\outputs\apk\debug\app-debug.apk

%ADB% install -r "%APK%"
```

#### Инсталация — Release APK

```bat
set APK=C:\tmp\BulgarianAutoRadio\app\build\outputs\apk\release\app-release.apk
%ADB% install -r "%APK%"
```

#### Стартирай директно след инсталация

```bat
REM За debug build (applicationId = com.bgautoradio.debug)
%ADB% shell am start -n com.bgautoradio.debug/.MainActivity

REM За release build
%ADB% shell am start -n com.bgautoradio/.MainActivity
```

#### Полезни ADB команди

```bat
REM Виж логове в реално време
%ADB% logcat | findstr bgautoradio

REM Виж само грешки
%ADB% logcat *:E

REM Изтрий данните (нулиране на приложението)
%ADB% shell pm clear com.bgautoradio.debug

REM Деинсталирай
%ADB% uninstall com.bgautoradio.debug
```

---

## Какво е тествано

| | Статус |
|---|---|
| `gradlew.bat assembleDebug` | ✅ BUILD SUCCESSFUL (31s) |
| APK размер | ✅ 21.8 MB |
| INTERNET permission | ✅ |
| FOREGROUND_SERVICE permission | ✅ |
| FOREGROUND_SERVICE_MEDIA_PLAYBACK | ✅ |
| landscape orientation (`android:screenOrientation="landscape"`) | ✅ |
| Room database (SQLite cache) | ✅ |
| DataStore Preferences | ✅ |
| Media3 ExoPlayer + HLS | ✅ |
| Hilt dependency injection | ✅ |
| JSON каталог (50 станции bundled) | ✅ |

---

## Грешки при build — бързи решения

### `JAVA_HOME not set`

```bat
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
set PATH=%JAVA_HOME%\bin;%PATH%
gradlew.bat assembleDebug
```

### `SDK location not found`

Провери `local.properties`:
```properties
sdk.dir=C\:\\Users\\ТВОЕТО_ИМЕ\\AppData\\Local\\Android\\Sdk
```

### `gradle-wrapper.jar not found`

Отвори проекта в Android Studio — ще регенерира автоматично.
Или: `gradle wrapper --gradle-version 8.7`

### `Theme not found` / Resource errors

Провери `app\src\main\res\values\themes.xml` — трябва да има:
```xml
parent="Theme.AppCompat.DayNight.NoActionBar"
```

---

## Добавяне на нови станции

Файл: `app\src\main\assets\bulgarian_radio_stations.json`

```json
{
  "id": "my-station",
  "name": "Моята Станция",
  "streamUrl": "http://stream.example.com:8000/live.mp3",
  "category": "pop",
  "city": "София",
  "bitrate": 128,
  "isVerified": true,
  "sortOrder": 50
}
```

След добавяне — rebuild APK и преинсталирай. Или качи JSON на GitHub и използвай **Settings → Обнови каталога**.

---

## Проверка на stream URL

```bat
REM Проверка с curl (включен в Windows 10/11)
curl -v --max-time 10 "http://stream.bnr.bg:8011/horizont.aac" 2>&1 | findstr /i "ICY HTTP Content-Type"
```

Очакван резултат: `ICY 200 OK` или `HTTP/1.1 200 OK` с `Content-Type: audio/aacp`

---

## Структура

```
BulgarianAutoRadio\
├── app\build\outputs\apk\debug\app-debug.apk   ← ГОТОВ APK
├── gradle\wrapper\gradle-wrapper.jar            ← включен
├── gradle\wrapper\gradle-wrapper.properties     ← Gradle 8.7
├── local.properties                             ← SDK path (не в Git)
└── app\src\main\
    ├── assets\bulgarian_radio_stations.json     ← 50 станции
    └── java\com\bgautoradio\...                 ← Kotlin сорс
```
