// Bulgarian online radio stations + sample track metadata
// Logos are inline SVG marks built from each station's wordmark/initials
// so the prototype is fully self-contained.

const STATIONS = [
  {
    id: "bg-radio",
    name: "BG RADIO",
    tagline: "Най-доброто от българската музика",
    freq: "91.9 FM",
    city: "София",
    bitrate: "128 kbps",
    genre: "Поп / Рок",
    genreId: "pop",
    bg: ["#1f2c5a", "#0b1230"],
    fg: "#FFD24D",
    listeners: "12.4k",
    logo: (size = 280) => (
      <svg viewBox="0 0 280 280" width={size} height={size}>
        <defs>
          <linearGradient id="bgr-g" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0" stopColor="#22315e" />
            <stop offset="1" stopColor="#0a1029" />
          </linearGradient>
        </defs>
        <rect width="280" height="280" rx="14" fill="url(#bgr-g)" />
        <g transform="translate(140,140)">
          <circle r="78" fill="none" stroke="#FFD24D" strokeWidth="3" opacity="0.9" />
          <circle r="58" fill="none" stroke="#FFD24D" strokeWidth="1.5" opacity="0.45" />
          <text textAnchor="middle" y="-6" fontSize="44" fontWeight="800" fill="#FFD24D" letterSpacing="2">BG</text>
          <text textAnchor="middle" y="30" fontSize="18" fontWeight="600" fill="#FFD24D" letterSpacing="6">RADIO</text>
        </g>
      </svg>
    ),
  },
  {
    id: "radio-1",
    name: "Радио 1",
    tagline: "Хитовете на твоя живот",
    freq: "103.1 FM",
    city: "София",
    bitrate: "128 kbps",
    genre: "Хитове",
    genreId: "hits",
    bg: ["#c20e1a", "#5a0a10"],
    fg: "#ffffff",
    listeners: "18.1k",
    logo: (size = 280) => (
      <svg viewBox="0 0 280 280" width={size} height={size}>
        <defs>
          <linearGradient id="r1-g" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0" stopColor="#e51324" />
            <stop offset="1" stopColor="#5a0a10" />
          </linearGradient>
        </defs>
        <rect width="280" height="280" rx="14" fill="url(#r1-g)" />
        <text x="140" y="138" textAnchor="middle" fontSize="44" fontWeight="800" fill="#fff" letterSpacing="1">РАДИО</text>
        <text x="140" y="206" textAnchor="middle" fontSize="120" fontWeight="900" fill="#fff" fontFamily="'Sora', sans-serif">1</text>
      </svg>
    ),
  },
  {
    id: "n-joy",
    name: "N-JOY",
    tagline: "More music. More fun.",
    freq: "94.7 FM",
    city: "София",
    bitrate: "192 kbps",
    genre: "Поп / Денс",
    genreId: "dance",
    bg: ["#ff5a1f", "#7a1b0a"],
    fg: "#ffffff",
    listeners: "9.8k",
    logo: (size = 280) => (
      <svg viewBox="0 0 280 280" width={size} height={size}>
        <defs>
          <linearGradient id="nj-g" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0" stopColor="#ff7a3d" />
            <stop offset="1" stopColor="#a02410" />
          </linearGradient>
        </defs>
        <rect width="280" height="280" rx="14" fill="url(#nj-g)" />
        <text x="140" y="170" textAnchor="middle" fontSize="62" fontWeight="900" fill="#fff" letterSpacing="-2">N-JOY</text>
        <rect x="60" y="190" width="160" height="3" fill="#fff" opacity="0.7" />
        <text x="140" y="222" textAnchor="middle" fontSize="14" fontWeight="600" fill="#fff" letterSpacing="6" opacity="0.9">MORE MUSIC</text>
      </svg>
    ),
  },
  {
    id: "darik",
    name: "Дарик Радио",
    tagline: "Новини, анализи, разговори",
    freq: "105.4 FM",
    city: "София",
    bitrate: "128 kbps",
    genre: "Новини / Говор",
    genreId: "news",
    bg: ["#0e386e", "#04132a"],
    fg: "#5fb1ff",
    listeners: "21.6k",
    logo: (size = 280) => (
      <svg viewBox="0 0 280 280" width={size} height={size}>
        <defs>
          <linearGradient id="dr-g" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0" stopColor="#1857a8" />
            <stop offset="1" stopColor="#04132a" />
          </linearGradient>
        </defs>
        <rect width="280" height="280" rx="14" fill="url(#dr-g)" />
        <g transform="translate(140,128)">
          <path d="M-58,-30 Q-58,-50 -38,-50 L38,-50 Q58,-50 58,-30 L58,30 Q58,50 38,50 L-38,50 Q-58,50 -58,30 Z"
            fill="none" stroke="#5fb1ff" strokeWidth="3" />
          <text textAnchor="middle" y="12" fontSize="42" fontWeight="800" fill="#fff" letterSpacing="2">DARIK</text>
        </g>
        <text x="140" y="222" textAnchor="middle" fontSize="20" fontWeight="600" fill="#5fb1ff" letterSpacing="3">РАДИО</text>
      </svg>
    ),
  },
  {
    id: "city",
    name: "City Radio",
    tagline: "The Beat of the City",
    freq: "101.4 FM",
    city: "София",
    bitrate: "192 kbps",
    genre: "Денс / Електронна",
    genreId: "dance",
    bg: ["#ec1e79", "#3a0823"],
    fg: "#ffffff",
    listeners: "14.0k",
    logo: (size = 280) => (
      <svg viewBox="0 0 280 280" width={size} height={size}>
        <defs>
          <linearGradient id="ct-g" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0" stopColor="#ff3392" />
            <stop offset="1" stopColor="#3a0823" />
          </linearGradient>
        </defs>
        <rect width="280" height="280" rx="14" fill="url(#ct-g)" />
        <text x="140" y="138" textAnchor="middle" fontSize="58" fontWeight="900" fill="#fff" letterSpacing="-1" fontStyle="italic">city</text>
        <text x="140" y="180" textAnchor="middle" fontSize="20" fontWeight="700" fill="#fff" letterSpacing="8" opacity="0.85">R A D I O</text>
        <rect x="70" y="200" width="140" height="2" fill="#fff" opacity="0.5" />
      </svg>
    ),
  },
  {
    id: "vitosha",
    name: "Радио Витоша",
    tagline: "Българската народна музика",
    freq: "101.6 FM",
    city: "София",
    bitrate: "128 kbps",
    genre: "Народна",
    genreId: "folk",
    bg: ["#1a6b3a", "#062813"],
    fg: "#ffffff",
    listeners: "7.2k",
    logo: (size = 280) => (
      <svg viewBox="0 0 280 280" width={size} height={size}>
        <defs>
          <linearGradient id="vt-g" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0" stopColor="#2e9d57" />
            <stop offset="1" stopColor="#062813" />
          </linearGradient>
        </defs>
        <rect width="280" height="280" rx="14" fill="url(#vt-g)" />
        <path d="M40,180 L90,110 L130,140 L180,80 L240,180 Z" fill="#fff" opacity="0.92" />
        <path d="M40,180 L240,180 L240,210 L40,210 Z" fill="#fff" opacity="0.18" />
        <text x="140" y="240" textAnchor="middle" fontSize="20" fontWeight="700" fill="#fff" letterSpacing="3">ВИТОША</text>
      </svg>
    ),
  },
  {
    id: "z-rock",
    name: "Z-Rock",
    tagline: "Pure Rock. No compromise.",
    freq: "92.7 FM",
    city: "София",
    bitrate: "192 kbps",
    genre: "Рок / Метъл",
    genreId: "rock",
    bg: ["#1a1a1a", "#000000"],
    fg: "#ff2e2e",
    listeners: "11.3k",
    logo: (size = 280) => (
      <svg viewBox="0 0 280 280" width={size} height={size}>
        <defs>
          <linearGradient id="zr-g" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0" stopColor="#2a2a2a" />
            <stop offset="1" stopColor="#000000" />
          </linearGradient>
        </defs>
        <rect width="280" height="280" rx="14" fill="url(#zr-g)" />
        <text x="140" y="170" textAnchor="middle" fontSize="140" fontWeight="900" fill="#ff2e2e" fontStyle="italic" letterSpacing="-4">Z</text>
        <text x="140" y="218" textAnchor="middle" fontSize="22" fontWeight="800" fill="#fff" letterSpacing="8">R O C K</text>
      </svg>
    ),
  },
  {
    id: "fresh",
    name: "Fresh!",
    tagline: "Fresh hits 24/7",
    freq: "88.7 FM",
    city: "София",
    bitrate: "192 kbps",
    genre: "Денс / Хитове",
    genreId: "hits",
    bg: ["#00b39f", "#022b27"],
    fg: "#ffffff",
    listeners: "8.6k",
    logo: (size = 280) => (
      <svg viewBox="0 0 280 280" width={size} height={size}>
        <defs>
          <linearGradient id="fr-g" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0" stopColor="#19d6c0" />
            <stop offset="1" stopColor="#022b27" />
          </linearGradient>
        </defs>
        <rect width="280" height="280" rx="14" fill="url(#fr-g)" />
        <text x="140" y="170" textAnchor="middle" fontSize="68" fontWeight="900" fill="#fff" fontStyle="italic" letterSpacing="-2">Fresh!</text>
        <text x="140" y="208" textAnchor="middle" fontSize="14" fontWeight="600" fill="#fff" letterSpacing="6" opacity="0.85">HITS 24/7</text>
      </svg>
    ),
  },
];

