package com.savyt711.client;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "helmetui")
public class ModConfig implements ConfigData {

    // HUD elements
    public boolean showO2 = true;
    public boolean showHunger = true;
    public boolean showSuitIntegrity = true;
    public boolean showCompass = true;

    // Leveling tool (diamond+ helmet only)
    public boolean showLevelingTool = false;
    public boolean showAltitude = false;

    public static ModConfig get() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static void register() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
    }
}