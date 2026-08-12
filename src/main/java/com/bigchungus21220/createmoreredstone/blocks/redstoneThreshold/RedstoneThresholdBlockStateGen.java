package com.bigchungus21220.createmoreredstone.blocks.redstoneThreshold;

import com.bigchungus21220.createmoreredstone.CreateMoreRedstone;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;

public class RedstoneThresholdBlockStateGen {
    public static ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder> rotateHorizontal(final Direction direction, final ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder> builder) {
        final int angleOffset = 0;
        builder.rotationY(((int) direction.toYRot() + angleOffset) % 360);
        return builder;
    }

    public static <P extends RedstoneThresholdBlock> NonNullBiConsumer<DataGenContext<Block, P>, RegistrateBlockstateProvider> generate() {
        return (ctx, prov) -> {
            final ModelFile torchOff = prov.models().withExistingParent("redstone_threshold", CreateMoreRedstone.path("block/diodes/redstone_threshold"));

            final ModelFile torchOn = prov.models().withExistingParent("redstone_threshold_on", CreateMoreRedstone.path("block/diodes/redstone_threshold"))
                .texture("0", ResourceLocation.withDefaultNamespace("block/redstone_torch"));

            prov.getVariantBuilder(ctx.get()).forAllStates(state -> {
                final Direction facing = state.getValue(RedstoneThresholdBlock.FACING);
                final boolean powering = state.getValue(RedstoneThresholdBlock.POWERING);

                final int yRot = (int) facing.toYRot();

                return ConfiguredModel.builder()
                    .modelFile(powering ? torchOn : torchOff)
                    .rotationY(yRot)
                    .build();
            });
        };
    }
}
