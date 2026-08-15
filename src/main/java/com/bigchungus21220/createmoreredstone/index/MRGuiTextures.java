package com.bigchungus21220.createmoreredstone.index;

import com.bigchungus21220.createmoreredstone.CreateMoreRedstone;

import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public enum MRGuiTextures implements ScreenElement, TextureSheetSegment {

	REDSTONE_THRESHOLD("redstone_threshold", 178, 55),
    REDSTONE_THRESHOLD_BAR_OFF("redstone_threshold", 3, 80, 135, 14),
	REDSTONE_THRESHOLD_INVERTED("redstone_threshold", 0, 55, 140, 24),

    WIDGET_DOUBLE_SLIDER_LEFT_ARROW("widgets", 0, 0, 9, 12),
    WIDGET_DOUBLE_SLIDER_RIGHT_ARROW("widgets", 9, 0, 9, 12),
    WIDGET_DOUBLE_SLIDER_STOP("widgets", 18, 0, 9, 12),
    WIDGET_DOUBLE_SLIDER_LEFT_ARROW_HOVER("widgets", 0, 12, 9, 12),
    WIDGET_DOUBLE_SLIDER_RIGHT_ARROW_HOVER("widgets", 9, 12, 9, 12),
    WIDGET_DOUBLE_SLIDER_STOP_HOVER("widgets", 18, 12, 9, 12);

	public static final int FONT_COLOR = 0x575F7A;

	public final ResourceLocation location;
	private final int width;
	private final int height;
	private final int startX;
	private final int startY;

	MRGuiTextures(String location, int width, int height) {
		this(location, 0, 0, width, height);
	}

	MRGuiTextures(String location, int startX, int startY, int width, int height) {
		this(CreateMoreRedstone.MODID, location, startX, startY, width, height);
	}

	MRGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
		this.location = ResourceLocation.fromNamespaceAndPath(namespace, "textures/gui/" + location + ".png");
		this.width = width;
		this.height = height;
		this.startX = startX;
		this.startY = startY;
	}

	@Override
	public ResourceLocation getLocation() {
		return location;
	}

	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, int x, int y) {
		graphics.blit(location, x, y, startX, startY, width, height);
	}

	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, int x, int y, Color c) {
		bind();
		UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
	}

	@Override
	public int getStartX() {
		return startX;
	}

	@Override
	public int getStartY() {
		return startY;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
}