package com.sacredmod.network;

import com.sacredmod.RaceMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record RaceDriveInput(boolean forward, boolean backward, boolean left, boolean right) implements CustomPacketPayload {
	public static final Type<RaceDriveInput> TYPE = new Type<>(RaceMod.id("drive_input"));

	public static final StreamCodec<RegistryFriendlyByteBuf, RaceDriveInput> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, RaceDriveInput::forward,
			ByteBufCodecs.BOOL, RaceDriveInput::backward,
			ByteBufCodecs.BOOL, RaceDriveInput::left,
			ByteBufCodecs.BOOL, RaceDriveInput::right,
			RaceDriveInput::new
	);

	public static final RaceDriveInput NONE = new RaceDriveInput(false, false, false, false);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
