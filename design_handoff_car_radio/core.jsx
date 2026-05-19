// Main app — Bulgarian car-radio prototype for 1920x720 head units
// Premium automotive (MBUX inspired), cyan accent, dark glass surfaces.

const { useState, useEffect, useRef, useMemo } = React;

// ────────────────────────────────────────────────────────────
// Helpers
// ────────────────────────────────────────────────────────────
const fmtTime = (s) => {
  const m = Math.floor(s / 60), r = s % 60;
  return `${m}:${r.toString().padStart(2, "0")}`;
};
const stationById = (id) => STATIONS.find((s) => s.id === id);

// Live animated equalizer bars
const Equalizer = ({ bars = 5, playing = true, height = 28, color = "#4DD3FF" }) => {
  return (
    <div className="eq-row" style={{ height }}>
      {Array.from({ length: bars }).map((_, i) => (
        <span
          key={i}
          className={`eq-bar ${playing ? "" : "paused"}`}
          style={{
            background: color,
            animationDelay: `${(i * 0.13).toFixed(2)}s`,
            animationDuration: `${0.6 + (i % 3) * 0.18}s`,
          }}
        />
      ))}
    </div>
  );
};

// Live waveform / spectrum strip (bigger)
const Spectrum = ({ playing = true, count = 64, color = "#4DD3FF" }) => {
  return (
    <div className="spectrum">
      {Array.from({ length: count }).map((_, i) => (
        <span
          key={i}
          className={`spec-bar ${playing ? "" : "paused"}`}
          style={{
            background: `linear-gradient(to top, ${color}33, ${color})`,
            animationDelay: `${(i * 0.04).toFixed(2)}s`,
            animationDuration: `${0.7 + (i % 5) * 0.12}s`,
          }}
        />
      ))}
    </div>
  );
};

// ────────────────────────────────────────────────────────────
// Top bar — angled chrome that matches the reference screenshot
// ────────────────────────────────────────────────────────────
const TopBar = ({ screen, onNav, clock, accent }) => {
  return (
    <header className="topbar">
      <div className="tb-chrome tb-chrome-left">
        <button className="tb-home" onClick={() => onNav("now")} aria-label="Home">
          <Icon name="home" size={28} stroke={2} />
        </button>
        <button
          className={`tb-channels ${screen === "channels" ? "is-on" : ""}`}
          onClick={() => onNav("channels")}
          style={screen === "channels" ? { color: accent } : {}}
        >
          <Icon name="channels-plus" size={26} stroke={2} />
          <span>Channels</span>
        </button>
      </div>
      <div className="tb-chrome tb-chrome-right">
        <span className="tb-clock">{clock}</span>
        <button className="tb-cog" onClick={() => onNav("settings")} aria-label="Settings">
          <Icon name="settings" size={28} stroke={2} />
        </button>
      </div>
    </header>
  );
};

// ────────────────────────────────────────────────────────────
// Left navigation rail
// ────────────────────────────────────────────────────────────
const Rail = ({ active, onNav, accent }) => {
  const items = [
    { id: "now", icon: "radio", label: "В ефир", en: "On Air" },
    { id: "channels", icon: "channels", label: "Станции", en: "Channels" },
    { id: "favorites", icon: "favorites", label: "Любими", en: "Favorites" },
    { id: "categories", icon: "categories", label: "Жанрове", en: "Genres" },
    { id: "search", icon: "search", label: "Търсене", en: "Search" },
    { id: "recent", icon: "recent", label: "Скоро", en: "Recent" },
  ];
  return (
    <nav className="rail">
      {items.map((it) => (
        <button
          key={it.id}
          className={`rail-item ${active === it.id ? "is-active" : ""}`}
          onClick={() => onNav(it.id)}
          style={active === it.id ? { color: accent } : {}}
        >
          <div className="rail-icon-wrap">
            <Icon name={it.icon} size={26} />
          </div>
          <div className="rail-label">{it.label}</div>
        </button>
      ))}
    </nav>
  );
};

// ────────────────────────────────────────────────────────────
// Now Playing screen
// ────────────────────────────────────────────────────────────
const NowPlaying = ({ station, track, playing, onPlayPause, onNext, onPrev, isFav, onToggleFav, accent, volume, setVolume }) => {
  return (
    <div className="now-ref">
      {/* center radial highlight */}
      <div
        className="now-spot"
        style={{ background: `radial-gradient(38% 100% at 50% 55%, ${accent}26, transparent 70%)` }}
      />
      {/* dotted reflective floor */}
      <div className="now-floor" />
      <div className="now-floor-fade" />

      <div className="now-stage">
        {/* Cover with thin frame */}
        <div className="ref-cover-wrap">
          <div className="ref-cover">
            <div className="ref-cover-inner">{station.logo(420)}</div>
            <div className="ref-cover-sheen" />
          </div>
          <div className="ref-cover-reflect" aria-hidden="true">
            <div className="ref-cover ref-mirror">
              <div className="ref-cover-inner">{station.logo(420)}</div>
            </div>
          </div>
        </div>

        {/* Meta column — matches reference */}
        <div className="ref-meta">
          <div className="ref-line">
            <Icon name="radio" size={32} stroke={1.8} />
            <span className="ref-name">{station.name}</span>
          </div>
          <div className="ref-line ref-line-sub">
            <Icon name="music" size={26} stroke={1.8} />
            <span className="ref-song">{track.title}</span>
          </div>
          <div className="ref-line ref-line-sub">
            <Icon name="user" size={24} stroke={1.8} />
            <span className="ref-artist">{track.artist}</span>
          </div>
        </div>

        {/* Frequency block — small, top right of the meta */}
        <div className="ref-freq">
          <div className="ref-freq-num">{station.freq.replace(" FM", "FM")}</div>
          <div className="ref-freq-city">{station.city === "София" ? "Bulgaria" : station.city}</div>
        </div>

        {/* Slim transport bar pinned bottom — surfaces controls without breaking the reference look */}
        <div className="ref-transport">
          <button className="rt-btn" onClick={onPrev} aria-label="Previous"><Icon name="prev" size={22} /></button>
          <button
            className="rt-play"
            onClick={onPlayPause}
            aria-label={playing ? "Pause" : "Play"}
            style={{ borderColor: accent, color: accent, boxShadow: `0 0 22px ${accent}55` }}
          >
            <Icon name={playing ? "pause" : "play"} size={22} stroke={2} />
          </button>
          <button className="rt-btn" onClick={onNext} aria-label="Next"><Icon name="next" size={22} /></button>
          <div className="rt-spacer" />
          <button
            className={`rt-btn ${isFav ? "is-fav" : ""}`}
            onClick={onToggleFav}
            style={isFav ? { color: "#ff5d7a" } : {}}
            aria-label="Favorite"
          >
            <Icon name={isFav ? "heart-filled" : "favorites"} size={20} />
          </button>
          <div className="rt-vol">
            <Icon name="volume" size={20} />
            <div className="rt-vol-track" onClick={(e) => {
              const r = e.currentTarget.getBoundingClientRect();
              setVolume(Math.round(((e.clientX - r.left) / r.width) * 100));
            }}>
              <div className="rt-vol-fill" style={{ width: `${volume}%`, background: accent }} />
            </div>
          </div>
          <div className="rt-eq">
            <Equalizer bars={5} height={22} color={accent} playing={playing} />
          </div>
        </div>
      </div>
    </div>
  );
};

window.NowPlaying = NowPlaying;
window.TopBar = TopBar;
window.Rail = Rail;
window.Equalizer = Equalizer;
window.Spectrum = Spectrum;
window.fmtTime = fmtTime;
window.stationById = stationById;
