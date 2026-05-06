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
                renderHud(drawContext, player);
            }
        });
    }

    private static void renderHud(DrawContext context, PlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        int x = 10;
        int y = 10;
        int barWidth = 100;
        int barHeight = 6;

        // O2
        int o2Percent = (int) ((float) Math.max(player.getAir(), 0) / player.getMaxAir() * 100);
        context.drawText(client.textRenderer, "O2", x, y, 0x00FF00, true);
        drawBar(context, x + 20, y, barWidth, barHeight, o2Percent, 0x00FF00);

        // Hunger
        int hungerPercent = (int) (player.getHungerManager().getFoodLevel() / 20f * 100);
        context.drawText(client.textRenderer, "HGR", x, y + 14, 0xFFAA00, true);
        drawBar(context, x + 20, y + 14, barWidth, barHeight, hungerPercent, 0xFFAA00);

        // Suit integrity (all 4 armor slots combined)
        int totalDur = 0, totalMax = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack piece = player.getEquippedStack(slot);
            if (!piece.isEmpty() && piece.getItem() instanceof ArmorItem) {
                totalMax += piece.getMaxDamage();
                totalDur += piece.getMaxDamage() - piece.getDamage();
            }
        }
        int suitPercent = totalMax == 0 ? 100 : (int) ((float) totalDur / totalMax * 100);
        context.drawText(client.textRenderer, "SUT", x, y + 28, 0x4444FF, true);
        drawBar(context, x + 20, y + 28, barWidth, barHeight, suitPercent, 0x4444FF);
    }

    private static void drawBar(DrawContext context, int x, int y, int width, int height, int percent, int color) {
        // Background
        context.fill(x, y, x + width, y + height, 0xFF333333);
        // Fill
        int filled = (int) (width * (percent / 100f));
        context.fill(x, y, x + filled, y + height, 0xFF000000 | color);
    }
}