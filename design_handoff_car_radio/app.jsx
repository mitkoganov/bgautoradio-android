// App entry — router, state, mini player, tweaks

const { useState: useS, useEffect: useE, useRef: useR } = React;

const TWEAK_DEFAULTS = /*EDITMODE-BEGIN*/{
  "stationId": "bg-radio",
  "accent": "#4DD3FF"
}/*EDITMODE-END*/;

const ACCENT_OPTIONS = ["#4DD3FF", "#7EE8C7", "#FFB454", "#FF6188", "#A78BFA"];

const MiniPlayer = ({ station, track, playing, onPlayPause, onNext, onPrev, onOpen, accent }) => {
  if (!station) return null;
  return (
    <div className="mini-player" onClick={onOpen}>
      <div className="mp-cover">{station.logo(64)}</div>
      <div className="mp-meta">
        <div className="mp-station">{station.name} <span className="mp-freq">· {station.freq}</span></div>
        <div className="mp-track">{track.title} — {track.artist}</div>
      </div>
      <div className="mp-eq"><Equalizer bars={5} height={20} color={accent} playing={playing} /></div>
      <div className="mp-controls" onClick={(e) => e.stopPropagation()}>
        <button className="mp-btn" onClick={onPrev}><Icon name="prev" size={20} /></button>
        <button className="mp-btn mp-play" onClick={onPlayPause} style={{ background: accent }}>
          <Icon name={playing ? "pause" : "play"} size={20} stroke={2} />
        </button>
        <button className="mp-btn" onClick={onNext}><Icon name="next" size={20} /></button>
      </div>
    </div>
  );
};

const App = () => {
  const [tweaks, setTweak] = useTweaks(TWEAK_DEFAULTS);
  const [screen, setScreen] = useS("now");
  const [playing, setPlaying] = useS(true);
  const [favs, setFavs] = useS(FAVORITE_IDS);
  const [clock, setClock] = useS("14:23");
  const [volume, setVolume] = useS(45);

  useE(() => {
    const tick = () => {
      const d = new Date();
      setClock(`${d.getHours().toString().padStart(2, "0")}:${d.getMinutes().toString().padStart(2, "0")}`);
    };
    tick();
    const t = setInterval(tick, 30000);
    return () => clearInterval(t);
  }, []);

  const station = stationById(tweaks.stationId) || STATIONS[0];
  const track = NOW_PLAYING[station.id] || { title: "—", artist: "—", album: "—", elapsed: 0, duration: 1 };
  const accent = tweaks.accent || "#4DD3FF";

  const selectStation = (id) => {
    setTweak("stationId", id);
    setPlaying(true);
    setScreen("now");
  };
  const nextStation = () => {
    const idx = STATIONS.findIndex((s) => s.id === station.id);
    setTweak("stationId", STATIONS[(idx + 1) % STATIONS.length].id);
  };
  const prevStation = () => {
    const idx = STATIONS.findIndex((s) => s.id === station.id);
    setTweak("stationId", STATIONS[(idx - 1 + STATIONS.length) % STATIONS.length].id);
  };
  const toggleFav = (id) => {
    setFavs((f) => f.includes(id) ? f.filter((x) => x !== id) : [...f, id]);
  };
  const togglePlay = () => setPlaying((p) => !p);

  const showRail = screen !== "now";
  const showMini = screen !== "now";

  return (
    <div className="hu-root" data-screen-label={`HU 1920x720 — ${screen}`}>
      {/* Background */}
      <div className="bg-base" />
      <div className="bg-grain" />
      <div
        className="bg-glow"
        style={{ background: `radial-gradient(40% 60% at 50% 110%, ${accent}24, transparent 60%)` }}
      />

      <TopBar screen={screen} onNav={setScreen} clock={clock} accent={accent} />

      <div className={`body ${showRail ? "has-rail" : ""}`}>
        {showRail && <Rail active={screen} onNav={setScreen} accent={accent} />}
        <main className="main">
          {screen === "now" && (
            <NowPlaying
              station={station}
              track={track}
              playing={playing}
              onPlayPause={togglePlay}
              onNext={nextStation}
              onPrev={prevStation}
              isFav={favs.includes(station.id)}
              onToggleFav={() => toggleFav(station.id)}
              accent={accent}
              volume={volume}
              setVolume={setVolume}
            />
          )}
          {screen === "channels" && (
            <ChannelsScreen activeId={station.id} playing={playing} onSelect={selectStation}
              favs={favs} toggleFav={toggleFav} accent={accent} />
          )}
          {screen === "favorites" && (
            <FavoritesScreen activeId={station.id} playing={playing} onSelect={selectStation}
              favs={favs} toggleFav={toggleFav} accent={accent} />
          )}
          {screen === "categories" && (
            <CategoriesScreen activeId={station.id} playing={playing} onSelect={selectStation}
              favs={favs} toggleFav={toggleFav} accent={accent} />
          )}
          {screen === "search" && (
            <SearchScreen activeId={station.id} playing={playing} onSelect={selectStation}
              favs={favs} toggleFav={toggleFav} accent={accent} />
          )}
          {screen === "recent" && (
            <RecentScreen activeId={station.id} playing={playing} onSelect={selectStation} accent={accent} />
          )}
          {screen === "settings" && <SettingsScreen accent={accent} />}
        </main>
      </div>

      {showMini && (
        <MiniPlayer
          station={station} track={track} playing={playing}
          onPlayPause={togglePlay} onNext={nextStation} onPrev={prevStation}
          onOpen={() => setScreen("now")} accent={accent}
        />
      )}

      <TweaksPanel title="Tweaks">
        <TweakSection title="Радио станция">
          <div className="tweak-stations">
            {STATIONS.map((s) => (
              <button
                key={s.id}
                className={`tw-stat ${tweaks.stationId === s.id ? "is-on" : ""}`}
                onClick={() => setTweak("stationId", s.id)}
                style={tweaks.stationId === s.id ? { borderColor: accent, color: accent } : {}}
              >
                <span className="tw-stat-mark" style={{ background: s.bg[0] }} />
                <span>{s.name}</span>
                <span className="tw-stat-freq">{s.freq}</span>
              </button>
            ))}
          </div>
        </TweakSection>
        <TweakSection title="Акцентен цвят">
          <TweakColor
            tweaks={tweaks} setTweak={setTweak} k="accent"
            options={ACCENT_OPTIONS}
          />
        </TweakSection>
      </TweaksPanel>
    </div>
  );
};

// Scale 1920x720 stage to fit viewport
const Stage = () => {
  const ref = useR(null);
  useE(() => {
    const fit = () => {
      if (!ref.current) return;
      const W = 1920, H = 720;
      const scale = Math.min(window.innerWidth / W, window.innerHeight / H);
      ref.current.style.transform = `translate(-50%, -50%) scale(${scale})`;
    };
    fit();
    window.addEventListener("resize", fit);
    return () => window.removeEventListener("resize", fit);
  }, []);
  return (
    <div className="stage-host">
      <div ref={ref} className="stage">
        <App />
      </div>
    </div>
  );
};

ReactDOM.createRoot(document.getElementById("root")).render(<Stage />);
