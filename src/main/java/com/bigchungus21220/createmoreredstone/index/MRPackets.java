package com.bigchungus21220.createmoreredstone.index;

import java.util.Locale;

import com.bigchungus21220.createmoreredstone.blocks.redstoneThreshold.ConfigureRedstoneThresholdPacket;
import com.simibubi.create.Create;
import com.simibubi.create.CreateBuildInfo;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public enum MRPackets implements BasePacketPayload.PacketTypeProvider {
    CONFIGURE_REDSTONE_THRESHOLD(ConfigureRedstoneThresholdPacket.class, ConfigureRedstoneThresholdPacket.STREAM_CODEC);

    private final CatnipPacketRegistry.PacketType<?> type;

	<T extends BasePacketPayload> MRPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
		String name = this.name().toLowerCase(Locale.ROOT);
		this.type = new CatnipPacketRegistry.PacketType<>(
			new CustomPacketPayload.Type<>(Create.asResource(name)),
			clazz, codec
		);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
		return (CustomPacketPayload.Type<T>) this.type.type();
	}

	public static void register() {
		CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(Create.ID, CreateBuildInfo.VERSION);
		for (MRPackets packet : MRPackets.values()) {
			packetRegistry.registerPacket(packet.type);
		}
		packetRegistry.registerAllPackets();
	}
}
