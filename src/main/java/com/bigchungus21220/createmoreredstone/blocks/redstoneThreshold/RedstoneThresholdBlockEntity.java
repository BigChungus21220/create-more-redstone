package com.bigchungus21220.createmoreredstone.blocks.redstoneThreshold;

/*
todo:
add hover text
set default values
convert readout to translatable text
add ponder screen + hover info
improve model
*/

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import static com.bigchungus21220.createmoreredstone.CreateMoreRedstone.LOGGER;

import java.util.List;

import dev.engine_room.flywheel.lib.transform.TransformStack;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;

import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RedstoneThresholdBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, ClipboardCloneable, IWrenchable {
	// upper threshold, lower threshold (if lower < upper, invert)
	// can't use ScrollValueBehavior since it is that one specific screen
    int upperThreshold;
	int lowerThreshold;
    int outputSignal;

	public RedstoneThresholdBlockEntity(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
	}

    @Override
    public void initialize() {
        super.initialize();
        this.updateSignal();
    }

    	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		upperThreshold = compound.getInt("UpperThreshold");
		lowerThreshold = compound.getInt("LowerThreshold");
		outputSignal = compound.getInt("OutputSignal");
		super.read(compound, registries, clientPacket);
	}

	protected void writeCommon(CompoundTag compound) {
		compound.putInt("UpperThreshold", upperThreshold);
		compound.putInt("LowerThreshold", lowerThreshold);
		compound.putInt("OutputSignal", outputSignal);
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		writeCommon(compound);
		super.write(compound, registries, clientPacket);
	}

	@Override
	public void writeSafe(CompoundTag compound, HolderLookup.Provider registries) {
		writeCommon(compound);
		super.writeSafe(compound, registries);
	}

    @Override
    public void addBehaviours(final List<BlockEntityBehaviour> behaviours) {}

    private String format(final int value) {
		return value + "";
    }

	private void updateFacingBlock(final Block block, final Level levelIn) {
        levelIn.updateNeighborsAt(this.worldPosition, block);
        levelIn.updateNeighborsAt(this.worldPosition.relative(this.getBlockState().getValue(RedstoneThresholdBlock.FACING).getOpposite()), block);
    }

	private void thresholdChanged(final Integer threshold) {
		this.updateSignal();
	}

    public void updateSignal() {
        this.updateFacingBlock((RedstoneThresholdBlock) this.getBlockState().getBlock(), this.getLevel());
        this.notifyUpdate();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level == null) {
            return;
        }

        final RedstoneThresholdBlock block = (RedstoneThresholdBlock) this.getBlockState().getBlock();

        final int backSignal = block.getBackSignal(this.level, this.worldPosition, this.getBlockState());

        final boolean inverted = this.getBlockState().getValue(RedstoneThresholdBlock.INVERTED);

        if (!this.level.isClientSide) {
			int tempPower = ((backSignal <= upperThreshold && backSignal >= lowerThreshold) != inverted) ? 15 : 0;

            if(this.outputSignal != tempPower) {
                this.outputSignal = tempPower;
                this.updateFacingBlock(block, this.level);
                this.sendData();
            }
        }
    }

    @Override
    public String getClipboardKey() {
        return "Block";
    }

    @Override
    public boolean readFromClipboard(final HolderLookup.@NotNull Provider provider, final CompoundTag tag, final Player player, final Direction direction, final boolean simulate) {
        if (!tag.contains("Inverted")) {
            return false;
        } else if (simulate) {
            return true;
        } else {
            final BlockState blockState = this.getBlockState();
            boolean inverted = tag.getBoolean("Inverted");
            if (blockState.getValue(RedstoneThresholdBlock.INVERTED) != inverted) {
                this.level.setBlockAndUpdate(this.worldPosition, blockState.setValue(RedstoneThresholdBlock.INVERTED, inverted));
            }
            if (tag.contains("LowerThreshold"))
                this.lowerThreshold = tag.getInt("LowerThreshold");
            if (tag.contains("UpperThreshold"))
                this.upperThreshold = tag.getInt("UpperThreshold");
            this.sendData();
            return true;
        }
    }

    @Override
    public boolean writeToClipboard(final HolderLookup.@NotNull Provider provider, final CompoundTag tag, final Direction direction) {
        tag.putBoolean("Inverted", this.getBlockState().getOptionalValue(RedstoneThresholdBlock.INVERTED).orElse(false));
        tag.putInt("LowerThreshold", lowerThreshold);
        tag.putInt("UpperThreshold", upperThreshold);
        return true;
    }

    public int getMinLevel() {
        return 0;
    }

    public int getMaxLevel() {
        return 15;
    }

    public boolean isInverted() {
        LOGGER.info("reading inverted state" + getBlockState().getValue(RedstoneThresholdBlock.INVERTED));
        return getBlockState().getValue(RedstoneThresholdBlock.INVERTED);
    }

    public boolean isPowered() {
        return getBlockState().getValue(RedstoneThresholdBlock.POWERED);
    }

    public void setInverted(boolean inverted) {
        BlockState bs = getBlockState();
        if (bs.getValue(RedstoneThresholdBlock.INVERTED) == inverted)
            return;
        BlockState newState = bs.setValue(RedstoneThresholdBlock.INVERTED, inverted);
        
        this.level.setBlock(this.worldPosition, newState, 3);

        LOGGER.info("setting inverted state to " + inverted);
    }
    
	private static class RedstoneThresholdValueBoxTransform extends ValueBoxTransform {

		private final boolean isUpper;

		public RedstoneThresholdValueBoxTransform(boolean isUpper) {
			this.isUpper = isUpper;
		}

		@Override
		public Vec3 getLocalOffset(final LevelAccessor levelAccessor, final BlockPos blockPos, final BlockState blockState) {
			return new Vec3(0.5, 3.5f / 16.0f, this.isUpper ? 0.7 : 0.3);
		}

		@Override
		public void rotate(final LevelAccessor levelAccessor, final BlockPos blockPos, final BlockState blockState, final PoseStack poseStack) {
			final float yRot = AngleHelper.horizontalAngle(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING));
			TransformStack.of(poseStack)
                .rotateYDegrees(yRot)
                .rotateXDegrees(90);
		}

		@Override
		public float getScale() {
			return 0.5f;
		}
	}
}