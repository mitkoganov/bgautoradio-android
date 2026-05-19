# Handoff: Car Radio — Android Head Unit (Bulgarian Online Radio)

## Overview

A production-ready visual redesign for an Android car-radio app targeting head units, Carlinkit T-Box Ambient, Android AI Box devices, and wide automotive screens. The app streams Bulgarian online radio stations. The UI is inspired by the "Car Radio – ultimate radio" concept (see `reference.png`) and styled to look like a factory automotive head unit: angled top-bar chrome with a cyan accent edge, a cinematic dark stage with a reflective dotted "floor," and a perspective-mirrored album cover.

**This handoff replaces the current theme of your Android app.** All screens, typography, colors, spacing, and chrome described below should be re-implemented in your existing Android codebase (Kotlin/Java + XML, or Jetpack Compose — whichever your project uses).

---

## About the Design Files

The files in this bundle are **design references created in HTML / React (Babel in-browser)**. They are prototypes showing the intended **look, layout, and behavior** — they are **not** production code to copy directly into the Android app.

The task is to **recreate these designs in the Android codebase using its established patterns** (Compose, View system, Material 3 theming, Glide/Coil for images, ExoPlayer for streams, etc.). If the project is fresh and has no established stack, prefer **Jetpack Compose** with Material 3 — it maps cleanly to the layout primitives used in the mocks.

---

## Fidelity

**High-fidelity.** All colors, type sizes, spacing, border radii, and interaction states below are final values. Recreate them pixel-perfectly. The target canvas is **1920 × 720 px** (the dominant aspect ratio for modern Android head units, AI boxes, and ultra-wide automotive displays); the layout must also degrade gracefully to **1280 × 720** and **1024 × 600**.

---

## Target Devices & Screen Sizing

| Device class | Resolution | Aspect | Notes |
|---|---|---|---|
| Ultra-wide head unit (primary) | 1920 × 720 | 8:3 | Tesla-style, AI box |
| Standard HD head unit | 1280 × 720 | 16:9 | Most factory units |
| Compact head unit | 1024 × 600 | 17:10 | Older Android units |

All layouts must scale the **stage** uniformly to fit the device window with letterboxing on black (the HTML mock does exactly this — see `app.jsx` → `Stage` component).

---

## Screens

The app has **7 screens**. The top-bar chrome and the persistent **Channels** button never change. The home (house) icon always returns to **Now Playing**.

### 1. Now Playing (default screen)

Replicates `reference.png` 1:1.

- **Background**
  - Base radial: `radial-gradient(120% 70% at 50% -10%, #0e2440 0%, #050a14 55%, #02060c 100%)`
  - Center spot: radial cyan glow `#4DD3FF26` at 38% × 100% from center
  - Floor: bottom 58% gets a soft cyan top-glow + black bottom-vignette
  - Floor dots: 14 × 14 px grid of `rgba(180,210,255,0.22)` 1 px dots, masked top→bottom with a fade, transformed `perspective(800px) rotateX(64deg)` to create the receding-floor effect
- **Layout** — single full-bleed stage, no left rail on this screen.
  - Two columns: `1fr 1.05fr`, gap **60 px**, padding `110 px 120 px 0 220 px`
  - Left column: 320 × 320 px album cover, right-aligned in column
  - Right column: station meta (radio name, song, artist)
  - Top-right of right column: tiny frequency block (`91.9FM` over `Bulgaria`)
  - Bottom-center: floating transport "pill"
- **Album cover** (320 × 320)
  - 2 px solid border `rgba(220,230,245,0.85)`
  - Background `#0b1830`
  - Shadow: `0 30px 80px rgba(0,0,0,0.7), 0 0 0 1px rgba(255,255,255,0.04) inset, 0 0 60px rgba(95,182,255,0.18)`
  - Diagonal sheen overlay (linear-gradient 160°)
  - **Reflection**: a vertically-mirrored copy directly below, height 200 px, opacity-masked top→bottom, border opacity reduced to 0.4
