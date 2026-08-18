package com.bigchungus21220.createmoreredstone.gui;

import java.util.function.BiConsumer;

import com.bigchungus21220.createmoreredstone.index.MRGuiTextures;
import com.bigchungus21220.createmoreredstone.index.MRSprites;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllSoundEvents;

import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;

public class DoubleSliderWidget extends AbstractSimiWidget {

    protected static final int step_size = 8;
    // dimensions of the handle interaction area
    protected static final int handle_width = 7;
    protected static final int handle_height = 14;

    protected int minValue;
    protected int maxValue;
    protected int steps;

    protected int leftPos; // the x offset of the slider
    protected int rightPos; // ^
    protected int leftValue; // the tick the slider is on (not the output value)
    protected int rightValue; // ^

    protected int barWidth;

    protected Focus focus;
    protected BiConsumer<Integer, Integer> onChanged;

    private enum Focus {
        None,
        Left,
        Right
    }
    
    /*
     Creates a double slider. 
     Output values go one below minValue and one above maxValue when the slider is inverted.
    */
    public DoubleSliderWidget(int x, int y, int minValue, int maxValue, int leftValue, int rightValue) {
        super(x - handle_width/2, y, (maxValue - minValue + 1)*step_size + handle_width, handle_height);
        this.steps = maxValue - minValue + 2;
        this.minValue = minValue;
        this.maxValue = maxValue;
        focus = Focus.None;
        this.onChanged = (a,b) -> {}; 
        setLeft(leftValue + 1 - minValue, false);
        setRight(rightValue - minValue, false);
        barWidth = step_size*(steps-1) + 7;
	}

	@Override
	public void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
            MRSprites between = leftValue < rightValue ? MRSprites.REDSTONE_BAR : MRSprites.REDSTONE_BAR_LIT; 
            MRSprites betwaint = leftValue < rightValue ? MRSprites.REDSTONE_BAR_LIT : MRSprites.REDSTONE_BAR;
            int lowerPos = Math.min(leftPos, rightPos);
            int upperPos = Math.max(leftPos, rightPos);

            betwaint.renderClipped(
                graphics, 
                getX() + 1, getY() - 1, 
                barWidth, 14, 
                0, 0, 
                barWidth - lowerPos - 3, 0
            );

            between.renderClipped(
                graphics, 
                getX() + 1, getY() - 1, 
                barWidth, 14, 
                lowerPos + 3, 0, 
                barWidth - upperPos - 4, 0
            );

            betwaint.renderClipped(
                graphics, 
                getX() + 1, getY() - 1, 
                barWidth, 14, 
                upperPos + 4, 0, 
                0, 0
            );

            Focus hover = getHover(mouseX, mouseY);
            boolean leftFocus = focus == Focus.Left || (hover == Focus.Left && focus != Focus.Right);
            boolean rightFocus = focus == Focus.Right || (hover == Focus.Right && focus != Focus.Left);

            // render stop texture if the handle would point to nothing
            MRGuiTextures leftHandle = (leftValue > 0) ? 
                leftFocus ? MRGuiTextures.WIDGET_DOUBLE_SLIDER_LEFT_ARROW_HOVER
                    : MRGuiTextures.WIDGET_DOUBLE_SLIDER_LEFT_ARROW
                : leftFocus ? MRGuiTextures.WIDGET_DOUBLE_SLIDER_STOP_HOVER
                    : MRGuiTextures.WIDGET_DOUBLE_SLIDER_STOP;

            MRGuiTextures rightHandle = (rightValue < steps-1) ? 
                rightFocus ? MRGuiTextures.WIDGET_DOUBLE_SLIDER_RIGHT_ARROW_HOVER
                    : MRGuiTextures.WIDGET_DOUBLE_SLIDER_RIGHT_ARROW
                : rightFocus ? MRGuiTextures.WIDGET_DOUBLE_SLIDER_STOP_HOVER
                    : MRGuiTextures.WIDGET_DOUBLE_SLIDER_STOP;

			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            // focused element moves to front
            if (leftFocus) {
                rightHandle.render(graphics, getX() + rightPos, getY());
                leftHandle.render(graphics, getX() + leftPos, getY());
            } else {
                leftHandle.render(graphics, getX() + leftPos, getY());
                rightHandle.render(graphics, getX() + rightPos, getY());
            }
		}
	}

    public DoubleSliderWidget withOnChanged(BiConsumer<Integer, Integer> callback) {
        this.onChanged = callback;
        return this;
    }

    private void setLeft(int left, boolean notify) {
        leftValue = left;
        leftPos = (leftValue - minValue)*step_size;
        if (notify)
            onChanged.accept(leftValue, rightValue);
    }

    private void setRight(int right, boolean notify) {
        rightValue = right;
        rightPos = (right - minValue)*step_size;
        if (notify)
            onChanged.accept(leftValue, rightValue);
    }

    public int getLeft(){
        return leftValue - 1 + minValue;
    }

    public int getRight(){
        return rightValue + minValue;
    }

    // this override is to disable the automatic sound event
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(button)) {
                boolean flag = this.clicked(mouseX, mouseY);
                if (flag) {
                    //this.playDownSound(Minecraft.getInstance().getSoundManager());
                    this.onClick(mouseX, mouseY, button);
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
	public void onClick(double mouseX, double mouseY, int button) {
		focus = getHover((int)mouseX, (int)mouseY);
	}

    @Override
    public void onRelease(double mouseX, double mouseY) {
        focus = Focus.None;
    }

    protected void playTick(int value) {
        Minecraft.getInstance()
            .getSoundManager()
            .play(SimpleSoundInstance.forUI(AllSoundEvents.SCROLL_VALUE.getMainEvent(), 1.5f + 0.1f*value/steps));
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (focus == Focus.Left) {
            int value = valueFromMouse(mouseX, rightValue);
            if (leftValue != value) playTick(value);

            setLeft(value, true);
        } else if (focus == Focus.Right) {
            int value = valueFromMouse(mouseX, leftValue);
            if (rightValue != value) playTick(value);

            setRight(value, true);
        }
    }

    private int valueFromMouse(double mouseX, int avoid) {
        int target = Math.clamp((int)(mouseX - getX())/step_size, 0, steps-1);
        // if the target tick should be avoided, move to the nearest valid tick
        if (target == avoid) {
            if (avoid == 0) {
                return avoid + 1;
            } else if (avoid == steps-1) {
                return avoid - 1;
            }
            double val = (mouseX - getX())/step_size - avoid;
            return val > 0.5 ? target + 1 : target - 1;
        }
        return target;
    }

    private Focus getHover(int mouseX, int mouseY) {
        if (
            mouseX >= getX() + leftPos + 1 && 
            mouseY >= getY() - 1 && 
            mouseX < getX() + leftPos + 1 + handle_width && 
            mouseY < getY() - 1 + handle_height
        ) {
            return Focus.Left;
        } else if (
            mouseX >= getX() + rightPos + 1 && 
            mouseY >= getY() - 1 && 
            mouseX < getX() + rightPos + 1 + handle_width && 
            mouseY < getY() - 1 + handle_height
        ) {
            return Focus.Right;
        } else {
            return Focus.None;
        }
    }
}
