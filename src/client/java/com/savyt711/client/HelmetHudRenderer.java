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

        // Calculate percents for warnings
        int o2Percent = (int) ((float) Math.max(player.getAir(), 0) / player.getMaxAir() * 100);
        int hungerPercent = (int) (player.getHungerManager().getFoodLevel() / 20f * 100);
        int totalDurW = 0, totalMaxW = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack piece = player.getEquippedStack(slot);
            if (!piece.isEmpty() && piece.getItem() instanceof ArmorItem) {
                totalMaxW += piece.getMaxDamage();
                totalDurW += piece.getMaxDamage() - piece.getDamage();
            }
        }
        int suitPercent = totalMaxW == 0 ? 100 : (int) ((float) totalDurW / totalMaxW * 100);
        WarningSounds.tick(o2Percent, hungerPercent, suitPercent);

        //Draws the visual effects for the status bars
        drawWarningEffects(context, client, o2Percent, hungerPercent, suitPercent);

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

    private static float prevX = 0, prevZ = 0, prevY = 0;
    private static float smoothSpeed = 0;

    private static void drawAltitudeAndLevel(DrawContext context, MinecraftClient client, PlayerEntity player) {
        int screenW = client.getWindow().getScaledWidth();
        int screenH = client.getWindow().getScaledHeight();
        int centerX = screenW / 2;
        int centerY = screenH / 2;

        float pitch = player.getPitch();
        int altitude = (int) player.getY() - 64;

        // Calculate speed in blocks per second (20 ticks per second)
        float dx = (float)(player.getX() - prevX);
        float dy = (float)(player.getY() - prevY);
        float dz = (float)(player.getZ() - prevZ);
        float rawSpeed = (float) Math.sqrt(dx*dx + dy*dy + dz*dz) * 20;

        smoothSpeed = smoothSpeed * 0.8f + rawSpeed * 0.2f; // smooth it out

        prevX = (float) player.getX();
        prevY = (float) player.getY();
        prevZ = (float) player.getZ();
        int speed = (int) smoothSpeed;

        int barH = 120;
        int barW = 14;
        int barTop = centerY - barH / 2;
        int tickRange = 10; // numbers visible above and below center

        // HORIZON LINE
        if (ModConfig.get().showLevelingTool) {
            int horizonOffset = (int)(pitch * 1.5f);
            int dashWidth = 18;
            int gapWidth = 6;
            int totalWidth = 120;
            int startX = centerX - totalWidth / 2;

            // Dashed horizon lock (0 degrees - always at centerY)
            int halfW = 120;

            //left dash
            int x = startX;
            while (x < centerX - 20) {
                int end = Math.min(x + 18, centerX - 20);
                context.fill(x, centerY, end, centerY + 1, 0xFF00FF00);
                x += 24;
            }

            //right dash
            x = centerX + 20;
            while (x < centerX + halfW) {
                int end = Math.min(x + 18, centerX + halfW);
                context.fill(x, centerY, end, centerY + 1, 0xFF00FF00);
                x += 24;
            }

            // Solid pitch line (moves with pitch)
            context.fill(centerX - totalWidth/2, centerY + horizonOffset,
                    centerX - 20, centerY + horizonOffset + 1, 0xFF00FF00);
            context.fill(centerX + 20, centerY + horizonOffset,
                    centerX + totalWidth/2, centerY + horizonOffset + 1, 0xFF00FF00);

            // Fixed center aircraft symbol
            context.fill(centerX - 12, centerY, centerX - 4, centerY + 1, 0xFF00FF00);
            context.fill(centerX + 4,  centerY, centerX + 12, centerY + 1, 0xFF00FF00);
            context.fill(centerX - 1,  centerY - 4, centerX + 1, centerY + 4, 0xFF00FF00);

            // Pitch value
            String pitchText = (int)pitch + "°";
            context.drawText(client.textRenderer, pitchText,
                    centerX - client.textRenderer.getWidth(pitchText) / 2,
                    centerY + 10, 0xFF00FF00, true);
        }

        // ALTITUDE METER (right side)
        if (ModConfig.get().showAltitude) {
            int altX = centerX + 80;

            // Scrolling numbers
            for (int i = -tickRange; i <= tickRange; i++) {
                int tickAlt = altitude + i;
                float t = (float) i / tickRange;
                int tickY = centerY - (int)(t * (barH / 2));

                if (tickY < barTop || tickY > barTop + barH) continue;

                if (tickAlt % 5 == 0) {
                    context.fill(altX, tickY, altX + 6, tickY + 1, 0xFF00FF00);
                    String label = String.valueOf(tickAlt);
                    context.drawText(client.textRenderer, label,
                            altX + barW + 3, tickY - 3, 0xFF00FF00, true);
                } else {
                    context.fill(altX, tickY, altX + 3, tickY + 1, 0xFF00FF00);
                }
            }

            // Center marker
            context.fill(altX - 3, centerY - 1, altX, centerY + 2, 0xFF00FF00);
            context.fill(altX + barW, centerY - 1, altX + barW + 3, centerY + 2, 0xFF00FF00);

            // Current altitude box
            int altLabelW = client.textRenderer.getWidth(String.valueOf(altitude)) + 6;
            context.fill(altX - 1, centerY - 6, altX + altLabelW, centerY + 7, 0xFF000000);
            context.fill(altX - 1, centerY - 6, altX + altLabelW, centerY + 7, 0x8800FF00);
            String altText = String.valueOf(altitude);
            context.drawText(client.textRenderer, altText,
                    altX + barW / 2 - client.textRenderer.getWidth(altText) / 2,
                    centerY - 3, 0xFF00FF00, true);
        }

        //  SPEED METER (left side)
        if (ModConfig.get().showLevelingTool) {
            int spdX = centerX - 80 - barW;

            // Scrolling numbers
            for (int i = -tickRange; i <= tickRange; i++) {
                //int tickSpd = Math.max(speed + i, 0);
                int tickSpd = speed + i;
                if (tickSpd < 0) continue;
                float t = (float) i / tickRange;
                int tickY = centerY - (int)(t * (barH / 2));

                if (tickY < barTop || tickY > barTop + barH) continue;

                if (tickSpd % 5 == 0) {
                    context.fill(spdX + barW - 6, tickY, spdX + barW, tickY + 1, 0xFF00FF00);
                    String label = String.valueOf(tickSpd);
                    context.drawText(client.textRenderer, label,
                            spdX - client.textRenderer.getWidth(label) - 3,
                            tickY - 3, 0xFF00FF00, true);
                } else {
                    context.fill(spdX + barW - 3, tickY, spdX + barW, tickY + 1, 0xFF00FF00);
                }
            }

            // Center marker
            context.fill(spdX - 3, centerY - 1, spdX, centerY + 2, 0xFF00FF00);
            context.fill(spdX + barW, centerY - 1, spdX + barW + 3, centerY + 2, 0xFF00FF00);

            // Current speed box
            context.fill(spdX - 1, centerY - 6, spdX + barW + 1, centerY + 7, 0xFF000000);
            context.fill(spdX - 1, centerY - 6, spdX + barW + 1, centerY + 7, 0x8800FF00);
            String spdText = String.valueOf(speed);
            context.drawText(client.textRenderer, spdText,
                    spdX + barW / 2 - client.textRenderer.getWidth(spdText) / 2,
                    centerY - 3, 0xFF00FF00, true);
        }
    }

    private static float pulseTimer = 0;

    private static void drawSoftEdgeWarning(DrawContext context, int screenW, int screenH, int edgeSize, int color) {
        int baseAlpha = (color >>> 24) & 0xFF;
        int rgb = color & 0x00FFFFFF;

        for (int i = 0; i < edgeSize; i++) {
            float fade = 1f - (i / (float) edgeSize);
            int alpha = (int)(baseAlpha * fade * fade);
            int fadedColor = (alpha << 24) | rgb;

            // Top
            context.fill(0, i, screenW, i + 1, fadedColor);

            // Bottom
            context.fill(0, screenH - i - 1, screenW, screenH - i, fadedColor);

            // Left, avoiding corners
            // Left
            context.fill(i, 0, i + 1, screenH, fadedColor);

            // Right
            context.fill(screenW - i - 1, 0, screenW - i, screenH, fadedColor);
        }
    }

    private static void drawWarningEffects(DrawContext context, MinecraftClient client, int o2Percent, int hungerPercent, int suitPercent) {
        int screenW = client.getWindow().getScaledWidth();
        int screenH = client.getWindow().getScaledHeight();
        int edgeSize = 20;

        pulseTimer += 0.05f;

        // O2 pulse — speed increases as O2 drops
        if (o2Percent <= 25) {
            float speed = o2Percent <= 10 ? 0.15f : o2Percent <= 15 ? 0.08f : 0.04f;
            pulseTimer += speed;
            float pulse = (float)(Math.sin(pulseTimer * Math.PI) + 1) / 2f;
            int alpha = (int)(pulse * 180);
            int color = (alpha << 24) | 0x005BB8F5;

            drawSoftEdgeWarning(context, screenW, screenH, edgeSize, color);
        }

        // Hunger — static dim orange tint on edges
        if (hungerPercent <= 25) {
            int color = 0x55FF8C00;
            drawSoftEdgeWarning(context, screenW, screenH, edgeSize, color);
        }

        // Suit — static dim red tint on edges
        if (suitPercent <= 25) {
            int color = 0x33FF3333;
            drawSoftEdgeWarning(context, screenW, screenH, edgeSize, color);
        }
    }

}

