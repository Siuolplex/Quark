package org.violetmoon.quark.base.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

import java.time.Instant;
import java.util.BitSet;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.network.message.*;
import org.violetmoon.quark.base.network.message.experimental.PlaceVariantUpdateMessage;
import org.violetmoon.quark.base.network.message.oddities.HandleBackpackMessage;
import org.violetmoon.quark.base.network.message.oddities.MatrixEnchanterOperationMessage;
import org.violetmoon.quark.base.network.message.oddities.ScrollCrateMessage;
import org.violetmoon.quark.base.network.message.structural.*;
import org.violetmoon.quark.content.tweaks.module.LockRotationModule;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.network.IZetaMessage;
import org.violetmoon.zeta.network.ZetaNetworkDirection;
import org.violetmoon.zeta.network.ZetaNetworkHandler;

public final class QuarkNetwork {

	private static final int PROTOCOL_VERSION = 3;

	private static ZetaNetworkHandler network;

	@LoadEvent
	public static void setup(ZCommonSetup event) {
		network = Quark.ZETA.createNetworkHandler(PROTOCOL_VERSION);

		network.getSerializer().mapHandlers(Instant.class, (buf, field) -> buf.readInstant(), (buf, field, instant) -> buf.writeInstant(instant));
		network.getSerializer().mapHandlers(MessageSignature.class, (buf, field) -> new MessageSignature(buf), (buf, field, signature) -> signature.write(buf));
		network.getSerializer().mapHandlers(LastSeenMessages.Update.class, (buf, field) -> new LastSeenMessages.Update(buf), (buf, field, update) -> update.write(buf));
		network.getSerializer().mapHandlers(BitSet.class, (buf, field) -> BitSet.valueOf(buf.readLongArray()), (buf, field, bitSet) -> buf.writeLongArray(bitSet.toLongArray()));

		// Base Quark
		network.register(SortInventoryMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(InventoryTransferMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(DoubleDoorMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(HarvestMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(RequestEmoteMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(ChangeHotbarMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(SetLockProfileMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(ShareItemMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(ScrollOnBundleMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.getSerializer().mapHandlers(LockRotationModule.LockProfile.class, LockRotationModule.LockProfile::readProfile, LockRotationModule.LockProfile::writeProfile);

		// Oddities
		network.register(HandleBackpackMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(MatrixEnchanterOperationMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);
		network.register(ScrollCrateMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);

		// Experimental
		network.register(PlaceVariantUpdateMessage.class, ZetaNetworkDirection.PLAY_TO_SERVER);

		// Clientbound
		network.register(DoEmoteMessage.class, ZetaNetworkDirection.PLAY_TO_CLIENT);
		network.register(UpdateTridentMessage.class, ZetaNetworkDirection.PLAY_TO_CLIENT);

		// Flag Syncing
		network.register(S2CUpdateFlag.class, ZetaNetworkDirection.PLAY_TO_CLIENT);
		network.register(C2SUpdateFlag.class, ZetaNetworkDirection.PLAY_TO_SERVER);

		// Login
		network.registerLogin(S2CLoginFlag.class, ZetaNetworkDirection.LOGIN_TO_CLIENT, 98, true, S2CLoginFlag::generateRegistryPackets);
		network.registerLogin(C2SLoginFlag.class, ZetaNetworkDirection.LOGIN_TO_SERVER, 99, false, null);
	}

	public static void sendToPlayer(IZetaMessage msg, ServerPlayer player) {
		if(network == null)
			return;

		network.sendToPlayer(msg, player);
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendToServer(IZetaMessage msg) {
		if(network == null || Minecraft.getInstance().getConnection() == null)
			return;

		network.sendToServer(msg);
	}

	public static void sendToPlayers(IZetaMessage msg, Iterable<ServerPlayer> players) {
		if(network == null)
			return;

		network.sendToPlayers(msg, players);
	}

	public static void sendToAllPlayers(IZetaMessage msg, MinecraftServer server) {
		if(network == null)
			return;

		network.sendToAllPlayers(msg, server);
	}

	public static Packet<?> toVanillaPacket(IZetaMessage msg, ZetaNetworkDirection dir) {
		return network.wrapInVanilla(msg, dir);
	}

}