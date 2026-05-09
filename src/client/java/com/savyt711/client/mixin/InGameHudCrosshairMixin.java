package com.savyt711.client.mixin;

import com.savyt711.client.HudState;
import com.savyt711.client.ModConfig;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudCrosshairMixin {

    @Inject(at = @At("HEAD"), method = "renderCrosshair", cancellable = true)
    private void hideCrosshair(CallbackInfo info) {
        if (HudState.helmetOn && ModConfig.get().showLevelingTool) {
            info.cancel();
        }
    }
}