- **Meta column** (gap 28 px, padding-top 56 px)
  - Row 1: radio icon (32 px) + station name — **44 px / 500 / +1.5 px letter-spacing, white**
  - Row 2: music icon (26 px) + song title — **32 px / 400 / +1 px**, no-wrap with ellipsis
  - Row 3: user icon (24 px) + artist — **24 px / 400 / rgba(255,255,255,0.7)**
- **Frequency** (absolute, top 170, right 120)
  - Line 1: `91.9FM` — 20 px / 500 / white
  - Line 2: `Bulgaria` — 16 px / 400 / rgba(255,255,255,0.7)
- **Transport pill** (bottom 22, centered)
  - Padding 10 × 18 px, fully-rounded, `rgba(8,16,28,0.72)` with `backdrop-filter: blur(20)` and 1 px `rgba(120,170,255,0.18)` border
  - Children, left → right: prev (42 px round), play/pause (50 px round, 2 px cyan border + cyan glow), next (42 px round), 1 px vertical divider, favorite heart (turns `#ff5d7a` when active), volume icon + 120 × 4 px slider, 5-bar live equalizer

### 2. Channels (browse all)

- **Header**: title "Станции", subtitle "8 български онлайн радио · сортирани по популярност", right-aligned pill buttons "Търсене" and "Добави URL" (cyan-bordered).
- **Grid**: 4 columns, 16 px gap. Each card:
  - 14 px padding, 16 px radius, `rgba(255,255,255,0.045)` panel with 1 px border
  - Hover: lifts 1 px, panel goes to `0.07`
  - Active station (currently playing): cyan tinted background + 3 px cyan left edge
  - Layout: 80 px cover (12 px radius) | name (18 / 600) + genre (12 / muted) + freq (11 / dimmer) | side column with play icon + heart

### 3. Favorites

Same card grid as Channels, filtered to the user's favorites (stored as an ID list). Empty state: large heart icon (`#3d4d66`), copy "Все още нямаш любими. Натисни ♥ до която и да е станция."

### 4. Categories (genres)

- **Chip row** at top: `Всички / Хитове / Поп / Рок / Денс / Народна / Новини / Класическа` with category count badge. Active chip: cyan border + tinted background. Disabled (count = 0) chips have 0.35 opacity.
- Below: same 4-column card grid as Channels.

### 5. Search

- **Search bar**: 16 × 22 px padding, `rgba(255,255,255,0.07)` background, 1 px `rgba(255,255,255,0.18)` border, 16 px radius. Cyan search icon, 20 px input. Right side has a clear (×) button when input is non-empty.
- **Empty state**: shows "Бързи филтри" chip row (Рок, Новини, Народна, Денс, Хитове, София).
- **Results**: same card grid, filtered against name + genre + city + frequency.

### 6. Recent (recently played)

Vertical list of rows, 8 px gap. Each row: timestamp `HH:MM` (tabular nums, 50 px min-width) | 56 px square cover | station name + freq + track title | live equalizer if currently playing, else play arrow.

### 7. Settings

Two-column grid (1fr 1fr), 14 px gap. Four cards: Възпроизвеждане, Дисплей, Звук, Кола. Each card:
- 18 × 22 px padding, 16 px radius
- Header: bilingual (BG title 18/600 + EN small-caps 11/letter-spacing 2)
- Rows: label left, value-with-chevron OR toggle right. Toggle is 44 × 24 pill with 18 px round knob — when on, background turns cyan and knob goes dark.

---

## Top Bar (persistent)

The **defining piece of chrome**. 66 px tall, two chamfered floating segments — one anchored top-left, one anchored top-right — with a cyan accent edge that traces the diagonal cut.

