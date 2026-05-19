// Browse / Channels / Favorites / Categories / Search / Recent / Settings

const { useState: uS, useMemo: uM } = React;

// ────────────────────────────────────────────────────────────
// Station card (used in lists)
// ────────────────────────────────────────────────────────────
const StationCard = ({ station, active, playing, onSelect, onToggleFav, isFav, accent }) => {
  return (
    <button
      className={`station-card ${active ? "is-active" : ""}`}
      onClick={() => onSelect(station.id)}
    >
      <div className="sc-cover">{station.logo(120)}</div>
      <div className="sc-meta">
        <div className="sc-name">{station.name}</div>
        <div className="sc-genre">{station.genre}</div>
        <div className="sc-freq">{station.freq} · {station.city}</div>
      </div>
      <div className="sc-side">
        {active && playing ? (
          <div className="sc-onair">
            <Equalizer bars={4} height={18} color={accent} />
            <span style={{ color: accent }}>В ефир</span>
          </div>
        ) : (
          <div className="sc-play-hint" style={{ color: accent }}>
            <Icon name="play" size={20} />
          </div>
        )}
        <span
          className="sc-fav"
          onClick={(e) => { e.stopPropagation(); onToggleFav(station.id); }}
          style={isFav ? { color: "#ff5d7a" } : { color: "#576a85" }}
        >
          <Icon name={isFav ? "heart-filled" : "favorites"} size={20} />
        </span>
      </div>
    </button>
  );
};

// ────────────────────────────────────────────────────────────
// Channels screen
// ────────────────────────────────────────────────────────────
const ChannelsScreen = ({ activeId, playing, onSelect, favs, toggleFav, accent }) => {
  return (
    <div className="screen">
      <div className="screen-head">
        <div>
          <h1 className="screen-title">Станции</h1>
          <p className="screen-sub">{STATIONS.length} български онлайн радио · сортирани по популярност</p>
        </div>
        <div className="head-actions">
          <button className="pill-btn"><Icon name="search" size={18} /><span>Търсене</span></button>
          <button className="pill-btn pill-primary" style={{ borderColor: accent, color: accent }}>
            <Icon name="plus" size={18} /><span>Добави URL</span>
          </button>
        </div>
      </div>
      <div className="card-grid">
        {STATIONS.map((s) => (
          <StationCard
            key={s.id}
            station={s}
            active={activeId === s.id}
            playing={playing}
            onSelect={onSelect}
            isFav={favs.includes(s.id)}
            onToggleFav={toggleFav}
            accent={accent}
          />
        ))}
      </div>
    </div>
  );
};

// ────────────────────────────────────────────────────────────
// Favorites
// ────────────────────────────────────────────────────────────
const FavoritesScreen = ({ activeId, playing, onSelect, favs, toggleFav, accent }) => {
  const list = STATIONS.filter((s) => favs.includes(s.id));
  return (
    <div className="screen">
      <div className="screen-head">
        <div>
          <h1 className="screen-title">Любими</h1>
          <p className="screen-sub">{list.length} запазени станции · бърз достъп от Now Playing</p>
        </div>
      </div>
      {list.length === 0 ? (
        <div className="empty">
          <Icon name="favorites" size={56} style={{ color: "#3d4d66" }} />
          <p>Все още нямаш любими.<br />Натисни <span style={{ color: accent }}>♥</span> до която и да е станция.</p>
        </div>
      ) : (
        <div className="card-grid">
          {list.map((s) => (
            <StationCard
              key={s.id} station={s} active={activeId === s.id} playing={playing}
              onSelect={onSelect} isFav toggleFav={toggleFav} onToggleFav={toggleFav} accent={accent}
            />
          ))}
        </div>
      )}
    </div>
  );
};

