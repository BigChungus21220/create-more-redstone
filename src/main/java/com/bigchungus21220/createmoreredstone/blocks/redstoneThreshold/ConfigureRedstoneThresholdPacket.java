package com.bigchungus21220.createmoreredstone.blocks.redstoneThreshold;

import com.bigchungus21220.createmoreredstone.index.MRPackets;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import static com.bigchungus21220.createmoreredstone.CreateMoreRedstone.LOGGER;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class ConfigureRedstoneThresholdPacket extends BlockEntityConfigurationPacket<RedstoneThresholdBlockEntity> {
    public static final StreamCodec<ByteBuf, ConfigureRedstoneThresholdPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, packet -> packet.pos,
        ByteBufCodecs.INT, packet -> packet.lowerValue,
        ByteBufCodecs.INT, packet -> packet.upperValue,
        ByteBufCodecs.BOOL, packet -> packet.invert,
        ConfigureRedstoneThresholdPacket::new
	);

	private final int lowerValue;
	private final int upperValue;
	private final boolean invert;

	public ConfigureRedstoneThresholdPacket(BlockPos pos, int lowerValue, int upperValue, boolean invert) {
		super(pos);
		this.lowerValue = lowerValue;
		this.upperValue = upperValue;
		this.invert = invert;
	}

	@Override
	protected void applySettings(ServerPlayer player, RedstoneThresholdBlockEntity be) {
		be.upperThreshold = upperValue;
		be.lowerThreshold = lowerValue;
		be.setInverted(invert);
		//be.setChanged();

		//level.sendBlockUpdated(blockPos, level.getBlockState(blockPos), level.getBlockState(blockPos), Block.UPDATE_CLIENTS);

		LOGGER.info("settings applied. upper value: " + upperValue);
	}

	@Override
	public PacketTypeProvider getTypeProvider() {
		return MRPackets.CONFIGURE_REDSTONE_THRESHOLD;
	}
}
