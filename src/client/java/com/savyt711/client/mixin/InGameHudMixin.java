package com.savyt711.client.mixin;

import com.savyt711.client.HudState;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(at = @At("HEAD"), method = "renderStatusBars", cancellable = true)
    private void hideStatusBars(CallbackInfo info) {
        if (HudState.helmetOn) {
            info.cancel();
        }
    }
}