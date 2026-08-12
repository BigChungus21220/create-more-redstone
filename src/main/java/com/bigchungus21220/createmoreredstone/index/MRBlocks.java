package com.bigchungus21220.createmoreredstone.index;

import com.bigchungus21220.createmoreredstone.CreateMoreRedstone;
import com.bigchungus21220.createmoreredstone.blocks.redstoneThreshold.RedstoneThresholdBlock;
import com.bigchungus21220.createmoreredstone.blocks.redstoneThreshold.RedstoneThresholdBlockStateGen;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.redstone.diodes.AbstractDiodeGenerator;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.block.Blocks;

public class MRBlocks {

    public static final BlockEntry<RedstoneThresholdBlock> REDSTONE_THRESHOLD = CreateMoreRedstone.REGISTRATE.block("redstone_threshold", RedstoneThresholdBlock::new)
        .initialProperties(() -> Blocks.REPEATER)
        .blockstate(RedstoneThresholdBlockStateGen.generate())
        .tag(AllTags.AllBlockTags.SAFE_NBT.tag, AllTags.AllBlockTags.WRENCH_PICKUP.tag)
        .recipe((c, p) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                .pattern(" Q ")
                .pattern("RBT")
                .pattern("SSS")
                .define('T', Blocks.REDSTONE_TORCH)
                .define('B', CommonMetal.BRASS.plates)
                .define('R', Blocks.REDSTONE_WIRE)
                .define('Q', AllItems.POLISHED_ROSE_QUARTZ)
                .define('S', Blocks.ANDESITE)
                .unlockedBy("has_ingredient", RegistrateRecipeProvider.has(Blocks.REDSTONE_WIRE))
                .save(p))
        .item()
        .model(AbstractDiodeGenerator::diodeItemModel)
        .build()
        .register();

    public static void register() {

	}
}
