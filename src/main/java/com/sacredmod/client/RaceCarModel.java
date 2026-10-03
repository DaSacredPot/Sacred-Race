package com.sacredmod.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class RaceCarModel extends EntityModel<RaceCarRenderState> {
	public RaceCarModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(0, 0).addBox(-7, 2, -12, 14, 5, 22),
				PartPose.offset(0, 16, 0));
		root.addOrReplaceChild("cabin",
				CubeListBuilder.create().texOffs(0, 27).addBox(-6, 7, -4, 12, 5, 10),
				PartPose.offset(0, 16, 0));
		root.addOrReplaceChild("wheel_fl",
				CubeListBuilder.create().texOffs(50, 27).addBox(-2, -2, -2, 3, 4, 4),
				PartPose.offset(-8, 18, -8));
		root.addOrReplaceChild("wheel_fr",
				CubeListBuilder.create().texOffs(50, 27).addBox(-1, -2, -2, 3, 4, 4),
				PartPose.offset(8, 18, -8));
		root.addOrReplaceChild("wheel_bl",
				CubeListBuilder.create().texOffs(50, 27).addBox(-2, -2, -2, 3, 4, 4),
				PartPose.offset(-8, 18, 8));
		root.addOrReplaceChild("wheel_br",
				CubeListBuilder.create().texOffs(50, 27).addBox(-1, -2, -2, 3, 4, 4),
				PartPose.offset(8, 18, 8));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
