package com.bigchungus21220.createmoreredstone.blocks.redstoneThreshold;

import com.bigchungus21220.createmoreredstone.gui.DoubleSliderWidget;
import com.bigchungus21220.createmoreredstone.index.MRGuiTextures;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.foundation.ui.PonderTagScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import static com.bigchungus21220.createmoreredstone.CreateMoreRedstone.LOGGER;

public class RedstoneThresholdScreen extends AbstractSimiScreen {
	private IconButton confirmButton;
	private DoubleSliderWidget slider;

	private MRGuiTextures background;
	private RedstoneThresholdBlockEntity blockEntity;
	private int lastModification;

	public RedstoneThresholdScreen(SmartBlockEntity be) {
		super(Component.translatable("gui.createmoreredstone.redstone_threshold.title"));
		background = MRGuiTextures.REDSTONE_THRESHOLD;
		lastModification = -1;
		this.blockEntity = switch (be) {
			case RedstoneThresholdBlockEntity rtbe -> rtbe;
			default -> throw new IllegalArgumentException("Expected RedstoneThresholdBlockEntity, got: " + be.getClass().getName());
		};
	}

	@Override
	protected void init() {
		setWindowSize(background.getWidth(), background.getHeight());
		setWindowOffset(-20, 0);
		super.init();

		int x = guiLeft;
		int y = guiTop;

		LOGGER.info("screen opened. inverted: " + blockEntity.isInverted());

		if (blockEntity.isInverted()) {
			slider = new DoubleSliderWidget(x + 5, y + 17, 0, 15, blockEntity.lowerThreshold, blockEntity.upperThreshold);
		} else {
			slider = new DoubleSliderWidget(x + 5, y + 17, 0, 15, blockEntity.upperThreshold, blockEntity.lowerThreshold);
		}

		addRenderableWidget(slider);

		confirmButton = new IconButton(x + background.getWidth() - 33, y + background.getHeight() - 35, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> onClose());
		addRenderableWidget(confirmButton);
	}

	@Override
	protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int x = guiLeft;
		int y = guiTop;

		background.render(graphics, x, y);
		graphics.drawString(font, title, x + background.getWidth() / 2 - font.width(title) / 2, y + 4, 0x592424, false);

		int left = slider.getLeft();
		int right = slider.getRight();

		int left_pos = 73;
		if (left < right) {
			MRGuiTextures.REDSTONE_THRESHOLD_INVERTED.render(graphics, x, y + 31);
			left ++;
			right --;
			left_pos = 98;
		}

		String left_text = "" + left;
		graphics.drawString(
            font, 
			Component.literal(left_text), 
			x + left_pos - font.width(left_text)/2, y + 40, 
            0xFFFFFFFF, 
            true
        );

		String right_text = "" + right;
		graphics.drawString(
            font, 
			Component.literal(right_text), 
			x + 23 - font.width(right_text)/2, y + 40, 
            0xFFFFFFFF, 
            true
        );
	}

	@Override
	public void tick() {
		super.tick();

		if (lastModification >= 0)
			lastModification++;

		if (lastModification >= 20) {
			lastModification = -1;
			send();
		}
	}

	@Override
	public void removed() {
		send();
	}

	protected void send() {
		int left = slider.getLeft();
		int right = slider.getRight();
		int lower = Math.min(left, right);
		int upper = Math.max(left, right);
		boolean invert = left < right;
		CatnipServices.NETWORK.sendToServer(
            new ConfigureRedstoneThresholdPacket(blockEntity.getBlockPos(), lower, upper, invert)
        );
	}
}
