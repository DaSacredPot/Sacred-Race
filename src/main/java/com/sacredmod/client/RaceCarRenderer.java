package com.sacredmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import com.sacredmod.RaceMod;
import com.sacredmod.car.CarBrand;
import com.sacredmod.car.RaceCarEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import java.util.Locale;

public class RaceCarRenderer extends EntityRenderer<RaceCarEntity, RaceCarRenderState> {
	private final RaceCarModel model;

	public RaceCarRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.8f;
		this.model = new RaceCarModel(context.bakeLayer(ModEntityModelLayers.RACE_CAR));
	}

	@Override
	public RaceCarRenderState createRenderState() {
		return new RaceCarRenderState();
	}

	@Override
	public void extractRenderState(RaceCarEntity entity, RaceCarRenderState state, float tickProgress) {
		super.extractRenderState(entity, state, tickProgress);
		state.brandId = entity.getBrand().ordinal();
		state.carLevel = entity.getCarLevel();
		state.yRot = entity.getYRot();
	}

	@Override
	public void submit(RaceCarRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.translate(0.0f, 1.5f, 0.0f);
		poseStack.mulPose(new Matrix4f().rotationY((float) Math.toRadians(180.0f - state.yRot)));
		poseStack.scale(-1.0f, -1.0f, 1.0f);
		this.model.setupAnim(state);
		Identifier texture = textureFor(state.brandId);
		collector.submitModel(this.model, state, poseStack, texture, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
		super.submit(state, poseStack, collector, camera);
	}

	private static Identifier textureFor(int brandId) {
		CarBrand brand = CarBrand.byId(brandId);
		return RaceMod.id("textures/entity/car/" + brand.name().toLowerCase(Locale.ROOT) + ".png");
	}
}