const CATEGORIES = [
  { id: "all", name: "Всички", nameEn: "All", icon: "grid", count: 8 },
  { id: "hits", name: "Хитове", nameEn: "Hits", icon: "star", count: 2 },
  { id: "pop", name: "Поп", nameEn: "Pop", icon: "music", count: 1 },
  { id: "rock", name: "Рок", nameEn: "Rock", icon: "guitar", count: 1 },
  { id: "dance", name: "Денс", nameEn: "Dance", icon: "wave", count: 2 },
  { id: "folk", name: "Народна", nameEn: "Folk", icon: "feather", count: 1 },
  { id: "news", name: "Новини", nameEn: "News", icon: "mic", count: 1 },
  { id: "classical", name: "Класическа", nameEn: "Classical", icon: "treble", count: 0 },
];

// Sample now-playing track per station
const NOW_PLAYING = {
  "bg-radio": { title: "Чисто гола", artist: "Графа feat. Михаела Маринова", album: "Single 2024", elapsed: 78, duration: 214 },
  "radio-1": { title: "Save Your Tears", artist: "The Weeknd", album: "After Hours", elapsed: 142, duration: 215 },
  "n-joy": { title: "Padam Padam", artist: "Kylie Minogue", album: "Tension", elapsed: 32, duration: 198 },
  "darik": { title: "Сутрешен блок", artist: "Дарик News — На живо", album: "12 май 2026", elapsed: 612, duration: 3600 },
  "city": { title: "Miracle", artist: "Calvin Harris, Ellie Goulding", album: "Single", elapsed: 98, duration: 207 },
  "vitosha": { title: "Полегнала е Тодора", artist: "Валя Балканска", album: "Българско народно богатство", elapsed: 45, duration: 224 },
  "z-rock": { title: "Wish You Were Here", artist: "Pink Floyd", album: "Wish You Were Here", elapsed: 188, duration: 334 },
  "fresh": { title: "Flowers", artist: "Miley Cyrus", album: "Endless Summer Vacation", elapsed: 67, duration: 200 },
};

// Recently played history (most recent first)
const RECENTLY_PLAYED = [
  { stationId: "bg-radio", track: "Чисто гола — Графа feat. Михаела Маринова", time: "14:21" },
  { stationId: "z-rock", track: "Wish You Were Here — Pink Floyd", time: "13:48" },
  { stationId: "n-joy", track: "Padam Padam — Kylie Minogue", time: "13:32" },
  { stationId: "vitosha", track: "Полегнала е Тодора — Валя Балканска", time: "12:55" },
  { stationId: "city", track: "Miracle — Calvin Harris, Ellie Goulding", time: "12:14" },
  { stationId: "fresh", track: "Flowers — Miley Cyrus", time: "11:42" },
  { stationId: "radio-1", track: "Save Your Tears — The Weeknd", time: "10:58" },
  { stationId: "darik", track: "Сутрешен блок — Дарик News", time: "09:20" },
];

const FAVORITE_IDS = ["bg-radio", "z-rock", "vitosha", "n-joy"];

Object.assign(window, { STATIONS, CATEGORIES, NOW_PLAYING, RECENTLY_PLAYED, FAVORITE_IDS });
