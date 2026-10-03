package com.sacredmod.car;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.Locale;

/** Fictional and parody marques available in the mod. */
public enum CarBrand {
	VELDORA("Veldora Motors", "Sprint-9", 1.00f, 1.00f, 1.00f, 0),
	NIMBUS("Nimbus Coil", "Arc Runner", 0.92f, 1.25f, 0.95f, 0),
	APEXLYN("Apexlyn", "Crownline", 0.96f, 1.05f, 1.20f, 0),
	DRIFTORA("Driftora", "Sidewinder", 0.90f, 1.10f, 1.35f, 0),
	ZEPHARA("Zephara Works", "Gale GT", 1.18f, 1.05f, 0.90f, 0),
	KORVEX("Korvex Heavy", "Bounty Hauler", 0.82f, 0.85f, 0.80f, 2),
	BERCEDES_MENZ("Bercedes Menz", "Silver Arrow", 1.08f, 0.98f, 1.04f, 0),
	BORCHE("Borche", "Vortex GT", 1.12f, 1.08f, 0.96f, 0),
	PMV("PMV", "M-3R", 1.02f, 1.10f, 1.12f, 0);

	private final String maker;
	private final String model;
	private final float topSpeedMul;
	private final float accelMul;
	private final float turnMul;
	private final int bountyShield;

	CarBrand(String maker, String model, float topSpeedMul, float accelMul, float turnMul, int bountyShield) {
		this.maker = maker;
		this.model = model;
		this.topSpeedMul = topSpeedMul;
		this.accelMul = accelMul;
		this.turnMul = turnMul;
		this.bountyShield = bountyShield;
	}

	public String maker() {
		return maker;
	}

	public String model() {
		return model;
	}

	public String itemId() {
		return name().toLowerCase(Locale.ROOT) + "_car";
	}

	public float topSpeedMul() {
		return topSpeedMul;
	}

	public float accelMul() {
		return accelMul;
	}

	public float turnMul() {
		return turnMul;
	}

	public int bountyShield() {
		return bountyShield;
	}

	public Component displayName() {
		return Component.literal(maker + " " + model);
	}

	public static CarBrand byId(int id) {
		CarBrand[] values = values();
		if (id < 0 || id >= values.length) {
			return VELDORA;
		}
		return values[id];
	}

	public ItemLike item() {
		return switch (this) {
			case VELDORA -> com.sacredmod.item.ModItems.VELDORA_CAR;
			case NIMBUS -> com.sacredmod.item.ModItems.NIMBUS_CAR;
			case APEXLYN -> com.sacredmod.item.ModItems.APEXLYN_CAR;
			case DRIFTORA -> com.sacredmod.item.ModItems.DRIFTORA_CAR;
			case ZEPHARA -> com.sacredmod.item.ModItems.ZEPHARA_CAR;
			case KORVEX -> com.sacredmod.item.ModItems.KORVEX_CAR;
			case BERCEDES_MENZ -> com.sacredmod.item.ModItems.BERCEDES_MENZ_CAR;
			case BORCHE -> com.sacredmod.item.ModItems.BORCHE_CAR;
			case PMV -> com.sacredmod.item.ModItems.PMV_CAR;
		};
	}

	public Item dropItem() {
		return item().asItem();
	}
}
