package com.sacredmod.car;

import com.sacredmod.race.ModAttachments;
import com.sacredmod.race.RaceStats;
import com.sacredmod.network.RaceDriveInput;
import com.sacredmod.item.ModItems;
import com.sacredmod.util.RaceMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.MovementEmission;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import java.util.function.Supplier;

public class RaceCarEntity extends Boat {
	public static final int MAX_LEVEL = 5;

	private static final EntityDataAccessor<Integer> DATA_BRAND = SynchedEntityData.defineId(RaceCarEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_ENGINE = SynchedEntityData.defineId(RaceCarEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_WHEELS = SynchedEntityData.defineId(RaceCarEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_HANDLING = SynchedEntityData.defineId(RaceCarEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_CHASSIS = SynchedEntityData.defineId(RaceCarEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> DATA_STEERING = SynchedEntityData.defineId(RaceCarEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_DRIFT = SynchedEntityData.defineId(RaceCarEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_FUEL = SynchedEntityData.defineId(RaceCarEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> DATA_THROTTLE = SynchedEntityData.defineId(RaceCarEntity.class, EntityDataSerializers.FLOAT);
	private static final float FUEL_CAPACITY = 100.0f;

	private float steeringAngle;
	private boolean inputForward;
	private boolean inputBackward;
	private boolean inputLeft;
	private boolean inputRight;
	private int lastInputTick;

	public RaceCarEntity(EntityType<? extends RaceCarEntity> type, Level level) {
		this(type, level, new CarDropItemSupplier());
	}

	private RaceCarEntity(EntityType<? extends RaceCarEntity> type, Level level, CarDropItemSupplier dropItemSupplier) {
		super(type, level, dropItemSupplier);
		dropItemSupplier.car = this;
		this.blocksBuilding = true;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_BRAND, CarBrand.VELDORA.ordinal());
		builder.define(DATA_ENGINE, 1);
		builder.define(DATA_WHEELS, 1);
		builder.define(DATA_HANDLING, 1);
		builder.define(DATA_CHASSIS, 1);
		builder.define(DATA_STEERING, 0.0f);
		builder.define(DATA_DRIFT, 0.0f);
		builder.define(DATA_FUEL, FUEL_CAPACITY);
		builder.define(DATA_THROTTLE, 0.0f);
	}

	private static final class CarDropItemSupplier implements Supplier<Item> {
		private RaceCarEntity car;

		@Override
		public Item get() {
			return this.car == null ? ModItems.VELDORA_CAR : this.car.getBrand().dropItem();
		}
	}

	public CarBrand getBrand() {
		return CarBrand.byId(this.entityData.get(DATA_BRAND));
	}

	public void setBrand(CarBrand brand) {
		this.entityData.set(DATA_BRAND, brand.ordinal());
	}

	public int getCarLevel() {
		int total = getPartLevel(CarPart.ENGINE) + getPartLevel(CarPart.WHEELS)
				+ getPartLevel(CarPart.HANDLING) + getPartLevel(CarPart.CHASSIS);
		return Math.round(total / 4.0f);
	}

	public void setCarLevel(int level) {
		int clamped = Mth.clamp(level, 1, MAX_LEVEL);
		this.entityData.set(DATA_ENGINE, clamped);
		this.entityData.set(DATA_WHEELS, clamped);
		this.entityData.set(DATA_HANDLING, clamped);
		this.entityData.set(DATA_CHASSIS, clamped);
	}

	public int getPartLevel(CarPart part) {
		int level = switch (part) {
			case ENGINE -> this.entityData.get(DATA_ENGINE);
			case WHEELS -> this.entityData.get(DATA_WHEELS);
			case HANDLING -> this.entityData.get(DATA_HANDLING);
			case CHASSIS -> this.entityData.get(DATA_CHASSIS);
		};
		return Mth.clamp(level, 1, MAX_LEVEL);
	}

	public void setPartLevel(CarPart part, int level) {
		int clamped = Mth.clamp(level, 1, MAX_LEVEL);
		switch (part) {
			case ENGINE -> this.entityData.set(DATA_ENGINE, clamped);
			case WHEELS -> this.entityData.set(DATA_WHEELS, clamped);
			case HANDLING -> this.entityData.set(DATA_HANDLING, clamped);
			case CHASSIS -> this.entityData.set(DATA_CHASSIS, clamped);
		}
	}

	public float getSteeringAngle() {
		return this.entityData.get(DATA_STEERING);
	}

	public float getDriftAngle() {
		return this.entityData.get(DATA_DRIFT);
	}

	public int getFuelPercent() {
		return Mth.ceil(this.entityData.get(DATA_FUEL));
	}

	public void setFuelPercent(int percent) {
		this.entityData.set(DATA_FUEL, (float) Mth.clamp(percent, 0, 100));
	}

	public int getThrottlePercent() {
		return Math.round(this.entityData.get(DATA_THROTTLE) * 100.0f);
	}

	public void acceptDriverInput(ServerPlayer driver, RaceDriveInput input) {
		if (this.getControllingPassenger() != driver) {
			return;
		}
		this.inputForward = input.forward() && !input.backward();
		this.inputBackward = input.backward() && !input.forward();
		this.inputLeft = input.left() && !input.right();
		this.inputRight = input.right() && !input.left();
		this.lastInputTick = this.tickCount;
		setInput(this.inputRight, this.inputLeft, this.inputForward, this.inputBackward);
	}

	public static int upgradeCost(int currentLevel) {
		return currentLevel >= MAX_LEVEL ? 0 : Math.max(1, currentLevel) * 5;
	}

	public float maxSpeedBlocksPerTick() {
		float base = 0.85f + (getPartLevel(CarPart.ENGINE) - 1) * 0.16f;
		float wheelBonus = 1.0f + (getPartLevel(CarPart.WHEELS) - 1) * 0.025f;
		return base * wheelBonus * getBrand().topSpeedMul();
	}

	public float acceleration() {
		float engine = 0.075f + (getPartLevel(CarPart.ENGINE) - 1) * 0.016f;
		float wheels = 1.0f + (getPartLevel(CarPart.WHEELS) - 1) * 0.015f;
		return engine * wheels * getBrand().accelMul();
	}

	public float turnRate() {
		return (2.4f + getPartLevel(CarPart.HANDLING) * 0.45f) * getBrand().turnMul();
	}

	/** Horizontal speed in km/h (1 block = 1 meter). */
	public int speedKmh() {
		Vec3 motion = this.getDeltaMovement();
		double metersPerSecond = Math.sqrt(motion.x * motion.x + motion.z * motion.z) * 20.0;
		return (int) Math.round(metersPerSecond * 3.6);
	}

	@Override
	protected MovementEmission getMovementEmission() {
		return MovementEmission.NONE;
	}

	@Override
	public void destroy(ServerLevel level, Item ignoredDropItem) {
		if (isRemoved()) {
			return;
		}
		ItemStack drop = new ItemStack(getBrand().dropItem());
		CarItem.storePartLevels(drop, this);
		if (getCustomName() != null) {
			drop.set(DataComponents.CUSTOM_NAME, getCustomName());
		}
		kill(level);
		if (level.getGameRules().get(GameRules.ENTITY_DROPS)) {
			spawnAtLocation(level, drop);
		}
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		output.putInt("Brand", getBrand().ordinal());
		output.putInt("CarLevel", getCarLevel());
		output.putInt("EngineLevel", getPartLevel(CarPart.ENGINE));
		output.putInt("WheelsLevel", getPartLevel(CarPart.WHEELS));
		output.putInt("HandlingLevel", getPartLevel(CarPart.HANDLING));
		output.putInt("ChassisLevel", getPartLevel(CarPart.CHASSIS));
		output.putFloat("Fuel", this.entityData.get(DATA_FUEL));
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		setBrand(CarBrand.byId(input.getInt("Brand").orElse(0)));
		int legacyLevel = input.getInt("CarLevel").orElse(1);
		setCarLevel(legacyLevel);
		setPartLevel(CarPart.ENGINE, input.getInt("EngineLevel").orElse(legacyLevel));
		setPartLevel(CarPart.WHEELS, input.getInt("WheelsLevel").orElse(legacyLevel));
		setPartLevel(CarPart.HANDLING, input.getInt("HandlingLevel").orElse(legacyLevel));
		setPartLevel(CarPart.CHASSIS, input.getInt("ChassisLevel").orElse(legacyLevel));
		this.entityData.set(DATA_FUEL, Mth.clamp(input.getFloatOr("Fuel", FUEL_CAPACITY), 0.0f, FUEL_CAPACITY));
	}

	@Override
	public boolean canAddPassenger(Entity passenger) {
		return this.getPassengers().size() < 2;
	}

	@Override
	@Nullable
	public LivingEntity getControllingPassenger() {
		Entity first = this.getFirstPassenger();
		return first instanceof LivingEntity living ? living : null;
	}

	@Override
	public boolean isPickable() {
		return !this.isRemoved();
	}

	@Override
	public float maxUpStep() {
		return 1.0f;
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
		ItemStack held = player.getItemInHand(hand);
		if (tryUpgrade(player, held, CarPart.ENGINE)) {
			return InteractionResult.SUCCESS;
		}
		if (player.isSecondaryUseActive()) {
			return InteractionResult.PASS;
		}
		if (held.is(Items.COAL) && getFuelPercent() < FUEL_CAPACITY) {
			if (!this.level().isClientSide()) {
				held.consume(1, player);
				this.entityData.set(DATA_FUEL, Math.min(FUEL_CAPACITY, this.entityData.get(DATA_FUEL) + 25.0f));
				RaceMessages.send(player, Component.literal("Refueled to " + getFuelPercent() + "%."), true);
			}
			return InteractionResult.SUCCESS;
		}
		if (!this.level().isClientSide() && player.startRiding(this)) {
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.SUCCESS;
	}

	public boolean tryUpgrade(Player player, ItemStack held, CarPart part) {
		if (held.isEmpty() || !held.is(Items.DIAMOND)) {
			return false;
		}
		int level = getPartLevel(part);
		if (level >= MAX_LEVEL) {
			if (!this.level().isClientSide()) {
				RaceMessages.send(player, Component.literal("The " + part.displayName() + " is already maxed (level 5)."), true);
			}
			return true;
		}
		int cost = upgradeCost(level);
		if (held.getCount() < cost && !player.hasInfiniteMaterials()) {
			if (!this.level().isClientSide()) {
				RaceMessages.send(player, Component.literal("Need " + cost + " diamonds to upgrade " + part.displayName()
						+ " to level " + (level + 1) + "."), true);
			}
			return true;
		}
		if (this.level().isClientSide()) {
			return true;
		}
		if (!player.hasInfiniteMaterials()) {
			held.shrink(cost);
		}
		setPartLevel(part, level + 1);
		this.level().playSound(null, this.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 0.8f, 1.4f);
		RaceMessages.send(player, Component.literal("Upgraded " + part.displayName() + " to level " + (level + 1)
				+ " for " + cost + " diamonds. Car rating: level " + getCarLevel() + "."), true);
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide()) {
			spawnDriftSmoke();
			return;
		}
		boolean inputFresh = this.tickCount - this.lastInputTick <= 10;
		if (!inputFresh) {
			this.inputForward = false;
			this.inputBackward = false;
			this.inputLeft = false;
			this.inputRight = false;
			setInput(false, false, false, false);
		}
		boolean hasFuel = this.entityData.get(DATA_FUEL) > 0.0f;
		boolean forward = inputFresh && this.inputForward && hasFuel;
		boolean backward = inputFresh && this.inputBackward && hasFuel;
		boolean left = inputFresh && this.inputLeft;
		boolean right = inputFresh && this.inputRight;
		if (!hasFuel && (this.inputForward || this.inputBackward)) {
			setInput(right, left, false, false);
		}
		float throttle = forward ? 1.0f : backward ? -1.0f : 0.0f;
		this.entityData.set(DATA_THROTTLE, throttle);
		float targetSteering = left ? 0.45f : right ? -0.45f : 0.0f;
		this.steeringAngle = Mth.lerp(0.35f, this.steeringAngle, targetSteering);
		this.entityData.set(DATA_STEERING, this.steeringAngle);
		float speedFactor = Mth.clamp(speedKmh() / 90.0f, 0.0f, 1.0f);
		float drift = (left ? 1.0f : right ? -1.0f : 0.0f) * speedFactor * 0.32f;
		this.entityData.set(DATA_DRIFT, Mth.lerp(0.2f, this.entityData.get(DATA_DRIFT), drift));
		if (throttle != 0.0f) {
			this.entityData.set(DATA_FUEL, Math.max(0.0f, this.entityData.get(DATA_FUEL) - 0.02f));
		}
		if (this.isVehicle() && speedKmh() > 5 && this.tickCount % 8 == 0) {
			float pitch = 0.65f + Math.min(0.5f, speedKmh() / 160.0f);
			this.level().playSound(null, this.blockPosition(), SoundEvents.MINECART_RIDING,
					SoundSource.NEUTRAL, 0.35f, pitch);
		}
	}

	private void spawnDriftSmoke() {
		if (Math.abs(getDriftAngle()) < 0.08f || speedKmh() < 12 || this.tickCount % 2 != 0) {
			return;
		}
		double yaw = this.getYRot() * Mth.DEG_TO_RAD;
		double sideX = Math.cos(yaw) * 0.72;
		double sideZ = Math.sin(yaw) * 0.72;
		double rearX = Math.sin(yaw) * 0.9;
		double rearZ = -Math.cos(yaw) * 0.9;
		for (int side = -1; side <= 1; side += 2) {
			this.level().addParticle(ParticleTypes.SMOKE,
					this.getX() + rearX + sideX * side,
					this.getY() + 0.15,
					this.getZ() + rearZ + sideZ * side,
					0.0, 0.015, 0.0);
		}
	}

	public void onRadarCaught(Player driver, int limitKmh) {
		if (!(this.level() instanceof ServerLevel)) {
			return;
		}
		RaceStats stats = driver.getAttachedOrCreate(ModAttachments.RACE_STATS);
		int add = Math.max(1, 3 - getBrand().bountyShield());
		RaceStats updated = stats.withBounty(stats.bounty() + add);
		driver.setAttached(ModAttachments.RACE_STATS, updated);
		RaceMessages.send(driver, Component.literal("Radar hit! " + speedKmh() + " km/h in a " + limitKmh
				+ " zone. Bounty is now " + updated.bounty() + "."), false);
		this.level().playSound(null, BlockPos.containing(this.position()), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.BLOCKS, 1.0f, 0.6f);
	}
}
