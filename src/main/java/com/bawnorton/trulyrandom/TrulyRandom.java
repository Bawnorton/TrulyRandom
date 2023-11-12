package com.bawnorton.trulyrandom;

import com.bawnorton.trulyrandom.network.Networking;
import com.bawnorton.trulyrandom.random.Randomiser;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrulyRandom implements ModInitializer {
	private static final Randomiser randomiser = new Randomiser();
	public static final String MOD_ID = "trulyrandom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Networking.init();
		LOGGER.debug("TrulyRandom Initialised");
	}

	public static Randomiser getRandomiser() {
		return randomiser;
	}
}