- **Background per segment**: `linear-gradient(180deg, rgba(8,16,28,0.96), rgba(8,16,28,0.78))`
- **Bottom border**: 1 px `rgba(120,170,255,0.18)`
- **Left segment** (width 350 px): clip-path polygon `0 0, 100% 0, calc(100% - 32px) 100%, 0 100%`
- **Right segment** (width 220 px): mirror polygon `32px 0, 100% 0, 100% 100%, 0 100%`
- **Accent line**: 60 × 2 px `linear-gradient(90deg, transparent, #5fb6ff)` with `0 0 10px #5fb6ff99` glow, positioned at `bottom: -1px`, skewed 45° to align with the chamfer
- **Left contents**: 64 × 66 home button (28 px icon) | 18 px gap | "Channels" with 26 px stacked-lines-plus icon, 22 px / 400 white text. Hover turns text/icon cyan `#5fb6ff`.
- **Right contents**: clock (26 / 500 white, tabular-nums, real-time) | 64 × 66 gear button

---

## Left Rail (non-Now-Playing screens only)

110 px wide, vertical list of icon+label buttons. Items: В ефир, Станции, Любими, Жанрове, Търсене, Скоро. Each:
- 44 × 44 icon area + 11 px / 600 BG label below
- Active: cyan text + cyan 3 px left-edge indicator with glow

---

## Mini Player (non-Now-Playing screens only)

Floating at `left: 24, right: 24, bottom: 16`, 76 px tall, fully rounded 18 px. Background `rgba(8,16,28,0.85)` + backdrop-blur 24. Contains: 60 px cover, station name + freq, current track, 5-bar live equalizer, prev/play/next controls. Clicking anywhere except controls navigates to Now Playing.

---

## Design Tokens

### Colors

| Token | Value | Usage |
|---|---|---|
| `bg.0` | `#03070d` | Page outer |
| `bg.1` | `#08111e` | Mid bg |
| `bg.2` | `#0b1828` | Panel base |
| `panel` | `rgba(255,255,255,0.045)` | Card surfaces |
| `panel.2` | `rgba(255,255,255,0.07)` | Card hover / inputs |
| `panel.3` | `rgba(255,255,255,0.10)` | Heavier surfaces |
| `border` | `rgba(255,255,255,0.08)` | Default borders |
| `border.2` | `rgba(255,255,255,0.18)` | Stronger borders |
| `text` | `#E8EFF8` | Primary text |
| `text.2` | `#98A8BF` | Secondary text |
| `text.3` | `#5D6E87` | Muted |
| `text.4` | `#3F4F66` | Disabled / empty state |
| `accent` | `#4DD3FF` | Primary cyan (tweakable: `#4DD3FF`, `#7EE8C7`, `#FFB454`, `#FF6188`, `#A78BFA`) |
| `accent.line` | `#5fb6ff` | Top-bar chamfer accent |
| `danger` | `#ff5d7a` | Favorites / live dot |

### Typography

- **Font family**: Sora (Google Fonts). Weights used: 300, 400, 500, 600, 700, 800.
- **Sora is the only family.** Use system fallback `system-ui, sans-serif`.

| Use | Size / weight / tracking |
|---|---|
| Now Playing — station name | 44 / 500 / +1.5 |
| Now Playing — song title | 32 / 400 / +1 |
| Now Playing — artist | 24 / 400 / +0.5 |
| Now Playing — frequency line 1 | 20 / 500 |
| Now Playing — frequency line 2 | 16 / 400 |
| Top-bar Channels label | 22 / 400 |
| Top-bar clock | 26 / 500 (tabular-nums) |
| Screen title | 36 / 700 |
| Screen subtitle | 14 / 400 / text.3 |
| Station card name | 18 / 600 |
| Station card genre | 12 / 400 / text.2 |
| Station card freq | 11 / 400 / text.3 |
| Rail label | 11 / 600 |
| Chip / pill button | 14 / 500 |
| Settings group title BG | 18 / 600 |
| Settings group title EN | 11 / small-caps / +2 |
| Settings row label | 14 / 400 |

