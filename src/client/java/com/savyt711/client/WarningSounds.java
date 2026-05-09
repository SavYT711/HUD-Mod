package com.savyt711.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

public class WarningSounds {

    private static int tickCounter = 0;

    public static void tick(int o2Percent, int hungerPercent, int suitPercent) {
        tickCounter++;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // O2 critical — rapid beep every 20 ticks (1 second)
        if (o2Percent <= 10) {
            if (tickCounter % 20 == 0) {
                client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), 1.0f, 1.5f);
            }
        }
        // O2 warning — slow beep every 40 ticks (2 seconds)
        else if (o2Percent <= 15) {
            if (tickCounter % 40 == 0) {
                client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), 0.8f, 1.5f);
            }
        }
        // O2 caution — single beep every 100 ticks (5 seconds)
        else if (o2Percent <= 25) {
            if (tickCounter % 100 == 0) {
                client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), 0.6f, 1.5f);
            }
        }

        // Hunger warning — single tone once when crossing 25%
        if (hungerPercent <= 25 && tickCounter % 200 == 0) {
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), 0.6f, 1.0f);
        }

        // Suit warning — single tone once when crossing 25%
        if (suitPercent <= 25 && tickCounter % 200 == 0) {
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), 0.6f, 0.2f);
        }
    }
}