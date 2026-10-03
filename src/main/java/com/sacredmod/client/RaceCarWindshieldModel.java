package com.sacredmod.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public final class RaceCarWindshieldModel extends EntityModel<RaceCarRenderState> {
	public RaceCarWindshieldModel(ModelPart windshield) {
		super(windshield, RenderTypes::entityTranslucent);
	}

	@Override
	public void setupAnim(RaceCarRenderState state) {
		super.setupAnim(state);
		this.root.zRot = state.drift;
	}
}
