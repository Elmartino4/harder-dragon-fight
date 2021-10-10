package github.elmartino4.dragonfight;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DragonFight implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("Dragon Fight");
	public static final TrackedData<Boolean> LOW_HEALTH = DataTracker.registerData(EnderDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Loaded Dragon Fight Mod");
	}
}