// ────────────────────────────────────────────────────────────
// Categories
// ────────────────────────────────────────────────────────────
const CategoriesScreen = ({ activeId, playing, onSelect, favs, toggleFav, accent }) => {
  const [cat, setCat] = uS("all");
  const filtered = uM(() => cat === "all" ? STATIONS : STATIONS.filter((s) => s.genreId === cat), [cat]);
  return (
    <div className="screen">
      <div className="screen-head">
        <div>
          <h1 className="screen-title">Жанрове</h1>
          <p className="screen-sub">Филтрирай по тип съдържание</p>
        </div>
      </div>
      <div className="cat-row">
        {CATEGORIES.map((c) => (
          <button
            key={c.id}
            className={`cat-chip ${cat === c.id ? "is-active" : ""}`}
            onClick={() => setCat(c.id)}
            style={cat === c.id ? { borderColor: accent, color: accent, background: `${accent}14` } : {}}
            disabled={c.count === 0}
          >
            <Icon name={c.icon} size={20} />
            <span className="chip-name">{c.name}</span>
            <span className="chip-count">{c.count}</span>
          </button>
        ))}
      </div>
      {filtered.length === 0 ? (
        <div className="empty">
          <Icon name="categories" size={56} style={{ color: "#3d4d66" }} />
          <p>В тази категория още няма станции.</p>
        </div>
      ) : (
        <div className="card-grid">
          {filtered.map((s) => (
            <StationCard
              key={s.id} station={s} active={activeId === s.id} playing={playing}
              onSelect={onSelect} isFav={favs.includes(s.id)} onToggleFav={toggleFav} accent={accent}
            />
          ))}
        </div>
      )}
    </div>
  );
};

// ────────────────────────────────────────────────────────────
// Search
// ────────────────────────────────────────────────────────────
const SearchScreen = ({ activeId, playing, onSelect, favs, toggleFav, accent }) => {
  const [q, setQ] = uS("");
  const results = uM(() => {
    const needle = q.trim().toLowerCase();
    if (!needle) return [];
    return STATIONS.filter((s) =>
      s.name.toLowerCase().includes(needle) ||
      s.genre.toLowerCase().includes(needle) ||
      s.city.toLowerCase().includes(needle) ||
      s.freq.toLowerCase().includes(needle),
    );
  }, [q]);

  const KEYS = ["Й","Ц","У","К","Е","Н","Г","Ш","Щ","З","Х","Ф","Ы","В","А","П","Р","О","Л","Д","Ж","Э","Я","Ч","С","М","И","Т","Ь","Б","Ю"];

  return (
    <div className="screen">
      <div className="screen-head">
        <div>
          <h1 className="screen-title">Търсене</h1>
          <p className="screen-sub">Търси по име, жанр, град или честота</p>
        </div>
      </div>
      <div className="search-bar">
        <Icon name="search" size={22} style={{ color: accent }} />
        <input
          autoFocus
          placeholder={'напр. „рок“, „новини“, „София“…'}
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />
        {q && (
          <button className="search-clear" onClick={() => setQ("")}>
            <Icon name="x" size={18} />
          </button>
        )}
      </div>

      {!q && (
        <div className="search-suggest">
          <div className="suggest-label">Бързи филтри</div>
          <div className="suggest-chips">
            {["Рок", "Новини", "Народна", "Денс", "Хитове", "София"].map((t) => (
              <button key={t} className="cat-chip" onClick={() => setQ(t.toLowerCase())}>
                <span className="chip-name">{t}</span>
              </button>
            ))}
          </div>
        </div>
      )}

      {q && (
        <div className="card-grid">
          {results.length === 0 ? (
            <div className="empty">
              <Icon name="search" size={56} style={{ color: "#3d4d66" }} />
              <p>Няма съвпадения за „<span style={{ color: accent }}>{q}</span>".</p>
            </div>
          ) : (
            results.map((s) => (
              <StationCard
                key={s.id} station={s} active={activeId === s.id} playing={playing}
                onSelect={onSelect} isFav={favs.includes(s.id)} onToggleFav={toggleFav} accent={accent}
              />
            ))
          )}
        </div>
      )}
    </div>
  );
};

