package com.savyt711.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {

    public static KeyBinding toggleO2;
    public static KeyBinding toggleHunger;
    public static KeyBinding toggleSuitIntegrity;
    public static KeyBinding toggleCompass;
    public static KeyBinding toggleLevelingTool;
    public static KeyBinding toggleAltitude;

    public static void register() {
        toggleO2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.helmetui.toggleO2", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN, "key.categories.helmetui"));

        toggleHunger = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.helmetui.toggleHunger", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN, "key.categories.helmetui"));

        toggleSuitIntegrity = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.helmetui.toggleSuitIntegrity", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN, "key.categories.helmetui"));

        toggleCompass = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.helmetui.toggleCompass", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN, "key.categories.helmetui"));

        toggleLevelingTool = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.helmetui.toggleLevelingTool", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN, "key.categories.helmetui"));

        toggleAltitude = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.helmetui.toggleAltitude", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN, "key.categories.helmetui"));

        // Listen for key presses each tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleO2.wasPressed())
                ModConfig.get().showO2 = !ModConfig.get().showO2;

            if (toggleHunger.wasPressed())
                ModConfig.get().showHunger = !ModConfig.get().showHunger;

            if (toggleSuitIntegrity.wasPressed())
                ModConfig.get().showSuitIntegrity = !ModConfig.get().showSuitIntegrity;

            if (toggleCompass.wasPressed())
                ModConfig.get().showCompass = !ModConfig.get().showCompass;

            if (toggleLevelingTool.wasPressed())
                ModConfig.get().showLevelingTool = !ModConfig.get().showLevelingTool;

            if (toggleAltitude.wasPressed())
                ModConfig.get().showAltitude = !ModConfig.get().showAltitude;
        });
    }
}