package com.sacredmod.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class RaceCarModel extends EntityModel<RaceCarRenderState> {
	private final ModelPart frontLeftWheel;
	private final ModelPart frontRightWheel;
	private final ModelPart rearLeftWheel;
	private final ModelPart rearRightWheel;
	private final ModelPart steeringWheel;

	public RaceCarModel(ModelPart root) {
		super(root);
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

		box(root, "underbody", 0, 0, -7, 5, -11, 14, 2, 23, chassis);
		box(root, "body", 0, 0, -8, 2, -13, 16, 4, 26, chassis);
		box(root, "hood", 0, 0, -7, 0, -12, 14, 2, 11, chassis);
		box(root, "rear_deck", 0, 0, -7, 0, 2, 14, 2, 9, chassis);
		box(root, "front_bumper", 128, 192, -8, 2, -15, 16, 2, 2, chassis);
		box(root, "rear_bumper", 128, 192, -8, 2, 13, 16, 2, 2, chassis);
		box(root, "front_grille", 128, 0, -4, 3, -15.1f, 8, 2, 0.4f, chassis);
		box(root, "rear_diffuser", 128, 0, -5, 5, 14, 10, 1, 1, chassis);

		box(root, "roof", 0, 0, -5, -10, -4, 10, 2, 10, chassis);
		box(root, "windshield", 0, 64, -5, -7, -5, 10, 5, 1,
				PartPose.offsetAndRotation(0, 16, 0, (float) -Math.PI / 7, 0, 0));
		box(root, "rear_window", 0, 64, -5, -7, 4, 10, 5, 1,
				PartPose.offsetAndRotation(0, 16, 0, (float) Math.PI / 7, 0, 0));

		box(root, "left_a_pillar", 0, 0, -6, -10, -5, 1, 7, 1,
				PartPose.offsetAndRotation(0, 16, 0, (float) -Math.PI / 7, 0, 0));
		box(root, "right_a_pillar", 0, 0, 5, -10, -5, 1, 7, 1,
				PartPose.offsetAndRotation(0, 16, 0, (float) -Math.PI / 7, 0, 0));
		box(root, "left_c_pillar", 0, 0, -6, -10, 4, 1, 7, 1,
				PartPose.offsetAndRotation(0, 16, 0, (float) Math.PI / 7, 0, 0));
		box(root, "right_c_pillar", 0, 0, 5, -10, 4, 1, 7, 1,
				PartPose.offsetAndRotation(0, 16, 0, (float) Math.PI / 7, 0, 0));

		box(root, "dashboard", 128, 128, -5, -2, -5, 10, 2, 2, chassis);
		box(root, "driver_seat_cushion", 128, 128, -5, 0, 1, 4, 2, 6, chassis);
		box(root, "driver_seat_back", 128, 128, -5, -4, 5, 4, 5, 2, chassis);
		box(root, "passenger_seat_cushion", 128, 128, 1, 0, 1, 4, 2, 6, chassis);
		box(root, "passenger_seat_back", 128, 128, 1, -4, 5, 4, 5, 2, chassis);
		box(root, "center_console", 128, 0, -0.5f, -1, -1, 1, 2, 8, chassis);
		box(root, "steering_column", 128, 0, -3.5f, -3, -5, 1, 1, 3, chassis);
		box(root, "steering_wheel", 128, 64, -4.5f, -5, -5, 1, 4, 4, chassis);
		box(root, "left_door", 0, 0, -8, 1, -1, 1, 3, 10, chassis);
		box(root, "right_door", 0, 0, 7, 1, -1, 1, 3, 10, chassis);
		box(root, "left_mirror", 128, 192, -11, -5, -6, 3, 1, 2, chassis);
		box(root, "right_mirror", 128, 192, 8, -5, -6, 3, 1, 2, chassis);
		box(root, "left_headlight", 0, 192, -7, 1, -14, 4, 2, 1, chassis);
		box(root, "right_headlight", 0, 192, 3, 1, -14, 4, 2, 1, chassis);
		box(root, "left_taillight", 72, 192, -7, 1, 13, 4, 2, 1, chassis);
		box(root, "right_taillight", 72, 192, 3, 1, 13, 4, 2, 1, chassis);
		box(root, "left_exhaust", 128, 192, -6, 5, 15, 2, 1, 1, chassis);
		box(root, "right_exhaust", 128, 192, 4, 5, 15, 2, 1, 1, chassis);
		box(root, "spoiler", 0, 0, -9, -2, 10, 18, 1, 2, chassis);
		box(root, "spoiler_left_post", 128, 0, -7, -1, 10, 1, 3, 1, chassis);
		box(root, "spoiler_right_post", 128, 0, 6, -1, 10, 1, 3, 1, chassis);

		wheel(root, "wheel_fl", -9, -8);
		wheel(root, "wheel_fr", 9, -8);
		wheel(root, "wheel_bl", -9, 8);
		wheel(root, "wheel_br", 9, 8);
		return LayerDefinition.create(mesh, 256, 256);
	}

	private static void wheel(PartDefinition root, String name, float x, float z) {
		PartDefinition wheel = root.addOrReplaceChild(name,
				CubeListBuilder.create().texOffs(128, 64).addBox(-3, -4, -4, 6, 8, 8),
				PartPose.offset(x, 4, z));
		float rimX = x < 0 ? -3 : 2;
		wheel.addOrReplaceChild("rim",
				CubeListBuilder.create().texOffs(128, 192).addBox(rimX, -2, -2, 1, 4, 4),
				PartPose.ZERO);
		wheel.addOrReplaceChild("hub",
				CubeListBuilder.create().texOffs(128, 192).addBox(rimX + (x < 0 ? -0.1f : 0.1f), -1, -1, 1, 2, 2),
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
		this.steeringWheel.zRot = -state.steering * 3;
	}
}
