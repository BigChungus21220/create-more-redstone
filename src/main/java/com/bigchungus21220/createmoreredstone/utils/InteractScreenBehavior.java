package com.bigchungus21220.createmoreredstone.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour.ValueSettings;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.FakePlayer;

public class InteractScreenBehavior extends BlockEntityBehaviour implements ValueSettingsBehaviour {
    public static final BehaviourType<InteractScreenBehavior> TYPE = new BehaviourType<>();

    ValueBoxTransform slotPositioning;
    Component label;
    Function<SmartBlockEntity, AbstractSimiScreen> screenFactory;
	Consumer<Integer> callback;
	Consumer<Integer> clientCallback;
	Function<Integer, String> formatter;
	private Supplier<Boolean> isActive;
	boolean needsWrench;

    public InteractScreenBehavior(Component label, SmartBlockEntity be, Function<SmartBlockEntity, AbstractSimiScreen> screenFactory, ValueBoxTransform slot) {
        super(be);
        this.setLabel(label);
        this.screenFactory = screenFactory;
		slotPositioning = slot;
		callback = i -> {};
		clientCallback = i -> {};
		formatter = i -> Integer.toString(i);
		isActive = () -> true;
    }

	@Override
	public boolean isSafeNBT() {
		return true;
	}

    @OnlyIn(Dist.CLIENT)
    public void openScreen() {
        if (this.screenFactory != null) {
            Minecraft.getInstance().setScreen(screenFactory.apply(blockEntity));
        }
    }

	@Override
	public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
		//nbt.putInt("ScrollValue", value);
		//super.write(nbt, registries, clientPacket);
	}

	@Override
	public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
		//value = nbt.getInt("ScrollValue");
		//super.read(nbt, registries, clientPacket);
	}

	public InteractScreenBehavior withClientCallback(Consumer<Integer> valueCallback) {
		clientCallback = valueCallback;
		return this;
	}

	public InteractScreenBehavior withCallback(Consumer<Integer> valueCallback) {
		callback = valueCallback;
		return this;
	}

	public InteractScreenBehavior requiresWrench() {
		this.needsWrench = true;
		return this;
	}

	public InteractScreenBehavior withFormatter(Function<Integer, String> formatter) {
		this.formatter = formatter;
		return this;
	}

	public InteractScreenBehavior onlyActiveWhen(Supplier<Boolean> condition) {
		isActive = condition;
		return this;
	}

	/*public void setValue(int value) {
		value = Mth.clamp(value, min, max);
		if (value == this.value)
			return;
		this.value = value;
		callback.accept(value);
		blockEntity.setChanged();
		blockEntity.sendData();
	}

	public int getValue() {
		return value;
	}

	public String formatValue() {
		return formatter.apply(value);
	}*/

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	@Override
	public boolean isActive() {
		return isActive.get();
	}

	@Override
	public boolean testHit(Vec3 hit) {
		BlockState state = blockEntity.getBlockState();
		Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf(blockEntity.getBlockPos()));
		return slotPositioning.testHit(getWorld(), getPos(), state, localHit);
	}

	public void setLabel(Component label) {
		this.label = label;
	}

	public static class StepContext {
		public int currentValue;
		public boolean forward;
		public boolean shift;
		public boolean control;
	}

	@Override
	public ValueBoxTransform getSlotPositioning() {
		return slotPositioning;
	}

	@Override
	public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
		return new ValueSettingsBoard(label, max, 10, ImmutableList.of(Component.literal("Value")),
			new ValueSettingsFormatter(ValueSettings::format));
	}

	@Override
	public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlDown) {
		if (valueSetting.equals(getValueSettings()))
			return;
		setValue(valueSetting.value());
		playFeedbackSound(this);
	}

	@Override
	public ValueSettings getValueSettings() {
		return new ValueSettings(0, value);
	}

	@Override
	public boolean onlyVisibleWithWrench() {
		return needsWrench;
	}

	@Override
	public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {
		if (player instanceof FakePlayer)
			blockEntity.getBlockState()
				.useItemOn(player.getItemInHand(hand), getWorld(), player, hand, hitResult);
	}
}
