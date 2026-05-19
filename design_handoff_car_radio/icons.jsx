// Icon set — clean line icons sized for automotive head units
// Stroke 1.6, 24x24 viewBox, currentColor

const Icon = ({ name, size = 24, stroke = 1.6, style }) => {
  const props = {
    width: size, height: size, viewBox: "0 0 24 24",
    fill: "none", stroke: "currentColor", strokeWidth: stroke,
    strokeLinecap: "round", strokeLinejoin: "round", style,
  };
  switch (name) {
    case "home": return (<svg {...props}><path d="M3 11l9-8 9 8" /><path d="M5 9.5V20a1 1 0 0 0 1 1h4v-6h4v6h4a1 1 0 0 0 1-1V9.5" /></svg>);
    case "channels": return (<svg {...props}><path d="M4 6h16M4 12h16M4 18h10" /><circle cx="19" cy="18" r="2" /></svg>);
    case "channels-plus": return (<svg {...props}><path d="M3 6h18M3 12h13M3 18h10" /><path d="M19 15v6M16 18h6" /></svg>);
    case "favorites": return (<svg {...props}><path d="M12 21s-7-4.5-9.5-9A5.5 5.5 0 0 1 12 6a5.5 5.5 0 0 1 9.5 6c-2.5 4.5-9.5 9-9.5 9z" /></svg>);
    case "categories": return (<svg {...props}><rect x="3" y="3" width="7" height="7" rx="1.2" /><rect x="14" y="3" width="7" height="7" rx="1.2" /><rect x="3" y="14" width="7" height="7" rx="1.2" /><rect x="14" y="14" width="7" height="7" rx="1.2" /></svg>);
    case "search": return (<svg {...props}><circle cx="11" cy="11" r="7" /><path d="m20 20-3.5-3.5" /></svg>);
    case "recent": return (<svg {...props}><circle cx="12" cy="12" r="9" /><path d="M12 7v5l3 2" /></svg>);
    case "settings": return (<svg {...props}><circle cx="12" cy="12" r="3" /><path d="M19.4 15a1.7 1.7 0 0 0 .3 1.8l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.8-.3 1.7 1.7 0 0 0-1 1.5V21a2 2 0 0 1-4 0v-.1a1.7 1.7 0 0 0-1-1.5 1.7 1.7 0 0 0-1.8.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.8 1.7 1.7 0 0 0-1.5-1H3a2 2 0 0 1 0-4h.1a1.7 1.7 0 0 0 1.5-1 1.7 1.7 0 0 0-.3-1.8l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.8.3h0a1.7 1.7 0 0 0 1-1.5V3a2 2 0 0 1 4 0v.1a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.8-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.8v0a1.7 1.7 0 0 0 1.5 1H21a2 2 0 0 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1z" /></svg>);
    case "play": return (<svg {...props}><path d="M7 4v16l13-8Z" fill="currentColor" stroke="none" /></svg>);
    case "pause": return (<svg {...props}><rect x="6" y="4" width="4" height="16" rx="1" fill="currentColor" stroke="none" /><rect x="14" y="4" width="4" height="16" rx="1" fill="currentColor" stroke="none" /></svg>);
    case "prev": return (<svg {...props}><path d="M19 5 9 12l10 7Z" fill="currentColor" stroke="none" /><rect x="5" y="5" width="2" height="14" rx="1" fill="currentColor" stroke="none" /></svg>);
    case "next": return (<svg {...props}><path d="M5 5l10 7L5 19Z" fill="currentColor" stroke="none" /><rect x="17" y="5" width="2" height="14" rx="1" fill="currentColor" stroke="none" /></svg>);
    case "volume": return (<svg {...props}><path d="M4 9v6h4l5 4V5L8 9H4Z" /><path d="M16 8a5 5 0 0 1 0 8" /><path d="M19 5a9 9 0 0 1 0 14" /></svg>);
    case "radio": return (<svg {...props}><rect x="3" y="9" width="18" height="11" rx="1.5" /><path d="M7 9 17 4" /><circle cx="8.5" cy="14.5" r="2" /><path d="M13 13h5M13 16h5" /></svg>);
    case "heart-filled": return (<svg {...props}><path d="M12 21s-7-4.5-9.5-9A5.5 5.5 0 0 1 12 6a5.5 5.5 0 0 1 9.5 6c-2.5 4.5-9.5 9-9.5 9z" fill="currentColor" /></svg>);
    case "music": return (<svg {...props}><path d="M9 18V5l10-2v13" /><circle cx="6" cy="18" r="3" /><circle cx="16" cy="16" r="3" /></svg>);
    case "user": return (<svg {...props}><circle cx="12" cy="8" r="4" /><path d="M4 21a8 8 0 0 1 16 0" /></svg>);
    case "wifi": return (<svg {...props}><path d="M5 12a10 10 0 0 1 14 0" /><path d="M8 15a6 6 0 0 1 8 0" /><circle cx="12" cy="18" r="1" fill="currentColor" /></svg>);
    case "signal": return (<svg {...props}><rect x="3" y="14" width="3" height="6" rx="0.5" fill="currentColor" stroke="none" /><rect x="9" y="10" width="3" height="10" rx="0.5" fill="currentColor" stroke="none" /><rect x="15" y="6" width="3" height="14" rx="0.5" fill="currentColor" stroke="none" /><rect x="20" y="2" width="2" height="18" rx="0.5" fill="currentColor" stroke="none" opacity="0.35" /></svg>);
    case "bluetooth": return (<svg {...props}><path d="M7 7l10 10-5 4V3l5 4L7 17" /></svg>);
    case "cast": return (<svg {...props}><path d="M3 18a3 3 0 0 1 3 3" /><path d="M3 14a7 7 0 0 1 7 7" /><path d="M3 10a11 11 0 0 1 11 11" /><rect x="3" y="3" width="18" height="14" rx="2" opacity="0.4" /></svg>);
    case "back": return (<svg {...props}><path d="M15 5l-7 7 7 7" /></svg>);
    case "chevron-right": return (<svg {...props}><path d="M9 5l7 7-7 7" /></svg>);
    case "plus": return (<svg {...props}><path d="M12 5v14M5 12h14" /></svg>);
    case "star": return (<svg {...props}><path d="m12 3 2.6 5.4 5.9.8-4.3 4.1 1 5.9L12 16.4 6.8 19.2l1-5.9L3.5 9.2l5.9-.8L12 3Z" /></svg>);
    case "grid": return (<svg {...props}><rect x="3" y="3" width="7" height="7" rx="1.2" /><rect x="14" y="3" width="7" height="7" rx="1.2" /><rect x="3" y="14" width="7" height="7" rx="1.2" /><rect x="14" y="14" width="7" height="7" rx="1.2" /></svg>);
    case "guitar": return (<svg {...props}><path d="M14 6l4-4 2 2-4 4" /><path d="M12 8l4 4" /><path d="M9 11a5 5 0 1 0 4 4l3-3-4-4-3 3z" /></svg>);
    case "wave": return (<svg {...props}><path d="M2 12c2 0 2-6 4-6s2 12 4 12 2-12 4-12 2 12 4 12 2-6 4-6" /></svg>);
    case "feather": return (<svg {...props}><path d="M20 4c-6 0-12 4-12 12v4h4c8 0 12-6 12-12L20 4Z" /><path d="M16 8 4 20" /></svg>);
    case "mic": return (<svg {...props}><rect x="9" y="3" width="6" height="12" rx="3" /><path d="M5 11a7 7 0 0 0 14 0" /><path d="M12 18v3" /></svg>);
    case "treble": return (<svg {...props}><path d="M12 3c-3 2-3 6 0 8s3 6 0 8a3 3 0 1 1-3-3" /></svg>);
    case "x": return (<svg {...props}><path d="m6 6 12 12M18 6 6 18" /></svg>);
    case "equalizer": return (<svg {...props}><rect x="4" y="10" width="2.5" height="10" rx="0.5" fill="currentColor" stroke="none" /><rect x="9" y="6" width="2.5" height="14" rx="0.5" fill="currentColor" stroke="none" /><rect x="14" y="12" width="2.5" height="8" rx="0.5" fill="currentColor" stroke="none" /><rect x="19" y="8" width="2.5" height="12" rx="0.5" fill="currentColor" stroke="none" /></svg>);
    case "shuffle": return (<svg {...props}><path d="M16 3h5v5" /><path d="m3 21 18-18" /><path d="M21 16v5h-5" /><path d="m15 15 6 6" /><path d="M3 3l6 6" /></svg>);
    case "sleep": return (<svg {...props}><path d="M20 14a8 8 0 1 1-9-11 6 6 0 0 0 9 11Z" /></svg>);
    case "globe": return (<svg {...props}><circle cx="12" cy="12" r="9" /><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18" /></svg>);
    default: return null;
  }
};

window.Icon = Icon;
