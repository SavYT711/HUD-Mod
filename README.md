# Helmet HUD (In Alpha Development Stage) 🛠️

A Fabric mod that adds an immersive, data-driven text overlay to the player's screen when wearing a helmet. Primarily intended for Aviation or Space modpacks. 

## 🚀 Current Features
The following modules have been implemented and render directly to the HUD:

*   **Helmet Detection:** The HUD automatically toggles visibility based on whether the player has headgear equipped.
*   **O2 Levels:** Real-time oxygen tracking for underwater or low-air situations.
*   **Hunger Monitoring:** Integrated nutritional data readout.
*   **Suit Integrity:** Tracks the durability of your equipped armor as a "suit health" percentage.

## 🛠 Technical Implementation
The mod uses `HudRenderCallback` to inject a high-tech readout. The data is scaled down for a "minimalist visor" aesthetic.

## 🚧 Upcoming Features
- [ ] **Low Integrity Alerts:** Visual warnings when suit durability is critical.
- [ ] **Compass/Coordinate Module:** Directional data for navigation.
- [ ] **Config System:** Customizable text colors (e.g., Amber, Cyan, or Red).

## 📥 Installation & Setup
1. Install [Fabric Loader](https://fabricmc.net).
2. Add **Fabric API** to your `mods` folder.
3. Add the **Helmet HUD** `.jar` to your `mods` folder.

## 📜 License
[MIT](https://opensource.org)