### Spacing & Radii

- Base unit: **4 px**. Common: 4 / 8 / 10 / 14 / 16 / 18 / 22 / 28 / 32 / 56 / 60.
- Radii: pills are `999px`; cards `16px`; buttons `10–14px`; cover **0 px** (square, only 2 px border).
- Card padding: `14 px` (station cards), `18 × 22 px` (settings cards).

### Shadows

- Cover: `0 30px 80px rgba(0,0,0,0.7), inset 0 0 0 1px rgba(255,255,255,0.04), 0 0 60px rgba(95,182,255,0.18)`
- Mini player: `0 20px 50px rgba(0,0,0,0.5)`
- Transport pill: `0 12px 40px rgba(0,0,0,0.5)`
- Top-bar accent line glow: `0 0 10px rgba(95,182,255,0.6)`

### Animations

- **Equalizer bars**: 5 thin bars, `eq-bounce` keyframes 18% → 65% → 100% height, 0.6–0.96 s ease-in-out infinite alternate, per-bar delay of `i × 0.13s`.
- **Spectrum strip**: 48–64 bars, similar but taller and gradient-filled.
- **Live dot pulse**: opacity 1 → 0.4 → 1, 1.4 s.
- **Card hover lift**: `transform: translateY(-1px)` over 150 ms.

---

## Stations (seed data)

8 Bulgarian online radio stations. Each has an SVG logo mark generated from initials (replace with real station artwork in production). See `data.jsx` for full data.

| ID | Name | Freq | City | Genre | Live URL (TODO) |
|---|---|---|---|---|---|
| `bg-radio` | BG Radio | 91.9 FM | София | Поп / Рок | — |
| `radio-1` | Радио 1 | 103.1 FM | София | Хитове | — |
| `n-joy` | N-JOY | 94.7 FM | София | Поп / Денс | — |
| `darik` | Дарик Радио | 105.4 FM | София | Новини / Говор | — |
| `city` | City Radio | 101.4 FM | София | Денс / Електронна | — |
| `vitosha` | Радио Витоша | 101.6 FM | София | Народна | — |
| `z-rock` | Z-Rock | 92.7 FM | София | Рок / Метъл | — |
| `fresh` | Fresh! | 88.7 FM | София | Денс / Хитове | — |

The **engineer must wire each station to its real streaming URL** (Shoutcast/Icecast MP3 or HLS) and use **ExoPlayer** for playback. Metadata (now-playing title/artist) should come from the stream's ICY tags where available.

---

## Interactions & Behavior

