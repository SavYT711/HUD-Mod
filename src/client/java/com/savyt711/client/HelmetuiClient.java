package com.savyt711.client;

import net.fabricmc.api.ClientModInitializer;

public class HelmetuiClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModConfig.register();
		HelmetHudRenderer.register();
	}
}