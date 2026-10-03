package com.sacredmod.car;

public enum CarPart {
	ENGINE("engine"),
	WHEELS("wheels"),
	HANDLING("handling"),
	CHASSIS("chassis");

	private final String displayName;

	CarPart(String displayName) {
		this.displayName = displayName;
	}

	public String displayName() {
		return displayName;
	}
}
