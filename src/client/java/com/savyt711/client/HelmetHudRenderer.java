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
        HudRenderCallback.EVENT.register((drawContext, tickDelta) ->{
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            PlayerEntity player = client.player;
            ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);

            boolean wearing = !helmet.isEmpty() && helmet.getItem() instanceof ArmorItem;
            HudState.helmetOn = wearing;
            if (wearing) {
                renderHud(drawContext, client, player);
            }
        });
    }

    private static void renderHud(DrawContext context, MinecraftClient client, PlayerEntity player) {
        ModConfig config = ModConfig.get();
        int screenW = client.getWindow().getScaledWidth();
        int screenH = client.getWindow().getScaledHeight();

        int barHeight = 80;
        int barWidth = 12;
        int bottomY = screenH / 2 + 40;
        int curveAmount = 6;

        // O2 — curve flips inward (right)
        if (config.showO2) {
            int o2Percent = (int) ((float) Math.max(player.getAir(), 0) / player.getMaxAir() * 100);
            drawCurvedBar(context, 30, bottomY, barWidth, barHeight, curveAmount, o2Percent, 0x5BB8F5, true);
            context.drawText(client.textRenderer, "O2",  28, bottomY - barHeight - 10, 0x5BB8F5, true);
        }

        // Hunger — curve flips inward (right)
        if (config.showHunger) {
            int hungerPercent = (int) (player.getHungerManager().getFoodLevel() / 20f * 100);
            drawCurvedBar(context, 48, bottomY, barWidth, barHeight, curveAmount, hungerPercent, 0xFF8C00, true);
            context.drawText(client.textRenderer, "HGR", 44, bottomY - barHeight - 10, 0xFF8C00, true);
        }

        // Suit integrity — curve faces inward (left)
        if (config.showSuitIntegrity) {
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
            context.drawText(client.textRenderer, "SUT", screenW - 48, bottomY - barHeight - 10, 0xFF3333, true);
        }

        // Compass bar
        if (config.showCompass) {
            drawCompass(context, client, player);
        }

        if (config.showLevelingTool || config.showAltitude) {
            drawAltitudeAndLevel(context, client, player);
        }
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


    private static void drawCompass(DrawContext context, MinecraftClient client, PlayerEntity player) {
        int screenW = client.getWindow().getScaledWidth();
        int centerX = screenW / 2;
        int topY = 8;
        int compassWidth = 300;
        int curveDepth = 6; // how much it curves down at edges

        // Player yaw: -180 to 180, convert to 0-360
        float yaw = player.getYaw() % 360;
        if (yaw < 0) yaw += 360;

        int tickRange = 120; // degrees visible on screen

        for (int i = -compassWidth / 2; i <= compassWidth / 2; i++) {
            float degree = yaw + (i / (float) compassWidth) * tickRange;
            if (degree < 0) degree += 360;
            if (degree >= 360) degree -= 360;

            float t = (float)(i + compassWidth / 2) / compassWidth;
            int curveY = (int)(curveDepth * Math.sin(Math.PI * t));
            int drawX = centerX + i;
            int drawY = topY + curveY;

            int degInt = Math.round(degree);

            // Major tick every 30 degrees
            if (degInt % 30 == 0) {
                context.fill(drawX, drawY, drawX + 1, drawY + 8, 0xFFFFFFFF);

                String label = " " + getCompassLabel(degInt) + " ";
                int charSpacing = 2; // extra pixels between each character
                int totalWidth = 0;
                for (char c : label.toCharArray()) {
                    totalWidth += client.textRenderer.getWidth(String.valueOf(c)) + charSpacing;
                }
                int labelX = drawX - totalWidth / 2;
                int labelY = drawY + 10;

                for (char c : label.toCharArray()) {
                    context.drawText(client.textRenderer, String.valueOf(c), labelX, labelY, 0xFFFFFFFF, true);
                    labelX += client.textRenderer.getWidth(String.valueOf(c)) + charSpacing;
                }
            }

            // Minor tick every 10 degrees
            else if (degInt % 10 == 0) {
                context.fill(drawX, drawY, drawX + 1, drawY + 4, 0xAAFFFFFF);
            }
        }

        // Center marker
        context.fill(centerX, topY - 2, centerX + 1, topY + 12, 0xFFFFFF00);
    }

    private static String getCompassLabel(int deg) {
        return switch (deg) {
            case 0, 360 -> "S";
            case 90 -> "W";
            case 180 -> "N";
            case 270 -> "E";
            default -> String.valueOf(deg);
        };
    }

    private static void drawAltitudeAndLevel(DrawContext context, MinecraftClient client, PlayerEntity player) {
        int screenW = client.getWindow().getScaledWidth();
        int screenH = client.getWindow().getScaledHeight();
        int centerX = screenW / 2;
        int centerY = screenH / 2;

        // Altitude — player Y position
        int altitude = (int) player.getY() - 63; // subtracts sea level so the altitude shows from above sea level.

        // Pitch — looking up/down (-90 to 90)
        float pitch = player.getPitch();

        // Roll is not native in MC so im using 0 for now (Create Aeronautics will provide this later)
        // float roll = 0;

        // Draw altitude on the right side
        if (ModConfig.get().showAltitude) {
            String altText = "ALT: " + altitude + "m";
            context.drawText(client.textRenderer, altText,
                    centerX + 20, centerY - 4, 0x00FF00, true);
        }

        // Draw leveling tool
        if (ModConfig.get().showLevelingTool) {
            // Horizon line — moves up/down based on pitch
            int horizonOffset = (int)(pitch * 1.5f);
            int lineWidth = 60;

            // Left horizon segment
            context.fill(centerX - lineWidth - 10, centerY + horizonOffset,
                    centerX - 15, centerY + horizonOffset + 1, 0xFF00FF00);

            // Right horizon segment
            context.fill(centerX + 15, centerY + horizonOffset,
                    centerX + lineWidth + 10, centerY + horizonOffset + 1, 0xFF00FF00);

            // Center crosshair
            context.fill(centerX - 5, centerY, centerX + 6, centerY + 1, 0xFF00FF00);
            context.fill(centerX, centerY - 5, centerX + 1, centerY + 6, 0xFF00FF00);

            // Pitch value
            String pitchText = "PITCH: " + (int)pitch + "°";
            context.drawText(client.textRenderer, pitchText,
                    centerX - client.textRenderer.getWidth(pitchText) / 2,
                    centerY + 15, 0x00FF00, true);
        }
    }
}

