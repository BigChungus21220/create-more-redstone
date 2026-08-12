package com.bigchungus21220.createmoreredstone.blocks.redstoneThreshold;

import com.bigchungus21220.createmoreredstone.index.MRPackets;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class ConfigureRedstoneThresholdPacket extends BlockEntityConfigurationPacket<RedstoneThresholdBlockEntity> {
    public static final StreamCodec<ByteBuf, ConfigureRedstoneThresholdPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, packet -> packet.pos,
        ByteBufCodecs.INT, packet -> packet.offBelow,
        ByteBufCodecs.INT, packet -> packet.onAbove,
        ByteBufCodecs.BOOL, packet -> packet.invert,
        ConfigureRedstoneThresholdPacket::new
	);

	private final int offBelow;
	private final int onAbove;
	private final boolean invert;

	public ConfigureRedstoneThresholdPacket(BlockPos pos, int offBelow, int onAbove, boolean invert) {
		super(pos);
		this.offBelow = offBelow;
		this.onAbove = onAbove;
		this.invert = invert;
	}

	@Override
	protected void applySettings(ServerPlayer player, RedstoneThresholdBlockEntity be) {
		be.upperThreshold = offBelow;
		be.lowerThreshold = onAbove;
		be.setInverted(invert);
	}

	@Override
	public PacketTypeProvider getTypeProvider() {
		return MRPackets.CONFIGURE_REDSTONE_THRESHOLD;
	}
}