| Interaction | Behavior |
|---|---|
| Tap a station card | Select station + start playback + navigate to Now Playing |
| Tap home icon | Navigate to Now Playing (does not stop playback) |
| Tap Channels | Navigate to Channels screen |
| Tap settings gear | Navigate to Settings screen |
| Tap play/pause | Toggle ExoPlayer playback |
| Tap prev/next | Cycle to previous/next station in the list (wrap around) |
| Tap heart | Toggle station in favorites (persist to SharedPreferences / DataStore) |
| Tap volume slider | Set volume to clicked position (also fires `AudioManager.setStreamVolume`) |
| Tap mini player body | Navigate to Now Playing |
| Mini player controls | Same as transport, scoped to current station, do not navigate |
| Tap category chip | Filter Channels grid by `genreId` |
| Type in Search | Live-filter against `name`, `genre`, `city`, `freq` (case-insensitive) |
| Tap row in Recent | Navigate to Now Playing with that station selected |
| Tap settings toggle | Persist immediately; for "Auto-pause on engine off" hook into ACC-state via Android Auto / car APIs |
| Hardware steering-wheel buttons | Next/prev/play-pause must be mapped via `MediaSession` so the head unit hardware buttons work |
| Back gesture / hardware back | Navigate to previous screen; from Now Playing, no-op (it's home) |

---

## State Management

| State | Scope | Persistence |
|---|---|---|
| `currentStationId` | App-wide (singleton / DI) | DataStore |
| `isPlaying` | App-wide | Not persisted (ExoPlayer is source of truth) |
| `favorites: List<String>` | App-wide | DataStore |
| `recentlyPlayed: List<{stationId, trackTitle, time}>` | App-wide, max 50 | Room DB |
| `volume` | System | `AudioManager` |
| `accentColor`, `theme` | User pref | DataStore |
| `screen` | Navigation | Compose NavController / Fragment back-stack |

---

## Assets

- **Font**: Sora (Google Fonts) — bundle locally for offline use or load via downloadable fonts.
- **Icons**: 30+ line icons, 24 × 24 viewBox, stroke 1.6, all in `icons.jsx`. Re-export as a Compose icon set or vector drawables (`res/drawable/ic_*.xml`). The unique top-bar icon is `channels-plus` (stacked lines with a small + on the right).
- **Station logos**: generated inline SVGs as placeholders. Replace each with the real station artwork (likely 512 × 512 PNG sourced from each broadcaster's media kit). Cache via Coil/Glide; show the SVG initial-mark while loading.
- **Reference screenshot**: `reference.png` — the user-provided target look.

---

## Files in This Bundle

| File | What it is |
|---|---|
| `Car Radio.html` | Entry point. Loads React + Babel, defines the page chrome, CSS variables, and all global styles. **Read the `<style>` block — it contains every design token used.** |
| `app.jsx` | Top-level `App` component, screen router, mini-player, tweaks panel wiring. |
| `core.jsx` | `TopBar`, left `Rail`, `NowPlaying`, `Equalizer`, `Spectrum` helpers. |
| `screens.jsx` | `ChannelsScreen`, `FavoritesScreen`, `CategoriesScreen`, `SearchScreen`, `RecentScreen`, `SettingsScreen`, and the shared `StationCard`. |
| `data.jsx` | Bulgarian station catalog, sample track metadata, recently-played history, default favorites, category list. |
| `icons.jsx` | Inline SVG icon set. |
| `tweaks-panel.jsx` | Design-tool helper — **not needed in production**, can be ignored. |
| `reference.png` | Original visual reference from the user. |

---

## Implementation Notes for Claude Code

1. **Don't copy the HTML/CSS verbatim.** Translate every value into the target framework's idioms. For Compose: a `Theme.kt` with `ColorScheme` + `Typography`, plus per-screen `@Composable` functions. For the View system: `themes.xml` + `colors.xml` + custom `View` subclasses for the chamfered top-bar segments and the cover-with-reflection.
2. **Top-bar chamfers** in Compose: use `Modifier.clip(GenericShape)` with the polygon points listed above; the accent line is a thin `Box` rotated with `Modifier.graphicsLayer(rotationZ = 45f)`.
3. **Reflective floor**: a `Canvas` composable drawing a dotted grid with `drawIntoCanvas { it.skia... }` perspective transform, OR a pre-rendered PNG with `mask` applied via `Modifier.drawWithContent` + `BlendMode.DstIn`.
4. **Album cover reflection**: render the cover into an `ImageBitmap`, then draw it again flipped with a vertical gradient alpha mask below the original — same trick as the HTML mock.
5. **Playback**: `ExoPlayer` with `MediaSourceFactory` for HLS or progressive MP3, wrapped in a `MediaSessionService` so Android Auto and the car's hardware buttons drive it.
6. **Localization**: keep Bulgarian as the default language but route all user-visible strings through `strings.xml` with `values-bg/` and `values-en/` so the bilingual labels (the BG/EN pair in the rail and settings) remain consistent.
7. **Theme replacement**: this design **replaces** the current theme entirely. Remove the old `colors.xml`, `themes.xml`, and any old layouts before recreating from this spec.