// ────────────────────────────────────────────────────────────
// Recently played
// ────────────────────────────────────────────────────────────
const RecentScreen = ({ activeId, playing, onSelect, accent }) => {
  return (
    <div className="screen">
      <div className="screen-head">
        <div>
          <h1 className="screen-title">Скоро слушани</h1>
          <p className="screen-sub">Последните {RECENTLY_PLAYED.length} часа · автоматична история</p>
        </div>
        <div className="head-actions">
          <button className="pill-btn"><Icon name="x" size={16} /><span>Изчисти историята</span></button>
        </div>
      </div>
      <div className="recent-list">
        {RECENTLY_PLAYED.map((r, i) => {
          const s = stationById(r.stationId);
          if (!s) return null;
          return (
            <button key={i} className="recent-row" onClick={() => onSelect(s.id)}>
              <div className="rr-time">{r.time}</div>
              <div className="rr-cover">{s.logo(64)}</div>
              <div className="rr-meta">
                <div className="rr-station">{s.name}<span className="rr-freq">· {s.freq}</span></div>
                <div className="rr-track">{r.track}</div>
              </div>
              <div className="rr-side">
                {activeId === s.id && playing ? (
                  <Equalizer bars={4} height={18} color={accent} />
                ) : (
                  <span className="rr-play" style={{ color: accent }}><Icon name="play" size={20} /></span>
                )}
              </div>
            </button>
          );
        })}
      </div>
    </div>
  );
};

// ────────────────────────────────────────────────────────────
// Settings
// ────────────────────────────────────────────────────────────
const SettingsScreen = ({ accent }) => {
  const groups = [
    {
      title: "Възпроизвеждане", titleEn: "Playback",
      rows: [
        { label: "Качество", val: "Високо (192 kbps)", action: "select" },
        { label: "Авто-старт при стартиране", val: "Включено", action: "toggle", on: true },
        { label: "Кросфейд между станции", val: "2.0 сек", action: "select" },
      ],
    },
    {
      title: "Дисплей", titleEn: "Display",
      rows: [
        { label: "Тема", val: "Автоматична (ден/нощ)", action: "select" },
        { label: "Плътност на UI", val: "Просторно", action: "select" },
        { label: "Намалена анимация", val: "Изключено", action: "toggle", on: false },
      ],
    },
    {
      title: "Звук", titleEn: "Audio",
      rows: [
        { label: "Еквалайзер", val: "Bass Boost", action: "select" },
        { label: "Loudness", val: "Включено", action: "toggle", on: true },
        { label: "Audio output", val: "Bluetooth · Mercedes-Benz", action: "select" },
      ],
    },
    {
      title: "Кола", titleEn: "Vehicle",
      rows: [
        { label: "Пауза при изключен двигател", val: "Включено", action: "toggle", on: true },
        { label: "Mute при заден ход", val: "Включено", action: "toggle", on: true },
        { label: "Управление от волана", val: "Свързано", action: "select" },
      ],
    },
  ];
  return (
    <div className="screen">
      <div className="screen-head">
        <div>
          <h1 className="screen-title">Настройки</h1>
          <p className="screen-sub">Car-Radio v2.4.1 · оптимизирано за Android head units</p>
        </div>
      </div>
      <div className="settings-grid">
        {groups.map((g) => (
          <div key={g.title} className="settings-card">
            <div className="settings-head">
              <span className="settings-title">{g.title}</span>
              <span className="settings-en">{g.titleEn}</span>
            </div>
            <div className="settings-rows">
              {g.rows.map((r) => (
                <div key={r.label} className="settings-row">
                  <div className="sr-label">{r.label}</div>
                  {r.action === "toggle" ? (
                    <div className={`tog ${r.on ? "on" : ""}`} style={r.on ? { background: accent } : {}}>
                      <span className="tog-knob" />
                    </div>
                  ) : (
                    <div className="sr-val">
                      <span>{r.val}</span>
                      <Icon name="chevron-right" size={16} style={{ color: "#576a85" }} />
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

Object.assign(window, {
  StationCard, ChannelsScreen, FavoritesScreen, CategoriesScreen,
  SearchScreen, RecentScreen, SettingsScreen,
});
