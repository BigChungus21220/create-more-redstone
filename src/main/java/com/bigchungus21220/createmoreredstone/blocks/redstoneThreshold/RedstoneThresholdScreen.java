package com.bigchungus21220.createmoreredstone.blocks.redstoneThreshold;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.foundation.ui.PonderTagScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneTorchBlock;

public class RedstoneThresholdScreen extends AbstractSimiScreen {
    
	private ScrollInput offBelow;
	private ScrollInput onAbove;

	private IconButton confirmButton;
	private IconButton flipSignals;

	private final Component invertSignal = CreateLang.translateDirect("gui.threshold_switch.invert_signal");

	private AllGuiTextures background;
	private RedstoneThresholdBlockEntity blockEntity;
	private int lastModification;

	public RedstoneThresholdScreen(SmartBlockEntity be) {
		super(CreateLang.translateDirect("gui.threshold_switch.title"));
		background = AllGuiTextures.THRESHOLD_SWITCH;
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

		offBelow = new ScrollInput(x + 48, y + 47, 1, 18)
			.withRange(blockEntity.getMinLevel(), blockEntity.getMaxLevel())
			.titled(CreateLang.translateDirect("gui.threshold_switch.lower_threshold"))
			.calling(state -> {
				lastModification = 0;

				if (onAbove.getState() == 0 && state == 0)
					return;
				
				if (onAbove.getState() <= state) {
					onAbove.setState(state + 1);
					onAbove.onChanged();
				}
			})
			.withStepFunction(sc -> 1)
			.setState(blockEntity.lowerThreshold);

		onAbove = new ScrollInput(x + 48, y + 23, 1, 18)
			.withRange(blockEntity.getMinLevel() + 1, blockEntity.getMaxLevel() + 1)
			.titled(CreateLang.translateDirect("gui.threshold_switch.upper_threshold"))
			.calling(state -> {
				lastModification = 0;

				if (offBelow.getState() == 0 && state == 0)
					return;

				if (offBelow.getState() >= state) {
					offBelow.setState(state - 1);
					offBelow.onChanged();
				}
			})
			.withStepFunction(sc -> 1)
			.setState(blockEntity.upperThreshold);

		onAbove.onChanged();
		offBelow.onChanged();

		addRenderableWidget(onAbove);
		addRenderableWidget(offBelow);

		confirmButton =
			new IconButton(x + background.getWidth() - 33, y + background.getHeight() - 24, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> onClose());
		addRenderableWidget(confirmButton);

		flipSignals = new IconButton(x + background.getWidth() - 62, y + background.getHeight() - 24, AllIcons.I_FLIP);
		flipSignals.withCallback(() -> send(!blockEntity.isInverted()));
		flipSignals.setToolTip(invertSignal);
		addRenderableWidget(flipSignals);

		updateInputBoxes();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int pButton) {
		int itemX = guiLeft + 13;
		int itemY = guiTop + 80;
		if (mouseX >= itemX && mouseX < itemX + 16 && mouseY >= itemY && mouseY < itemY + 16) {
			ScreenOpener.open(new PonderTagScreen(AllCreatePonderTags.THRESHOLD_SWITCH_TARGETS));
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, pButton);
	}

	@Override
	protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int x = guiLeft;
		int y = guiTop;

		background.render(graphics, x, y);
		graphics.drawString(font, title, x + background.getWidth() / 2 - font.width(title) / 2, y + 4, 0x592424, false);

		AllGuiTextures inputBg = AllGuiTextures.THRESHOLD_SWITCH_ITEMCOUNT_INPUTS;

		inputBg.render(graphics, x + 44, y + 21);
		inputBg.render(graphics, x + 44, y + 21 + 24);

        Component suffix = CreateLang.translateDirect("schedule.condition.threshold.items");
        graphics.drawString(font, suffix, x + 105, y + 28, 0xFFFFFFFF, true);
        graphics.drawString(font, suffix, x + 105, y + 28 + 24, 0xFFFFFFFF, true);

		graphics.drawString(
            font, 
            Component.literal("\u2265 " + onAbove.getState()), x + 53, y + 28, 
            0xFFFFFFFF, 
            true
        );
		graphics.drawString(
            font,
			Component.literal("\u2264 " + offBelow.getState()), x + 53, y + 28 + 24, 
            0xFFFFFFFF, 
            true
        );

		int torchX = x + 23;
		int torchY = y + 24;

		boolean highlightTopRow = blockEntity.isInverted() ^ blockEntity.isPowered();
		AllGuiTextures.THRESHOLD_SWITCH_CURRENT_STATE.render(graphics, torchX - 3,
			torchY - 4 + (highlightTopRow ? 0 : 24));

		PoseStack ms = graphics.pose();
		ms.pushPose();
		ms.translate(torchX - 5, torchY + 14, 200);
		TransformStack.of(ms)
			.rotateXDegrees(-22.5f)
			.rotateYDegrees(45);

		for (boolean power : Iterate.trueAndFalse) {
			GuiGameElement.of(Blocks.REDSTONE_TORCH.defaultBlockState()
					.setValue(RedstoneTorchBlock.LIT, blockEntity.isInverted() ^ power))
				.scale(20)
				.render(graphics);
			ms.translate(0, 26, 0);
		}

		ms.popPose();

		for (boolean power : Iterate.trueAndFalse) {
			int thisTorchY = power ? torchY : torchY + 26;
			if (mouseX >= torchX && mouseX < torchX + 16 && mouseY >= thisTorchY && mouseY < thisTorchY + 16) {
				graphics.renderComponentTooltip(font,
					List.of(CreateLang
						.translate(power ^ blockEntity.isInverted() ? "gui.threshold_switch.power_on_when"
							: "gui.threshold_switch.power_off_when")
						.color(AbstractSimiWidget.HEADER_RGB)
						.component()),
					mouseX, mouseY);
				return;
			}
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (lastModification >= 0)
			lastModification++;

		if (lastModification >= 20) {
			lastModification = -1;
			send(blockEntity.isInverted());
		}

		updateInputBoxes();
	}

	private void updateInputBoxes() {
		onAbove.setWidth(48);
		offBelow.setWidth(48);

		int min = blockEntity.lowerThreshold + 1;
		int max = blockEntity.upperThreshold;
		onAbove.withRange(min, max + 1);
		int roundedState = Mth.clamp(onAbove.getState(), min, max);
		if (roundedState != onAbove.getState()) {
			onAbove.setState(roundedState);
			onAbove.onChanged();
		}

		min = blockEntity.lowerThreshold;
		max = blockEntity.upperThreshold - 1;
		offBelow.withRange(min, max + 1);
		roundedState = Mth.clamp(offBelow.getState(), min, max);
		if (roundedState != offBelow.getState()) {
			offBelow.setState(roundedState);
			offBelow.onChanged();
		}
	}

	@Override
	public void removed() {
		send(blockEntity.isInverted());
	}

	protected void send(boolean invert) {
		CatnipServices.NETWORK.sendToServer(
            new ConfigureRedstoneThresholdPacket(blockEntity.getBlockPos(), offBelow.getState(),onAbove.getState(), invert)
        );
	}
}
