package com.bigchungus21220.createmoreredstone.index;

import com.bigchungus21220.createmoreredstone.CreateMoreRedstone;
import com.bigchungus21220.createmoreredstone.blocks.RedstoneThresholdBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class MRBlockEntities {
	public static final BlockEntityEntry<RedstoneThresholdBlockEntity> REDSTONE_THRESHOLD = CreateMoreRedstone.REGISTRATE
		.blockEntity("redstone_threshold", RedstoneThresholdBlockEntity::new)
		.validBlocks(MRBlocks.REDSTONE_THRESHOLD)
		.register();

    public static void register() {

	}
}
