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
                renderHud(drawContext, client, player);
            }
        });
    }

    private static void renderHud(DrawContext context, MinecraftClient client, PlayerEntity player) {
        int screenW = client.getWindow().getScaledWidth();
        int screenH = client.getWindow().getScaledHeight();

        int barHeight = 80;
        int barWidth = 12;
        int bottomY = screenH / 2 + 40;
        int curveAmount = 6;

        // O2 — curve flips inward (right)
        int o2Percent = (int) ((float) Math.max(player.getAir(), 0) / player.getMaxAir() * 100);
        drawCurvedBar(context, 30, bottomY, barWidth, barHeight, curveAmount, o2Percent, 0x5BB8F5, true);

        // Hunger — curve flips inward (right)
        int hungerPercent = (int) (player.getHungerManager().getFoodLevel() / 20f * 100);
        drawCurvedBar(context, 48, bottomY, barWidth, barHeight, curveAmount, hungerPercent, 0xFF8C00, true);

        // Suit integrity — curve faces inward (left)
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
        drawCurvedBar(context, screenW - 46, bottomY, barWidth, barHeight, curveAmount, suitPercent, 0xFF3333, false);

        // Labels
        context.drawText(client.textRenderer, "O2",  28, bottomY - barHeight - 10, 0x5BB8F5, true);
        context.drawText(client.textRenderer, "HGR", 44, bottomY - barHeight - 10, 0xFF8C00, true);
        context.drawText(client.textRenderer, "SUT", screenW - 48, bottomY - barHeight - 10, 0xFF3333, true);
    }

    private static void drawCurvedBar(DrawContext context, int x, int bottomY, int width, int height, int curve, int percent, int color, boolean flipCurve) {
        int filled = (int)(height * (percent / 100f));
        int startY = bottomY - height;

        for (int slice = 0; slice < height; slice++) {
            int sliceY = startY + slice;
            float t = (float) slice / height;
            int offset = (int)(curve * Math.sin(Math.PI * t));
            if (flipCurve) offset = -offset;

            // Background
            context.fill(x + offset, sliceY, x + offset + width, sliceY + 1, 0x88222222);

            // Fill from bottom up
            if (slice >= height - filled) {
                context.fill(x + offset, sliceY, x + offset + width, sliceY + 1, 0xDD000000 | color);
            }

            // Side borders
            context.fill(x + offset - 1, sliceY, x + offset, sliceY + 1, 0xFF000000);
            context.fill(x + offset + width, sliceY, x + offset + width + 1, sliceY + 1, 0xFF000000);
        }

        // Top and bottom borders drawn separately outside the bar
        int topOffset = (int)(curve * Math.sin(0)) * (flipCurve ? -1 : 1);
        int botOffset = (int)(curve * Math.sin(Math.PI * ((height-1f)/height))) * (flipCurve ? -1 : 1);
        context.fill(x + topOffset - 1, startY - 1,     x + topOffset + width + 1, startY,          0xFF000000);
        context.fill(x + botOffset - 1, bottomY,         x + botOffset + width + 1, bottomY + 1,     0xFF000000);
    }
}