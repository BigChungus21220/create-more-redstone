package com.bigchungus21220.createmoreredstone.index;

import com.simibubi.create.AllShapes.Builder;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;


import static net.minecraft.core.Direction.*;

public class MRBlockShapes {
    public static final VoxelShaper REDSTONE_THRESHOLD =
        shape(0, 0, 0, 16, 2, 16)
        .forHorizontal(NORTH);

    private static Builder shape(final VoxelShape shape) {
        return new Builder(shape);
    }

    private static Builder shape(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }

    public static void register() {

	}
}
