package com.savyt711;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helmetui implements ModInitializer {
	public static final String MOD_ID = "helmetui";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Helmet HUD initializing!");
	}
}