package vazkii.zeta;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.Logger;
import vazkii.zeta.client.ClientTicker;
import vazkii.zeta.config.IZetaConfigInternals;
import vazkii.zeta.config.SectionDefinition;
import vazkii.zeta.config.ConfigManager;
import vazkii.zeta.event.bus.IZetaLoadEvent;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.ZetaEventBus;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ModuleFinder;
import vazkii.zeta.module.ZetaCategory;
import vazkii.zeta.module.ZetaModuleManager;
import vazkii.zeta.network.ZetaNetworkHandler;
import vazkii.zeta.registry.DyeablesRegistry;
import vazkii.zeta.registry.ZetaRegistry;
import vazkii.zeta.util.ZetaSide;

/**
 * do not touch forge OR quark from this package, it will later be split off
 */
public abstract class Zeta {
	public Zeta(String modid, Logger log) {
		this.log = log;

		this.modid = modid;
		this.side = getSide();
		this.loadBus = new ZetaEventBus<>(LoadEvent.class, IZetaLoadEvent.class, log);
		this.playBus = new ZetaEventBus<>(PlayEvent.class, IZetaPlayEvent.class, null);
		this.modules = new ZetaModuleManager(this);
		this.registry = createRegistry(modid);
		this.dyeables = new DyeablesRegistry(this);

		this.ticker_SHOULD_NOT_BE_HERE = new ClientTicker();
	}

	public final Logger log;

	public final String modid;
	public final ZetaSide side;
	public final ZetaEventBus<IZetaLoadEvent> loadBus;
	public final ZetaEventBus<IZetaPlayEvent> playBus;
	public final ZetaModuleManager modules;

	public final ZetaRegistry registry;
	public final DyeablesRegistry dyeables; //TODO: move into ZetaRegistry?

	public ConfigManager configManager; //This could do with being split up into various pieces
	public IZetaConfigInternals configInternals;

	//TODO: move to ZetaClient. Some bits of the server *do* actually need this for some raisin (config code)
	@Deprecated public final ClientTicker ticker_SHOULD_NOT_BE_HERE;

	public void loadModules(Iterable<ZetaCategory> categories, ModuleFinder finder, Object rootPojo) {
		modules.initCategories(categories);
		modules.load(finder);

		//The reason why there's a circular dependency between configManager and configInternals:
		// - ConfigManager determines the shape and layout of the config file
		// - The platform-specific configInternals loads the actual values, from the platform-specfic config file
		// - Only then can ConfigManager do the initial config load

		this.configManager = new ConfigManager(this, rootPojo);
		this.configInternals = makeConfigInternals(configManager.getRootConfig());
		this.configManager.onReload();
	}

	public abstract ZetaSide getSide();
	public abstract boolean isModLoaded(String modid);

	public abstract IZetaConfigInternals makeConfigInternals(SectionDefinition rootSection);

	public abstract ZetaRegistry createRegistry(String modid);
	public abstract ZetaNetworkHandler createNetworkHandler(String modid, int protocolVersion);

	//time for JANK - "fire this on the forge event bus and tell me whether it was cancelled"
	public abstract boolean fireRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult bhr);

	public abstract void start();
}
