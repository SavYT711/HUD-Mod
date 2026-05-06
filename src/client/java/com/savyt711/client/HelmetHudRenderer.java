package com.savyt711.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class HelmetHudRenderer {

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            PlayerEntity player = client.player;
            ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);

            if (!helmet.isEmpty() && helmet.getItem() instanceof ArmorItem) {
                renderHud(drawContext, player, helmet);
            }
        });
    }

    private static void renderHud(DrawContext context, PlayerEntity player, ItemStack helmet) {
        MinecraftClient client = MinecraftClient.getInstance();
        int x = 10;
        int y = 10;

        // O2 (air supply, 0-300 in vanilla)
        int currentAir = Math.max(player.getAir(), 0);
        int o2Percent = (int) ((float) currentAir / player.getMaxAir() * 100);

        // Hunger (0-20 in vanilla)
        int hunger = player.getHungerManager().getFoodLevel();

        // Helmet integrity as percentage
        int maxDur = helmet.getMaxDamage();
        int integrity = maxDur == 0 ? 100 : (int) ((float)(maxDur - helmet.getDamage()) / maxDur * 100);

        context.drawText(client.textRenderer, "O2: "        + o2Percent + "%",  x, y,      0x00FF00, true);
        context.drawText(client.textRenderer, "Hunger: "    + hunger,            x, y + 12, 0xFFAA00, true);
        context.drawText(client.textRenderer, "Integrity: " + integrity + "%",   x, y + 24, 0x4444FF, true);
    }
}