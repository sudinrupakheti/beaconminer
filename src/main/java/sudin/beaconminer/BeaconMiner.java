package sudin.beaconminer;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeaconMiner implements ModInitializer {
	public static final String MOD_ID = "beaconminer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		BeaconMinerEvents.register();
		BeaconMinerCommand.register();
		LOGGER.info("BeaconMiner initialized");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
