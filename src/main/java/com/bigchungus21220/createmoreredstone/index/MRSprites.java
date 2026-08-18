package com.bigchungus21220.createmoreredstone.index;

import com.bigchungus21220.createmoreredstone.CreateMoreRedstone;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public enum MRSprites {
    REDSTONE_BAR("redstone_bar"),
	REDSTONE_BAR_LIT("redstone_bar_lit");


	public final ResourceLocation location;

	MRSprites(String location) {
		this(CreateMoreRedstone.MODID, location);
	}

	MRSprites(String namespace, String location) {
		this.location = ResourceLocation.fromNamespaceAndPath(namespace, location);
	}

	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, int x, int y, int width, int height) {
		graphics.blitSprite(location, x, y, width, height);
	}

    @OnlyIn(Dist.CLIENT)
    public void renderClipped(GuiGraphics guiGraphics, int x, int y, int scaleWidth, int scaleHeight, int clipLeft, int clipTop, int clipRight, int clipBottom) {
        int clipWidth = scaleWidth - clipLeft - clipRight;
        int clipHeight = scaleHeight - clipTop - clipBottom;

        if (clipWidth <= 0 || clipHeight <= 0) return;

        guiGraphics.enableScissor(
            x + clipLeft, 
            y + clipTop, 
            x + clipWidth + clipLeft, 
            y + clipHeight + clipTop
        );

        guiGraphics.blitSprite(location, x, y, scaleWidth, scaleHeight);

        guiGraphics.disableScissor();
    }
}
