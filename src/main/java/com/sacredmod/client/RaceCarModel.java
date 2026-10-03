package com.sacredmod.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class RaceCarModel extends EntityModel<RaceCarRenderState> {
	private final ModelPart chassis;
	private final ModelPart frontLeftWheel;
	private final ModelPart frontRightWheel;
	private final ModelPart rearLeftWheel;
	private final ModelPart rearRightWheel;
	private final ModelPart steeringWheel;

	public RaceCarModel(ModelPart root) {
		super(root);
		this.chassis = root;
		this.frontLeftWheel = root.getChild("wheel_fl");
		this.frontRightWheel = root.getChild("wheel_fr");
		this.rearLeftWheel = root.getChild("wheel_bl");
		this.rearRightWheel = root.getChild("wheel_br");
		this.steeringWheel = root.getChild("steering_wheel");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartPose chassis = PartPose.offset(0, 16, 0);

		box(root, "underbody", 128, 224, -10, 4, -14, 20, 3, 28, chassis);
		box(root, "left_body_flank", 0, 32, -11, 0, -14, 2, 8, 28, chassis);
		box(root, "right_body_flank", 0, 32, 9, 0, -14, 2, 8, 28, chassis);
		box(root, "front_nose", 0, 32, -9, 0, -16, 18, 8, 10, chassis);
		box(root, "rear_body", 0, 32, -9, 0, 6, 18, 8, 10, chassis);
		box(root, "front_hood", 0, 72, -10, -1, -16, 20, 4, 13, chassis);
		box(root, "hood_scoop", 0, 112, -3, -3, -12, 6, 2, 5, chassis);
		box(root, "rear_deck", 64, 72, -10, -1, 4, 20, 4, 12, chassis);
		box(root, "engine_cover", 64, 112, -7, -3, 10, 14, 2, 5, chassis);

		box(root, "front_left_fender", 0, 160, -12, -2, -13, 3, 8, 11, chassis);
		box(root, "front_right_fender", 0, 160, 9, -2, -13, 3, 8, 11, chassis);
		box(root, "rear_left_fender", 64, 160, -12, -2, 3, 3, 8, 11, chassis);
		box(root, "rear_right_fender", 64, 160, 9, -2, 3, 3, 8, 11, chassis);
		box(root, "left_sill", 0, 192, -11, 4, -4, 2, 3, 10, chassis);
		box(root, "right_sill", 0, 192, 9, 4, -4, 2, 3, 10, chassis);
		box(root, "front_bumper", 128, 192, -12, 4, -18, 24, 3, 2, chassis);
		box(root, "rear_bumper", 128, 192, -12, 4, 16, 24, 3, 2, chassis);
		box(root, "front_grille", 192, 0, -6, 5, -18.2f, 12, 2, 0.4f, chassis);
		box(root, "front_splitter", 192, 16, -13, 7, -17, 26, 1, 4, chassis);
		box(root, "rear_diffuser", 192, 32, -10, 7, 16, 20, 1, 2, chassis);

		box(root, "cockpit_floor", 128, 128, -8, 5, -5, 16, 2, 13, chassis);
		box(root, "left_door", 128, 160, -10, -1, -5, 2, 7, 12, chassis);
		box(root, "right_door", 128, 160, 8, -1, -5, 2, 7, 12, chassis);
		box(root, "dashboard", 128, 128, -9, -4, -9, 18, 3, 3, chassis);
		box(root, "instrument_cluster", 160, 128, -8, -5, -8, 7, 2, 2, chassis);
		box(root, "center_console", 128, 160, -1, -5, -5, 2, 4, 10, chassis);
		box(root, "driver_seat_cushion", 160, 160, -8, 3, -1, 6, 2, 7, chassis);
		box(root, "driver_seat_back", 160, 160, -8, -2, 4, 6, 6, 2, chassis);
		box(root, "passenger_seat_cushion", 160, 160, 2, 3, -1, 6, 2, 7, chassis);
		box(root, "passenger_seat_back", 160, 160, 2, -2, 4, 6, 6, 2, chassis);
		box(root, "steering_column", 160, 192, -5, -3, -8, 1, 2, 4, chassis);
		steeringWheel(root);

		box(root, "windscreen", 128, 0, -8, -9, -11, 16, 5, 1,
				PartPose.offsetAndRotation(0, 16, 0, (float) -Math.PI / 8, 0, 0));
		box(root, "left_rollbar", 192, 64, -10, -10, -5, 2, 11, 2, chassis);
		box(root, "right_rollbar", 192, 64, 8, -10, -5, 2, 11, 2, chassis);
		box(root, "rollbar_top_front", 192, 80, -10, -10, -5, 20, 2, 2, chassis);
		box(root, "rollbar_top_rear", 192, 80, -10, -10, 5, 20, 2, 2, chassis);
		box(root, "rear_guard", 64, 192, -9, -5, 7, 18, 2, 2, chassis);
		box(root, "left_mirror", 192, 96, -14, -7, -10, 3, 2, 3, chassis);
		box(root, "right_mirror", 192, 96, 11, -7, -10, 3, 2, 3, chassis);

		box(root, "left_headlight", 224, 128, -9, 3, -16.5f, 6, 2, 1, chassis);
		box(root, "right_headlight", 224, 128, 3, 3, -16.5f, 6, 2, 1, chassis);
		box(root, "left_taillight", 224, 144, -9, 3, 15, 6, 2, 1, chassis);
		box(root, "right_taillight", 224, 144, 3, 3, 15, 6, 2, 1, chassis);
		box(root, "left_exhaust", 192, 112, -8, 7, 17, 3, 2, 1, chassis);
		box(root, "right_exhaust", 192, 112, 5, 7, 17, 3, 2, 1, chassis);
		box(root, "spoiler", 64, 32, -13, -5, 12, 26, 2, 4, chassis);
		box(root, "spoiler_left_post", 192, 64, -10, -4, 12, 2, 4, 2, chassis);
		box(root, "spoiler_right_post", 192, 64, 8, -4, 12, 2, 4, 2, chassis);

		wheel(root, "wheel_fl", -13, -10);
		wheel(root, "wheel_fr", 13, -10);
		wheel(root, "wheel_bl", -13, 10);
		wheel(root, "wheel_br", 13, 10);
		return LayerDefinition.create(mesh, 256, 256);
	}

	private static void steeringWheel(PartDefinition root) {
		PartDefinition steering = root.addOrReplaceChild("steering_wheel",
				CubeListBuilder.create().texOffs(160, 192).addBox(-0.8f, -0.8f, -0.8f, 1.6f, 1.6f, 1.6f),
				PartPose.offset(-5, 13, -6));
		steering.addOrReplaceChild("rim_top",
				CubeListBuilder.create().texOffs(160, 208).addBox(-4, -4, -0.5f, 8, 1, 1),
				PartPose.ZERO);
		steering.addOrReplaceChild("rim_bottom",
				CubeListBuilder.create().texOffs(160, 208).addBox(-4, 3, -0.5f, 8, 1, 1),
				PartPose.ZERO);
		steering.addOrReplaceChild("rim_left",
				CubeListBuilder.create().texOffs(160, 208).addBox(-4, -3, -0.5f, 1, 6, 1),
				PartPose.ZERO);
		steering.addOrReplaceChild("rim_right",
				CubeListBuilder.create().texOffs(160, 208).addBox(3, -3, -0.5f, 1, 6, 1),
				PartPose.ZERO);
		steering.addOrReplaceChild("spoke_left",
				CubeListBuilder.create().texOffs(160, 208).addBox(-3, -0.5f, -0.5f, 3, 1, 1),
				PartPose.ZERO);
		steering.addOrReplaceChild("spoke_right",
				CubeListBuilder.create().texOffs(160, 208).addBox(0, -0.5f, -0.5f, 3, 1, 1),
				PartPose.ZERO);
		steering.addOrReplaceChild("spoke_bottom",
				CubeListBuilder.create().texOffs(160, 208).addBox(-0.5f, 0, -0.5f, 1, 3, 1),
				PartPose.ZERO);
	}

	private static void wheel(PartDefinition root, String name, float x, float z) {
		PartDefinition wheel = root.addOrReplaceChild(name,
				CubeListBuilder.create().texOffs(128, 64).addBox(-3, -5, -5, 6, 10, 10),
				PartPose.offset(x, 21, z));
		float rimX = x < 0 ? -3.4f : 2.4f;
		wheel.addOrReplaceChild("rim",
				CubeListBuilder.create().texOffs(160, 64).addBox(rimX, -3.5f, -3.5f, 1, 7, 7),
				PartPose.ZERO);
		wheel.addOrReplaceChild("hub",
				CubeListBuilder.create().texOffs(184, 64).addBox(rimX + (x < 0 ? -0.1f : 0.1f), -1.5f, -1.5f, 1, 3, 3),
				PartPose.ZERO);
	}

	private static void box(PartDefinition root, String name, int u, int v,
			float x, float y, float z, float width, float height, float depth, PartPose pose) {
		root.addOrReplaceChild(name,
				CubeListBuilder.create().texOffs(u, v).addBox(x, y, z, width, height, depth),
				pose);
	}

	@Override
	public void setupAnim(RaceCarRenderState state) {
		super.setupAnim(state);
		float rotation = state.wheelRotation;
		this.frontLeftWheel.xRot = rotation;
		this.frontRightWheel.xRot = rotation;
		this.rearLeftWheel.xRot = rotation;
		this.rearRightWheel.xRot = rotation;
		this.frontLeftWheel.yRot = state.steering;
		this.frontRightWheel.yRot = state.steering;
		this.chassis.zRot = state.drift;
		this.steeringWheel.zRot = -state.steering * 3;
	}
}
