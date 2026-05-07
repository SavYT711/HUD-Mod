# Helmet HUD (In Alpha Development Stage) 🛠️

A Fabric mod that adds an immersive, data-driven text overlay to the player's screen when wearing a helmet. Primarily intended for Aviation or Space modpacks.

## 🚀 Current Features
The following modules have been implemented and render directly to the HUD:

*   **Helmet Detection:** The HUD automatically toggles visibility based on whether the player has headgear equipped.
*   **O2 Levels:** Real-time oxygen tracking for underwater or low-air situations.
*   **Hunger Monitoring:** Integrated nutritional data readout.
*   **Suit Integrity:** Tracks the durability of your equipped armor as a "suit health" percentage.
*   **Compass:** Displays the player's heading with degree markers.
*   All bars are curved and styled like a helmet visor

## 🛠 Technical Implementation
The mod uses `HudRenderCallback` to inject a high-tech readout. The data is scaled down for a "minimalist visor" aesthetic.

## Development Roadmap

| Version | Features |
|---------|----------|
| v0.1 ✅ | Basic HUD — O2, hunger, suit integrity, compass, FPS |
| v0.2 | Custom helmet item + visor overlay shapes |
| v0.3 | CO2 bar, EVA timer, battery bar |
| v0.4 | Low stat warning effects (vignette, flashing) |
| v0.5 | Night vision (green tint in low light) |
| v0.6 | Hide/show vanilla UI properly for all edge cases |
| v0.7 | Multiple helmet types with different HUD layouts |
| v0.8 | Polish — animations, smooth bar transitions |
| v0.9 | Bug fixes, performance, compatibility testing |
| **v1.0** | **Stable release — full base mod complete** |
| v2.0 | Temperature system + Galacticraft integration |
| v2.1 | Atmospheric pressure, separate Galacticraft O2 |
| v3.0 | Navigation map + waypoint system |
| v3.1 | Pathfinding — efficient route avoiding terrain |
| v4.0 | Multiplayer features (comms status, team waypoints) |


## 📥 Installation & Setup (Not setup yet)
1. Install [Fabric Loader](https://fabricmc.net).
2. Add **Fabric API** to your `mods` folder.
3. Add the **Helmet HUD** `.jar` to your `mods` folder.

## 📜 License
[MIT](https://opensource.org)
